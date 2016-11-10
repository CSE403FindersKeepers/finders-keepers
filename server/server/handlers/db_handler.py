from flaskext.mysql import MySQL

class DBHandler():
	def __init__(self, app):
		self.app = app
		self.my_sql = MySQL()
		
		# configure the app
		self.configure()

		# initialize the app
		self.my_sql.init_app(app)

		# $NOTE-yoont4: currently I've set this to return until we have a test
		# database to query. I don't want to risk muddying the production db

		# connect to the database and create a cursor
		self.connection = self.my_sql.connect()
		self.cursor = self.connection.cursor()

	def configure(self):
		# Configure the MySQL database connection settings here
		self.app.config['MYSQL_DATABASE_USER'] = 'finderskeepers'
		self.app.config['MYSQL_DATABASE_PASSWORD'] = '403student'
		self.app.config['MYSQL_DATABASE_DB'] = 'finderskeepersappdb'
		self.app.config['MYSQL_DATABASE_HOST'] = 'finderskeepersappdb.crxiugvetln0.us-west-2.rds.amazonaws.com'

	def sample_method(self):
		# using the connection cursor, call one of the stored procedures and pass in the 
		# required arguments
		self.cursor.callproc('sp_test_procedure', ('argument 1', 'argument 2'))

		# once the procedure is called, fetch the results of the procedure and store it
		data = self.cursor.fetchall()

		# just return the raw results to whatever handler called this and
		# parse, check, jsonify, etc. there
		return data

	def sample_query(self, query):
		# same principle as sample_method(), but with a query string
		self.cursor.execute(query)

		# you can also get some of the rows
		some_rows = cursor.fetchmany(size=2)

		# but you MUST fetch the rest before making a new query
		remaining_rows = cursor.fetchall()

	def get_user(self, userId):
		query = "SELECT * FROM USER WHERE id=" + str(userId)
		self.cursor.execute(query);
		user = self.cursor.fetchone()
		self.cursor.fetchall()
		return user