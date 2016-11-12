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

	def create_item(self, json):
		if len(json['tags']) < 1:
			abort(400, "Must give item at least one tag")

		item_image_url = upload_image(json['item_image']);
		query = ""
		if len(json['tags']) < 2:
			query = "INSERT INTO ITEM (ownerId, title, description, photo, tag1) VALUES ("
			query += concat_params(json['user_id'], json['title'], json['description'], item_image_url, json['tags'][0])
		else:	
			query = "INSERT INTO ITEM (ownerId, title, description, photo, tag1, tag2) VALUES ("
			query += concat_params(json['user_id'], json['title'], json['description'], item_image_url, json['tags'][0], json['tags'][1])
		query += ")"

		self.db_handler.cursor.execute(query)
		self.db_handler.connection.commit()
		
		query = "SELECT id, photo FROM ITEM WHERE"
		query += " ownerId=" + str(json['user_id'])
		query += " AND title='" + str(json['title']) + "'"
		self.db_handler.cursor.execute(query)
		result = self.db_handler.cursor.fetchone()
		self.db_handler.cursor.fetchall()

		if result is None:
			abort(400, "Create was unnsuccessful even though you provided the correct info")
		else:
			item, url = result
			return jsonify(item_id=item, item_image_url=url)

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

		if 'tags' in json and len(json['tags']) > 0 :
			query += ",tag1='" + json['tags'][0] + "'"
			if len(json['tags']) > 1:
				query += ",tag2='" + json['tags'][1] + "'"
			else:
				query += ",tag2=NULL"

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

	def set_wishlist(self, json): #TODO need wishlist col in USER
		user_id, wishlist = json['user_id'], json['wishlist']
		query = "UPDATE USER SET wishlist="
		query += "'" + ",".join(wishlist) + "'"
		query += " WHERE id=" + str(user_id)
		
		self.db_handler.cursor.execute(query)
		self.db_handler.connection.commit()

		return jsonify(error=None) # TODO return error if it didn't work?

	def get_wishlist(self, user_id): #TODO need wishlist col in USER
		query = "SELECT id, wishlist FROM USER WHERE id=" + str(user_id)
		self.db_handler.cursor.execute(query);
		result = self.db_handler.cursor.fetchone()
		self.db_handler.cursor.fetchall()

		if result is None:
			abort(400, "That user DNE or doesn't have a wishlist")
		else:
			user_id, wishlist = result
			arr = wishlist.split(",")
			return jsonify(wishlist=arr)

def concat_params(*args):
	return ",".join("'{0}'".format(w) for w in args)

def concat_arr(arr):
	return ",".join(arr)
