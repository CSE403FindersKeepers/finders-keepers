from flask import jsonify, abort
from image_handler import upload_image, delete_image

class ItemHandler():
	def __init__(self, db_handler):
		self.db_handler = db_handler

	def get_all_items(self):
		query = "SELECT id FROM ITEM"
		self.db_handler.cursor.execute(query);
		items = self.db_handler.cursor.fetchall()
		return jsonify(items=items)

	def get_item(self, item_id):
		query = "SELECT * FROM ITEM WHERE id=" + str(item_id)
		self.db_handler.cursor.execute(query);
		result = self.db_handler.cursor.fetchone()
		self.db_handler.cursor.fetchall()		

		if result is None:
			abort(400, 'that item does not exist')
		else:
			(id, owner_id, photo, tag1, tag2, description, title) = result
			item = {
				"item_id":id, 
				"owner_id":owner_id, 
				"title":title, 
				"description":description, 
				"image_url":photo, 
				"tags":[tag1,tag2]
			}
			return jsonify(item=item)

	def create_item(self, json):
		# upload the image to S3 first and get the image URL
		item_image_url = upload_image(json['item_image']);

		query = "INSERT INTO ITEM (ownerId, title, description, photo, tag1, tag2) VALUES ("
		query += concat_params(json['owner_id'], json['title'], json['description'], item_image_url, json['tags'][0], json['tags'][1])
		query += ")"

		self.db_handler.cursor.execute(query)
		self.db_handler.connection.commit()
		
		query = "SELECT id FROM ITEM WHERE"
		query += "ownerId=" + str(json['owner_id'])
		query += "title=" + str(json['title'])
		item = self.db_handler.cursor.fetchone()
		self.db_handler.cursor.fetchall()
		return jsonify(item_id=item)

	def update_item(self, json):
		ATTRS = ['title', 'description', 'item_image']
		query = "UPDATE ITEM SET "
		query += "id=" + str(json['item_id'])
		if 'title' in json:
			query += ",title='" + json['title'] + "'"

		if 'description' in json:
			query += ",description='" + json['description'] + "'"

		if 'item_image' in json:
			# delete the old image first
			url_query = 'SELECT photo FROM ITEM WHERE id=' + str(json['item_id'])
			self.db_handler.cursor.execute(url_query)
			image_url = self.db_handler.cursor.fetchone()
			self.db_handler.cursor.fetchall()
			delete_image(image_url)

			# upload the new image
			new_url = upload_image(json['item_image'])

			query += ",photo='" + new_url + "'"

		if 'tags' in json:
			query += ",tag1='" + json['tags'][0] + "'"
			query += ",tag2='" + json['tags'][0] + "'"

		query += " WHERE id=" + str(json['item_id'])

		self.db_handler.cursor.execute(query)
		self.db_handler.connection.commit()
		
		return jsonify(errors=None) # TODO actually do an error

	def delete_item(self, item_id):
		query = "DELETE FROM ITEM WHERE id=" + str(item_id)
		self.db_handler.cursor.execute(query)
		self.db_handler.connection.commit()
		
		return jsonify(errors=None) # TODO actually do an error

	def get_inventory(self, owner_id):
		query = "SELECT * FROM ITEM WHERE ownerId=" + str(owner_id)
		self.db_handler.cursor.execute(query);
		items = self.db_handler.cursor.fetchall()		

		if items is None:
			abort(400, 'that user does not exist or does not own any items')
		else:
			arr = []
			for item in items:
				(item_id, owner_id, photo, tag1, tag2, description, title) = item
				arr.append({
					"item_id": item_id,
					"owner_id": owner_id,
					"title": title,
					"description": description,
					"image_url": photo,
					"tags": [tag1, tag2]
				})
			return jsonify(items=arr)

	def set_wishlist(self, json): #TODO need wishlist col in USER
		user_id, wishlist = json['user_id'], json['wishlist']
		query = "UPDATE USER (wishlist) VALUES ("
		query += ",".join(wishlist)
		query += ") WHERE id=" + str(user_id)
		
		self.db_handler.cursor.execute(query)
		self.db_handler.connection.commit()

		return jsonify(error=None) # TODO return error if it didn't work?

	def get_wishlist(self, user_id): #TODO need wishlist col in USER
		query = "SELECT id, wishlist FROM USER WHERE id=" + str(user_id)
		self.db_handler.cursor.execute(query);
		result = self.db_handler.cursor.fetchall()

		if result is None:
			abort(400, "That user DNE or doesn't have a wishlist")
		else:
			user_id, wishlist = result
			arr = wishlist.split(",")
			return jsonify(wishlist=arr)

def concat_params(*args):
	return ",".join(w if w.isnumeric() else "'{0}'".format(w) for w in args)

		
