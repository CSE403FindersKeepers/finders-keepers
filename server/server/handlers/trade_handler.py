# trade_handler deals with functionality related to trade
from flask import Flask
from flask import abort, jsonify, request
from db_handler import DBHandler
import image_handler

# create the app instance
app = Flask(__name__)

class TradeHandler():
	def __init__(self, db_handler):
		self.db_handler = db_handler
	
	"""
	get_trade() takes a Trade ID and returns all trade information.
	"""
	def get_trade(self, trade_id):
		tradeQuery = "SELECT * FROM TRADES WHERE tradeId=%s"
		self.db_handler.cursor.execute(tradeQuery, str(trade_id))
		tradeRes = self.db_handler.cursor.fetchone()
		if tradeRes is None:
			return jsonify(error="Cannot find trade with id " + str(trade_id))

		initiator = tradeRes[0]
		recipient = tradeRes[1]
		offered_items = self.get_item_array(trade_id, initiator)
		requested_items = self.get_item_array(trade_id, recipient)
		return jsonify(trade=format_trade_json(tradeRes, offered_items, requested_items))

	"""
	get_trades() takes a User ID and retuns all trades that the user
	is associated with.
	"""
	def get_trades(self, user_id):
		query = "SELECT * FROM TRADES WHERE "
		query += "recipientId=%s OR initiatorId=%s"
		self.db_handler.cursor.execute(query, (str(user_id), str(user_id)))
		results = self.db_handler.cursor.fetchall()

		if results is None:
			return jsonify(trades=[])

		trades = []
		for result in results:
			initiator = result[0]
			recipient = result[1]
			trade_id = result[2]
			offered_items = self.get_item_array(trade_id, initiator)
			requested_items = self.get_item_array(trade_id, recipient)
			trades.append(format_trade_json(result, offered_items, requested_items))
		return jsonify(trades=trades)

	"""
	start_trade() creates a trade between an initiator and a recipient, made up of 
	items offered by the initiator and the recipient.
	"""
	def start_trade(self, json):
		query = "INSERT INTO TRADES"
		query += " VALUES(%s,%s"
		query += ", default, 'PENDING')"

		self.db_handler.cursor.execute(query, (str(json["initiator_id"]), str(json["recipient_id"])))
		self.db_handler.connection.commit()

		query = "SELECT * FROM TRADES WHERE "
		query += "initiatorId=%s AND "
		query += "recipientId=%s"
		query += " ORDER BY tradeId DESC"
		self.db_handler.cursor.execute(query, (str(json["initiator_id"]), str(json["recipient_id"])))
		result = self.db_handler.cursor.fetchone()
		self.db_handler.cursor.fetchall()

		if result is None:
			return jsonify(error="Start trade failed")

		trade_id = result[2]

		for item in json["offered_item_ids"]:
			query = "INSERT INTO TRADEITEMS"
			query += " VALUES (%s, %s, %s)" 
			self.db_handler.cursor.execute(query,(str(trade_id), str(item), str(json["initiator_id"])))
			self.db_handler.connection.commit()

		for item in json["requested_item_ids"]:
			query = "INSERT INTO TRADEITEMS"
			query += " VALUES (%s, %s, %s)" 
			self.db_handler.cursor.execute(query, (str(trade_id), str(item), str(json["recipient_id"])))
			self.db_handler.connection.commit()

		return jsonify(trade_id=result[2]) # todo change to match spec

	"""
	accept_trade() takes a user and a trade ID and accepts the trade if it is pending
	and does nothing if it has already been closed.
	"""
	def accept_trade(self, json):
		# check if json["user"] is recipient?
		query = "UPDATE TRADES SET status='ACCEPTED' WHERE recipientId=%s"
		query += " AND tradeId=%s"
		self.db_handler.cursor.execute(query, (str(json["user_id"]), str(json["trade_id"])))
		self.db_handler.connection.commit()
		return jsonify(error=None) # todo change to match spec

	"""
	deny_trade() takes a user and a trade ID and cancels the trade if it is pending
	and (?is this reasonable behavior?) does nothing if it has already been closed.
	"""
	def deny_trade(self, json):
		# check if json["user"] is recipient?
		query = "UPDATE TRADES SET status='DENIED' WHERE recipientId=%s"
		query += " AND tradeId=%s"
		self.db_handler.cursor.execute(query, (str(json["user_id"]), str(json["trade_id"])))
		self.db_handler.connection.commit()
		return jsonify(error=None) # todo change to match spec

	"""
	get_item_array() takes a user and a trade ID and returns a list of all the items that belong to
	the given trade and user.
	"""
	def get_item_array(self, trade_id, user_id):
		query = "SELECT itemId FROM TRADEITEMS WHERE userId=%s AND tradeId=%s"
		items = []
		self.db_handler.cursor.execute(query, (str(user_id), str(trade_id)))
		result = self.db_handler.cursor.fetchone()
		while result is not None:
			items.append(result[0])
			result = self.db_handler.cursor.fetchone()
		return items

	"""
	TEST METHOD, DO NOT USE IN PRODUCTION
	"""
	def delete_trade(self, trade_id): # should not be used, only for testing
		self.db_handler.cursor.execute("SELECT * FROM TRADES WHERE tradeId=%s", str(trade_id))
		data = self.db_handler.cursor.fetchone()
		if data is None:
			return jsonify(error="Trade not found")
		else:
			image_handler.delete_image(data[2])
			query = "DELETE FROM TRADES WHERE tradeId=%s"
			self.db_handler.cursor.execute(query, str(trade_id))
			self.db_handler.connection.commit()
			return jsonify(error=None)

def format_trade_json(result, offered_items, requested_items):
	initiator_id, recipient_id, trade_id, status = result

	return {
		"trade_id":trade_id, 
		"initiator_id":initiator_id, 
		"recipient_id":recipient_id, 
		"offered_items":offered_items, 
		"requested_items":requested_items, 
		"status":status
	}

def concat_arr(**arr):
	return ",".join(arr)
