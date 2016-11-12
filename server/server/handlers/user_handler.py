from flask import Flask
from flask import abort, jsonify, request
from db_handler import DBHandler
import image_handler

# create the app instance
app = Flask(__name__)

class UserHandler():
	def __init__(self, db_handler):
		self.db_handler = db_handler

	# get_user: Takes in a user_id, returns a json with user_id, user_id = -1 if error
	def get_user(self, user_id):
		# TODO: test
		self.db_handler.cursor.execute("SELECT * FROM USER WHERE USER.id=" + str(user_id))
		data = self.db_handler.cursor.fetchone()
		if data is None:
			return jsonify(user_id=-1)
		else:
			query = "SELECT * FROM ITEM WHERE ownerId=" + str(user_id)
			self.db_handler.cursor.execute(query);
			items = self.db_handler.cursor.fetchall()		

			# TODO needs wishlistitem table
			# query = "SELECT tag FROM WISHLISTITEM WHERE userId=" + str(user_id)
			# self.db_handler.cursor.execute(query);
			# result = self.db_handler.cursor.fetchall()
			# wishitems = item for (item,) in result

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
				"wishlist": [], 
				"inventory": inventory
			}

			return jsonify(user=user)

	# create_user: Takes in a json containing an email, returns a user id. User id is -1 if there is an error
	def create_user(self, json):
		self.db_handler.cursor.execute("INSERT INTO USER VALUES(default,'','http://i.imgur.com/0bGFP47.jpg',0,'" + json["email"] + "', '')")
		self.db_handler.cursor.execute("SELECT USER.id FROM USER WHERE USER.email='" + json['email'] + "'")
		data = self.db_handler.cursor.fetchone()
		if data is None:
			return jsonify(user_id=-1)
		else:
			self.db_handler.connection.commit()
			return jsonify(user_id=data[0])

	# create_user: Takes in a json containing a user_id, name, zipcode, photo, and email returns a json with a null error field on success,
	# and an error field with a message upon failure
	def update_user(self,json):
		# Make sure user exists
		url = 'http://i.imgur.com/0bGFP47.jpg'
		self.db_handler.cursor.execute("SELECT USER.photo FROM USER WHERE USER.id=" + str(json['user_id']))
		data = self.db_handler.cursor.fetchone()
		if data is None:
			return jsonify(error="Error. User not found")
		else:
			url = data[0]
		# If avatar is null, leave the default value. Otherwise get a url
		if json['avatar'] is not None:
			url = image_handler.upload_image(json['avatar'])
		# If image handler returns nothing due to an error, set url to be default image
		if url is None:
			url = data[0]
		self.db_handler.cursor.execute("UPDATE USER SET name='" + json['name'] + "', zipcode=" + str(json['zipcode']) + ", photo='" + url + "' WHERE id =" + str(json['user_id']))
		self.db_handler.connection.commit()
		return jsonify(error=None)
