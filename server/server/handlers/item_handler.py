from flask import Flask
from flask import abort, jsonify, request
from db_handler import DBHandler

# create the app instance
app = Flask(__name__)

# create the MySQL database handler instance
db_handler = DBHandler(app)


@app.route('/api/get_item/<int:item_id>', methods=['GET'])
def get_item(item_id):
	return
	#db_handler.cursor.execute("SELECT id, tag1, tag2")
