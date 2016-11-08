from server import app

# run the app in debug mode
app.run(debug=True)

"""
	NOTES/INSTRUCTIONS

	Default Port = 5000

	How to test locally:
		1. launch the server:
			> python run_server.py
		2. send a curl request from another terminal
			(GET Request)
			> curl localhost:5000/mock/api/get_user/12345

			(POST)
			> curl -X POST -d '{"email":"test_email@email.com"}' -H "Content-Type:application/json" localhost:5000/mock/api/create_user
			(PUT)
			> curl -X PUT -d '{"user_id":1234, "name":"test_name", "zipcode":12345}' -H "Content-Type:application/json" localhost:5000/mock/api/update_user


			(DELETE)
			> curl -X DELETE localhost:5000/mock/api/delete_user/100

"""
