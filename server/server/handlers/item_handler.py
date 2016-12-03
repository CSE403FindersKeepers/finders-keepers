from flask import jsonify, abort
from image_handler import upload_image, delete_image

"""
The ItemHandler class deals with all DB and API interactions regarding items and inventory.
"""
class ItemHandler():
	def __init__(self, db_handler):
		self.db_handler = db_handler

	# TODO: this is a test method
	def get_all_items(self):
		query = "SELECT id FROM ITEM"
		self.db_handler.cursor.execute(query);
		items = self.db_handler.cursor.fetchall()
		return jsonify(items=items)

	# get_item: uses an item ID to return the other information
	# about the item.
	def get_item(self, item_id):
		# query the database
		query = "SELECT * FROM ITEM WHERE id=%s"
		self.db_handler.cursor.execute(query, str(item_id));
		result = self.db_handler.cursor.fetchone()

		# handle querying failures
		if result is None:
			abort(400, 'that item does not exist')
		else:
			# parse the results
			(id, owner_id, photo, tag1, tag2, description, title) = result
			tags = []
			if tag1 is not None:
				tags.append(tag1)
			if tag2 is not None:
				tags.append(tag2)
			item = {
				"item_id":id, 
				"user_id":owner_id, 
				"title":title, 
				"description":description, 
				"image_url":photo, 
				"tags":tags
			}
			return jsonify(item=item)

	# create_item: takes the POST data, uploads a new item image, and publishes the items
	# to the database.
	def create_item(self, json):
		if len(json['tags']) < 1:
			abort(400, "Must give item at least one tag")

		# upload the item and retrieve its URL
		item_image_url = upload_image(json['item_image']);
		
		# build the query to add the new data
		query = ""
		if len(json['tags']) < 2:
			query = "INSERT INTO ITEM (ownerId, title, description, photo, tag1) VALUES (%s,%s,%s,%s,%s)"
			self.db_handler.cursor.execute(query, (json['user_id'], json['title'], json['description'], item_image_url, json['tags'][0]))
		else:	
			query = "INSERT INTO ITEM (ownerId, title, description, photo, tag1, tag2) VALUES (%s,%s,%s,%s,%s,%s)"
			self.db_handler.cursor.execute(query, (json['user_id'], json['title'], json['description'], item_image_url, json['tags'][0], json['tags'][1]))

		self.db_handler.connection.commit()
		
		# query for the item ID and image URL
		query = "SELECT id, photo FROM ITEM WHERE ownerId=%s AND title=%s"
		self.db_handler.cursor.execute(query, (str(json['user_id']), str(json['title'])))
		result = self.db_handler.cursor.fetchone()
		self.db_handler.cursor.fetchall()

		# handle database errors
		if result is None:
			abort(400, "Create was unsuccessful even though you provided the correct info")
		else:
			item, url = result
			return jsonify(item_id=item, item_image_url=url)

	# update_item: takes the PUT data, and updates a pre-existing image
	# depending on what fields are included.
	def update_item(self, json):
		# build the query
		self.db_handler.cursor.execute("SELECT * FROM ITEM WHERE ITEM.id=%s", str(json['item_id']))
		data = self.db_handler.cursor.fetchone()
		query = "UPDATE ITEM SET photo=%s, tag1=%s, tag2=%s, description=%s, title=%s WHERE id=%s"
		if data is None:
			return jsonify(error="Error. Item not found")
		else:
			url = data[2]
			tag1 = data[3]
			tag2 = data[4]
			description = data[5]
			title = data[6]

		if 'title' in json:
			title = json['title']

		if 'description' in json:
			description = json['description']

		if 'item_image' in json:
			# delete the old image first
			url_query = 'SELECT photo FROM ITEM WHERE id=%s'
			self.db_handler.cursor.execute(url_query, str(json['item_id']))
			image_url = self.db_handler.cursor.fetchone()
			self.db_handler.cursor.fetchall()
			delete_image(image_url)

			# upload the new image
			new_url = upload_image(json['item_image'])

			url = new_url

		if 'tags' in json and len(json['tags']) > 0 :
			tag1 = json['tags'][0]
			if len(json['tags']) > 1:
				tag2 = json['tags'][1]

		self.db_handler.cursor.execute(query, (url, tag1, tag2, description, title, str(json['item_id'])))
		self.db_handler.connection.commit()
		
		return jsonify(errors=None) # TODO actually do an error

	# delete_item: deletes an item based on the item ID given.
	def delete_item(self, item_id):
		query = "SELECT tradeId FROM TRADEITEMS WHERE itemId=%s"
		self.db_handler.cursor.execute(query, str(item_id))
		result = self.db_handler.cursor.fetchall()
		for row in result:
			trade_id = row[0]
			query = "UPDATE TRADES SET status='CANCELLED' WHERE tradeId=%s"
			self.db_handler.cursor.execute(query, str(trade_id))
			self.db_handler.connection.commit()
		query = "DELETE FROM TRADEITEMS WHERE itemId=%s"
		self.db_handler.cursor.execute(query, str(item_id))
		self.db_handler.connection.commit()

		query = "DELETE FROM ITEM WHERE id=%s"
		self.db_handler.cursor.execute(query, str(item_id))
		self.db_handler.connection.commit()
		
		return jsonify(errors=None) # TODO actually do an error

	# get_inventory: returns all the objects owned by a specific user, specified
	# by the user ID argument.
	def get_inventory(self, owner_id):
		query = "SELECT * FROM ITEM WHERE ownerId=%s"
		self.db_handler.cursor.execute(query, str(owner_id));
		items = self.db_handler.cursor.fetchall()		

		if items is None:
			abort(400, 'that user does not exist or does not own any items')
		else:
			# build the list of item objects to be returned
			arr = []
			for item in items:
				(item_id, owner_id, photo, tag1, tag2, description, title) = item
				tags = []
				if tag1 is not None:
					tags.append(tag1)
				if tag2 is not None:
					tags.append(tag2)
				arr.append({
					"item_id": item_id,
					"user_id": owner_id,
					"title": title,
					"description": description,
					"image_url": photo,
					"tags": tags
				})
			return jsonify(items=arr)

	# set_wishlist: given a user ID and array of wishlist tags, updates the wishlist tags
	def set_wishlist(self, json):
		user_id, wishlist = json['user_id'], json['wishlist']
		query = "UPDATE USER SET wishlist=%s WHERE id=%s"
		
		self.db_handler.cursor.execute(query, (",".join(wishlist), str(user_id)))
		self.db_handler.connection.commit()

		return jsonify(error=None) # TODO return error if it didn't work?

	# get_wishlist: given a user ID, returns a user's wishlist.
	def get_wishlist(self, user_id):
		query = "SELECT id, wishlist FROM USER WHERE id=%s"
		self.db_handler.cursor.execute(query, str(user_id));
		result = self.db_handler.cursor.fetchone()
		self.db_handler.cursor.fetchall()

		if result is None:
			abort(400, "Something went wrong in the DB")
		else:
			user_id, wishlist = result
			if wishlist:
				arr = wishlist.split(",")
				return jsonify(wishlist=arr)
			else:
				return jsonify(wishlist=[])

def concat_params(*args):
	return ",".join("'{0}'".format(w) for w in args)

def concat_arr(arr):
	return ",".join(arr)
