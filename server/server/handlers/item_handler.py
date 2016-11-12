from flask import jsonify, abort

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
				"user_id":owner_id, 
				"title":title, 
				"description":description, 
				"image_url":photo, 
				"tags":[tag1,tag2]
			}
			return jsonify(item=item)

	def create_item(self, json):
		query = "INSERT INTO ITEM (ownerId, title, description, photo, tag1, tag2) VALUES ("
		query += concat_params(json['user_id'], json['title'], json['description'], json['item_image'], json['tags'][0], json['tags'][1])
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
		ATTRS = ['title', 'description', 'image_url']
		query = "UPDATE ITEM SET "
		query += "id=" + str(json['item_id'])
		if 'title' in json:
			query += ",title='" + json['title'] + "'"
		if 'description' in json:
			query += ",description='" + json['description'] + "'"
		if 'image_url' in json:
			query += ",photo='" + json['image_url'] + "'"
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
					"user_id": owner_id,
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
	return ",".join("'{0}'".format(w) for w in args)

		