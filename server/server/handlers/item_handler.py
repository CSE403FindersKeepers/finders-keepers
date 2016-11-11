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
		item = self.db_handler.cursor.fetchone()
		self.db_handler.cursor.fetchall()		

		if item is None:
			abort(400, 'that item does not exist')
		else:
			(id, owner_id, photo, tag1, tag2, description, title) = item
			return jsonify(item_id=id, owner_id=owner_id, title=title, description=description, image_url=photo, tags=[tag1,tag2])

	def create_item(self, json):
		query = "INSERT INTO ITEM (ownerId, title, description, photo, tag1, tag2) VALUES ("
		query += concat_params(json['owner_id'], json['title'], json['description'], json['image_url'], json['tags'][0], json['tags'][1])
		query += ")"

		self.db_handler.cursor.execute(query)
		self.db_handler.connection.commit()
		
		query = "SELECT id FROM ITEM WHERE"
		query += "ownerId=" + str(json['owner_id'])
		query += "title=" + str(json['title'])
		item = self.db_handler.cursor.fetchone()
		self.db_handler.cursor.fetchall()
		return jsonify(item_id=item)
		

def concat_params(*args):
	return ",".join(w if w.isnumeric() else "'{0}'".format(w) for w in args)
		