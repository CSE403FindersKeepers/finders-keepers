from flask import Flask
from flask import abort, jsonify, request
from db_handler import DBHandler

# create the app instance
app = Flask(__name__)

# create the MySQL database handler instance
#db_handler = DBHandler(app)

#@app.route('/api/get_user/<int:user_id>', methods=['GET'])
class UserHandler():
	def __init__(self, db_handler):
		self.db_handler = db_handler
		#self.db_handler.cursor.execute("CREATE TABLE USER(id int NOT NULL AUTO_INCREMENT, name varchar(255), photo varchar(8000), zipcode int, email varchar(255), PRIMARY KEY(id))")

	def get_user(self, user_id):
		# TODO: test
		#return "SELECT * FROM USER WHERE USER.id=" + str(user_id)
		self.db_handler.cursor.execute("SELECT * FROM USER WHERE USER.id=" + str(user_id))
		data = self.db_handler.cursor.fetchone()
		if data is None:
			return "User not found"
		else:
			return jsonify(user=data)

	#@app.route('/api/create_user', methods=['POST'])
	def create_user(self, json):
		self.db_handler.cursor.execute("INSERT INTO USER VALUES(default,'','',0,'" + json["email"] + "')")
		self.db_handler.cursor.execute("SELECT USER.id FROM USER WHERE USER.email='" + json['email'] + "'")
		data = self.db_handler.cursor.fetchall()
		if data is None:
				return "Error: Unable to create user"
		else:
			return jsonify(user_id=data[0][0])

	#@app.route('/api/update_user', methods=['POST'])
	def update_user(self,json):
		# For when CDN is up and running
		#url = image_handler.get_image(json['avatar'])
		self.db_handler.cursor.execute("UPDATE USER SET name='" + json['name'] + "', zipcode=" + str(json['zipcode']) + ", photo='http://i.imgur.com/0bGFP47.jpg', email='" + json["email"] + "' WHERE id =" + str(json['user_id']))
		# avatar is temporarily set to this default image: http://i.imgur.com/0bGFP47.jpg
		return jsonify(error=None)
