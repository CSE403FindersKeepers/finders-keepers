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
			> curl -d '{"email":"test_email@email.com"}' -H "Content-Type:application/json" localhost:5000/mock/api/create_user
			(PUT)

			(DELETE)

"""
