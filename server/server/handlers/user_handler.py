from flask import Flask
from flask import abort, jsonify, request
from db_handler import DBHandler
import image_handler

# create the app instance
app = Flask(__name__)

# the UserHandler encapsulates DB operations relating to users
# this includes creating, getting, and updating users
class UserHandler():
	def __init__(self, db_handler):
		self.db_handler = db_handler

	# get_user: Takes in a user_id, returns a json with user_id, user_id = -1 if error
	def get_user(self, user_id):
		# TODO: test
		self.db_handler.cursor.execute("SELECT * FROM USER WHERE USER.id=%s",  str(user_id))
		data = self.db_handler.cursor.fetchone()
		if data is None:
			return jsonify(user_id=-1)
		else:
			query = "SELECT * FROM ITEM WHERE ownerId=%s"
			self.db_handler.cursor.execute(query, str(user_id));
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

			user = {
				"user_id": data[0],
				"name": data[1],
				"image_url": data[2],
				"zipcode": data[3],
				"email": data[4],
				"wishlist": (data[5].split(",") if data[5] else []), 
				"inventory": inventory
			}

			return jsonify(user=user)

	# create_user: Takes in a json containing an email, returns a user id. User id is -1 if there is an error
	def create_user(self, json):
		# Return the original user if already exists
		self.db_handler.cursor.execute("SELECT USER.id FROM USER WHERE USER.email=%s", json['email'])
 		data = self.db_handler.cursor.fetchone()
 		if data is None:
	 		self.db_handler.cursor.execute("INSERT INTO USER VALUES(default,'','http://i.imgur.com/0bGFP47.jpg',0,%s, '')", json['email'])
	 		self.db_handler.cursor.execute("SELECT USER.id FROM USER WHERE USER.email=%s", json['email'])
	 		data = self.db_handler.cursor.fetchone()
	 		if data is None:
	 			return jsonify(user_id=-1)
	 		else:
	 			self.db_handler.connection.commit()
	 	return jsonify(user_id=data[0])

	# create_user: Takes in a json containing a user_id, name, zipcode, photo, and email returns a json with a null error field on success,
	# and an error field with a message upon failure
	def update_user(self,json):
		if 'name' not in json and 'zipcode' not in json and 'avatar' not in json:
			abort(400, 'must pass at least 1 parameter besides user_id to update')
		
		# Make sure user exists
		url = 'http://i.imgur.com/0bGFP47.jpg'
		self.db_handler.cursor.execute("SELECT * FROM USER WHERE USER.id=%s", str(json['user_id']))
		data = self.db_handler.cursor.fetchone()
		if data is None:
			return jsonify(error="Error. User not found")
		else:
			name = data[1]
			url = data[2]
			zipcode = data[3]
		
		if 'name' in json:
			name = json['name']
		if 'zipcode' in json:
			zipcode = str(json['zipcode'])
		# If avatar is null, leave the default value. Otherwise get a url
		if 'avatar' in json and json['avatar'] is not None:
			url = image_handler.upload_image(json['avatar'])
		# If image handler returns nothing due to an error, set url to be default image
		if url is None:
			url = data[0]
			
		# build the query string based on what arguments are provided
		query = "UPDATE USER SET name=%s, zipcode=%s, photo=%s WHERE id=%s"
		
		self.db_handler.cursor.execute(query, (name, zipcode, url, str(json['user_id'])))
		self.db_handler.connection.commit()
		return jsonify(error=None)

	# delete_user: Takes in a user_id and deletes that user from the table.
	def delete_user(self, user_id):
		self.db_handler.cursor.execute("SELECT * FROM USER WHERE USER.id=%s", str(user_id))
		data = self.db_handler.cursor.fetchone()
		if data is None:
			return jsonify(error="User not found")
		else:
			image_handler.delete_image(data[2])
			query = "DELETE FROM USER WHERE id=%s"
			self.db_handler.cursor.execute(query, str(user_id))
			self.db_handler.connection.commit()
			return jsonify(error=None)