from flask import abort, jsonify, request
from zipcode_handler import zipcode

"""
The SearchHandler class allows someone to search for users and items
based on there relevance, given a search query. It can match based
on tags or distance.
"""
class SearchHandler():
	def __init__(self, db_handler):
		self.db_handler = db_handler
		
	def get_users_within_radius(self, zip, radius):
		zipstr = str(zip)
		while len(zipstr) < 5:
			zipstr = "0" + zipstr
			
		zip = zipcode.isequal(zipstr)
		
		# check if the zipcode has a record
		if zip is None:
			return None
			
		# get all zipcodes within the given radius
		zips = zipcode.isinradius((zip.lat, zip.lon), radius)
		# if the radius is zero, just search users with the same zip
		zips = [zip]
		
		if len(zips) is 0:
			return None
	
		# build a list of all users
		query = "SELECT * FROM USER WHERE zipcode=" + str(zips[0].zip)
		self.db_handler.cursor.execute(query);
		
		# ID, name, avatar_url, zipcode, email, wishlist
		users = self.db_handler.cursor.fetchall()
		
		results = []
		# build the json object
		for user in users:
			user_id, name, avatar, zip, email, wishlist = user
						
			# build the inventory
			query = "SELECT * FROM ITEM WHERE ownerId=" + str(user_id)
			self.db_handler.cursor.execute(query);
			items = self.db_handler.cursor.fetchall()		
			inventory = []
			if items is not None:
				for item in items:
					(item_id, owner_id, photo, tag1, tag2, description, title) = item
					inventory.append({
						"item_id": item_id,
						"owner_id": owner_id,
						"title": title,
						"description": description,
						"image_url": photo,
						"tags": [tag1, tag2]
					})
					
			# generate the json object
			user = {
				'user_id': user_id,
				'name': name,
				'avatar': avatar,
				'zipcode': zip,
				'email': email,
				'wishlist': [] if wishlist is None else wishlist.split(","),
				'inventory': inventory
			}
			
			# append the user to results
			results.append(user)
			
		return jsonify(users=results, error='')