from flask import abort, jsonify, request

"""
The SearchHandler class allows someone to search for users and items
based on there relevance, given a search query. It can match based
on tags or distance.
"""
class SearchHandler():
	def __init__(self, db_handler):
		self.db_handler = db_handler
		
	def get_users_within_radius(self, zipcode, radius):
		return