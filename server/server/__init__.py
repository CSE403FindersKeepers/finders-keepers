from flask import Flask
from flask import abort, jsonify, request
from handlers.response_objects import Item, User, Trade, Error
from handlers import user_handler
from handlers import item_handler
from handlers import trade_handler
from handlers.db_handler import DBHandler

# create the app instance
app = Flask(__name__)

# create the MySQL database handler instance
db_handler = DBHandler(app)

# these are all the mock API end points.
# ---------------------------------------------------------------------
# -------------------------- MOCK -------------------------------------
# ---------------------------------------------------------------------

# ------ USER ----- 

@app.route("/mock/api/get_user/<int:user_id>", methods=["GET"])
def mock_get_user(user_id):
	# just a dummy value for now
	return make_dummy_user(user_id)

@app.route("/mock/api/create_user", methods=["POST"])
def mock_create_user():
	json = request.get_json()

	# check for malformed json request
	if not is_valid_json(('email'), json):
		abort(400, 'invalid post data')

	return jsonify(user_id='9000')

# NOT DONE DOWN:
@app.route("/mock/api/update_user", methods=["PUT"])
def mock_update_user():
	# check for malformed json request
	if not request.json:
		abort(400)
	
	return jsonify(request.json)

@app.route("/mock/api/delete_user/<int:user_id>", methods=["DELETE"])
def mock_delete_user(user_id):
	return make_dummy_user(user_id)

# mock API methods
def make_dummy_user(user_id):
	return jsonify(
		user_id=user_id,
		zipcode=32000,
		name="test_user" + str(user_id),
		email="test_email" + str(user_id) + "@email.com",
		image_url="pretend_this_is_a_photo_URL",
		wishlist=["test_tag_1", "test_tag_2"],
		inventory=[
			{
			'item_id': 1000,
			'user_id': user_id,
			'name': 'test_item_1',
			'image_url': 'test_item_url_1',
			'tags': ['test_item_tag_1', 'test_item_tag_2']
			},
			{
			'item_id': 1001,
			'user_id': user_id,
			'name': 'test_item_2',
			'image_url': 'test_item_url_2',
			'tags': ['test_item_tag_3', 'test_item_tag_4']
			}
			]
		)

# ---------------------------------------------------------------------
# --------------------------- API -------------------------------------
# ---------------------------------------------------------------------

@app.route("/api/get_user/<int:user_id>", methods=["GET"])
def get_user(user_id):
	# check for malformed request
	if user_id <= 0:
		abort(400, "<get_user> only accepts positive user IDs")
	
	
	
	abort(400, "<get_user> is not accessable right now, sorry dawg")


def is_valid_json(expected_fields, json):
	# check that there is json data
	if not json:
		return False
	
	# validate the expected fields exist
	for field in expected_fields:
		if field not in json:
			return True
	
	return True


