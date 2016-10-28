from flask import Flask
from flask import abort, jsonify, request

app = Flask(__name__)
@app.route("/")
def hello():
	return "Hello World!"

@app.route("/dummy/api/get_user/<int:user_id>", methods=["GET"])
def get_user(user_id):
	# just a dummy value for now
	return make_dummy_user(user_id)

@app.route("/dummy/api/new_user", methods=["POST"])
def new_user():
	# check for malformed json request
	if not request.json:
		abort(400)

	return jsonify(request.json)

@app.route("/dummy/api/update_user", methods=["PUT"])
def update_user():
	# check for malformed json request
	if not request.json:
		abort(400)
	
	return jsonify(request.json)

@app.route("/dummy/api/delete_user/<int:user_id>", methods=["DELETE"])
def delete_user(user_id):
	return make_dummy_user(user_id)

def make_dummy_user(user_id):
	return jsonify(
		user_id=user_id,
		name="test_user" + str(user_id),
		photo="pretend_this_is_a_photo",
		zipcode=12345,
		email="test_email" + str(user_id) + "@email.com")




if __name__ == "__main__":
	app.run()
