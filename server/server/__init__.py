from flask import Flask
from flask import abort, jsonify, request
from handlers.response_objects import Item, User, Trade, Error
from handlers import user_handler
from handlers.item_handler import ItemHandler
from handlers import trade_handler
from handlers.db_handler import DBHandler

# create the app instance
app = Flask(__name__)

# create the MySQL database handler instance
db_handler = DBHandler(app)
user_handler = user_handler.UserHandler(db_handler)
item_handler = ItemHandler(db_handler)

# these are all the mock API end points.
# ---------------------------------------------------------------------
# -------------------------- MOCK -------------------------------------
# ---------------------------------------------------------------------

# ------ USER ------
# get_user
# create_user
# update_user
# delete_user
# * helper methods

@app.route('/mock/api/get_user/<int:user_id>', methods=['GET'])
def mock_get_user(user_id):
	if user_id > 9000:
		return jsonify(error='mock_get_user: OH NO, USERS OVER 9000 DON\'T EXIST!')

	# just a dummy value for now
	return jsonify(make_dummy_user(user_id))

@app.route('/mock/api/create_user', methods=['POST'])
def mock_create_user():
	json = request.get_json()

	# check for malformed json request
	if not is_valid_json(['email'], json):
		abort(400, 'mock_create_user: invalid POST data')

	return jsonify(user_id='9000')

@app.route('/mock/api/update_user', methods=['PUT'])
def mock_update_user():
	json = request.get_json()

	# check for malformed json request
	if not is_valid_json(['user_id', 'name', 'zipcode', 'avatar'], json):
		abort(400, 'mock_update_user: invalid PUT data')
	
	return jsonify(error=None)

@app.route('/mock/api/delete_user/<int:user_id>', methods=['DELETE'])
def mock_delete_user(user_id):
	if user_id > 9000:
		return jsonify(error='mock_delete_user: OH NO, USERS OVER 9000 DON\'T EXIST!')

	return jsonify(error=None)

@app.route('/mock/api/get_wishlist/<int:user_id>', methods=['GET'])
def mock_get_wishlist(user_id):
	if user_id > 9000:
		return jsonify(error='mock_delete_user: OH NO, USERS OVER 9000 DON\'T EXIST!')

	return jsonify(['cool_stuff', 'awful_stuff', 'a_banana_for_scale'])
	
@app.route('/mock/api/set_wishlist', methods=['PUT'])
def mock_set_wishlist():
	json = request.get_json()
	
	if not is_valid_json(['user_id', 'wishlist'], json):
		abort(400, 'mock_set_wishlist: invalid PUT data')
	
	return jsonify(error=None)

	
# helper method
def make_dummy_user(user_id):
	return {
		'user_id': user_id,
		'zipcode': 12345,
		'name': 'test_user_' + str(user_id),
		'email': 'test_email_' + str(user_id) + '@email.com',
		'image_url': 'https://upload.wikimedia.org/wikipedia/commons/thumb/b/b2/Hausziege_04.jpg/220px-Hausziege_04.jpg',
		'wishlist': ['test_tag_1', 'test_tag_2'],
		'inventory': [
			make_dummy_item(1, user_id),
			make_dummy_item(2, user_id)
			]
		}

# ------ ITEM ------
# get_item
# create_item
# update_item
# delete_item
# get_inventory
# * helper methods

@app.route('/mock/api/get_item/<int:item_id>', methods=['GET'])
def mock_get_item(item_id):
	if item_id > 9000:
		return jsonify(error='mock_get_item: OH NO, ITEM IDS OVER 9000 DON\'T EXIST!')

	return jsonify(make_dummy_item(item_id))


@app.route('/mock/api/create_item', methods=['POST'])
def mock_create_item():
	json = request.get_json()

	if not is_valid_json(['name', 'tags', 'item_image_data'], json):
		abort(400, 'mock_create_item: invalid POST data')

	return jsonify(item_id=100, item_url='i.imgur.com/ibsZi5R.png', error=None)

@app.route('/mock/api/update_item', methods=['PUT'])
def mock_update_item():
	json = request.get_json()

	if not is_valid_json([], json):
		abort(400, 'mock_update_item: invalid PUT data')

	return jsonify(error=None)

@app.route('/mock/api/delete_item/<int:item_id>', methods=['DELETE'])
def mock_delete_item(item_id):
	if item_id > 9000:
		return jsonify(error='mock_delete_item: OH NO, ITEM IDS OVER 9000 DON\'T EXIST!')

	return jsonify(error='')

@app.route('/mock/api/get_inventory/<int:user_id>', methods=['GET'])
def mock_get_inventory(user_id):
	if user_id > 9000:
		return jsonify(error='mock_get_inventory: OH NO, USERS OVER 9000 DON\'T EXIST!')

	return jsonify([
		make_dummy_item(1),
		make_dummy_item(2),
		make_dummy_item(3)
		])

# helper method
def make_dummy_item(item_id, user_id=1234):
	return {
		'item_id': item_id,
		'user_id': user_id,
		'name': 'test_item_' + str(item_id),
		'image_url': 'http://cdn.newsapi.com.au/image/v1/26a2476dc344c27ac3e7670c9df711b2?width=650',
		'tags': ['test_item_tag_1', 'test_item_tag_2']
		}

# ------ TRADE ------ 
# get_trade
# get_trades
# start_trade
# accept_trade
# deny_trade
# * helper methods

@app.route('/mock/api/get_trade/<int:trade_id>', methods=['GET'])
def mock_get_trade(trade_id):
	if trade_id > 9000:
		return jsonify(error='mock_get_trade: OH NO, TRADE IDS OVER 9000 DON\'T EXIST!')
	
	return jsonify(make_dummy_trade(trade_id))

@app.route('/mock/api/get_trades/<int:user_id>', methods=['GET'])
def mock_get_trades(user_id):
	if user_id > 9000:
		return jsonify(error='mock_get_trades: OH NO, USERS OVER 9000 DON\'T EXIST!')
	
	return jsonify([
		make_dummy_trade(1),
		make_dummy_trade(2),
		make_dummy_trade(3)
		])

@app.route('/mock/api/start_trade', methods=['POST'])
def mock_start_trade():
	json = request.get_json()

	if not is_valid_json(['user_id', 'recipient_id' ,'offered_items' ,'requested_items'], json):
		abort(400, 'mock_start_trade: invalid POST data')

	return jsonify(error=None)

@app.route('/mock/api/accept_trade', methods=['PUT'])
def mock_accept_trade():
	json = request.get_json()

	if not is_valid_json(['user_id', 'trade_id'], json):
		abort(400, 'mock_accept_trade: invalid PUT data')

	return jsonify(error=None)

@app.route('/mock/api/deny_trade', methods=['PUT'])
def mock_deny_trade():
	json = request.get_json()

	if not is_valid_json(['user_id', 'trade_id'], json):
		abort(400, 'mock_deny_trade: invalid PUT data')

	return jsonify(error=None)

# helper method
def make_dummy_trade(trade_id):
	return {
		'trade_id': trade_id,
		'initiator_id': 1000,
		'recipient_id': 2000,
		'requested_items': [
			make_dummy_item(1),
			make_dummy_item(2),
			make_dummy_item(3)
			],
		'offered_items': [
			make_dummy_item(1000)
			],
		'status': '',
	}


# ---------------------------------------------------------------------
# --------------------------- API -------------------------------------
# ---------------------------------------------------------------------

@app.route('/api/get_user/<int:user_id>', methods=['GET'])
def get_user(user_id):
	# check for malformed request
	if user_id <= 0:
		abort(400, '<get_user> only accepts positive user IDs')
	
	return user_handler.get_user(user_id)

@app.route('/api/create_user', methods=['POST'])
def create_user():
	json = request.get_json()
	# check for malformed json request
	if not is_valid_json(["email"], json):
		abort(400, 'mock_create_user: invalid POST data')
	return user_handler.create_user(json)

@app.route('/api/update_user', methods=['PUT'])
def update_user():
	json = request.get_json()

	# check for malformed json request
	if not is_valid_json(['user_id', 'name', 'zipcode', 'avatar'], json):
		abort(400, 'mock_update_user: invalid PUT data')
	
	return user_handler.update_user(json)

#----------------------------------------------------------------#

@app.route('/api/get_item/<int:item_id>', methods=['GET'])
def get_item(item_id):
	return item_handler.get_item(item_id)

@app.route('/api/create_item/', methods=['POST'])
def create_item():
	json = request.get_json()
	if not is_valid_json(('owner_id', 'title', 'description', 'tags', 'image_url'), json):
		abort(400, 'create_item: invalid PUT data')
	return item_handler.create_item(json)

@app.route('/api/get_all_items/', methods=['GET'])
def get_all_items():
	return item_handler.get_all_items()

#------------------------------ utility -------------------------------#

def is_valid_json(expected_fields, json):
	# check that there is json data
	if not json:
		return False
	
	# validate the expected fields exist
	for field in expected_fields:
		if field not in json:
			return False
	
	return True


if __name__ == "__main__":
	app.run()