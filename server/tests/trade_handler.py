# trade_handler deals with functionality related to trade
from flask import Flask
from flask import abort, jsonify, request
from db_handler import DBHandler

# create the app instance
app = Flask(__name__)

class TradeHandler():
	def __init__(self, db_handler):
		self.db_handler = db_handler

	def get_trade(self, trade_id):
		return None

	def get_trades(self, user_id):
		return None

	def start_trade(self, json):
		return None

	def accept_trade(self, json):
		return None

	def deny_trade(self, json):
		return None