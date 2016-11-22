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

	def get_trade(self, trade_id):
		tradeQuery = "SELECT * FROM TRADES WHERE tradeId=" + str(trade_id)
		self.db_handler.cursor.execute(tradeQuery)
		tradeRes = self.db_handler.cursor.fetchone()
		self.db_handler.cursor.fetchall()
		if tradeRes is None:
			return jsonify(error="Cannot find trade with id " + str(trade_id))

		initiator = tradeRes[0]
		recipient = tradeRes[1]
		offered_items = self.get_item_array(trade_id, initiator)
		requested_items = self.get_item_array(trade_id, recipient)
		return jsonify(trade=format_trade_json(tradeRes, offered_items, requested_items))

	def get_trades(self, user_id):
		query = "SELECT * FROM TRADES WHERE "
		query += "recipientId=" + str(user_id) + " OR initiatorId=" + str(user_id)
		self.db_handler.cursor.execute(query)
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

	def start_trade(self, json):
		query = "INSERT INTO TRADES"
		query += " VALUES(" + str(json["initiator_id"]) + "," + str(json["recipient_id"])
		query += ", default, 'PENDING')"

		self.db_handler.cursor.execute(query)
		self.db_handler.connection.commit()

		query = "SELECT * FROM TRADES WHERE "
		query += "initiatorId=" + str(json["initiator_id"]) + " AND "
		query += "recipientId=" + str(json["recipient_id"])
		query += " ORDER BY tradeId DESC"
		self.db_handler.cursor.execute(query)
		result = self.db_handler.cursor.fetchone()
		self.db_handler.cursor.fetchall()

		if result is None:
			return jsonify(error="Start trade failed")

		trade_id = result[2]

		for item in json["offered_item_ids"]:
			query = "INSERT INTO TRADEITEMS"
			query += " VALUES (" + str(trade_id) + ", " + str(item) + ", " + str(json["initiator_id"]) + ")" 
			self.db_handler.cursor.execute(query)
			self.db_handler.connection.commit()

		for item in json["requested_item_ids"]:
			query = "INSERT INTO TRADEITEMS"
			query += " VALUES (" + str(trade_id) + ", " + str(item) + ", " + str(json["recipient_id"]) + ")" 
			self.db_handler.cursor.execute(query)
			self.db_handler.connection.commit()

		return jsonify(trade_id=result[2]) # todo change to match spec

	def accept_trade(self, json):
		# check if json["user"] is recipient?
		query = "UPDATE TRADES SET status='ACCEPTED' WHERE recipientId=" + str(json["user_id"])
		query += " AND tradeId = " + str(json["trade_id"])
		self.db_handler.cursor.execute(query)
		self.db_handler.connection.commit()
		return jsonify(error=None) # todo change to match spec

	def deny_trade(self, json):
		# check if json["user"] is recipient?
		query = "UPDATE TRADES SET status='DENIED' WHERE recipientId=" + str(json["user_id"])
		query += " AND tradeId = " + str(json["trade_id"])
		self.db_handler.cursor.execute(query)
		self.db_handler.connection.commit()
		return jsonify(error=None) # todo change to match spec

	def get_item_array(self, trade_id, user_id):
		query = "SELECT itemId FROM TRADEITEMS WHERE userId=" + str(user_id) + " AND tradeId=" + str(trade_id)
		items = []
		self.db_handler.cursor.execute(query)
		result = self.db_handler.cursor.fetchone()
		while result is not None:
			items.append(result[0])
			result = self.db_handler.cursor.fetchone()
		return items

	def delete_trade(self, trade_id): # should not be used, only for testing
		self.db_handler.cursor.execute("SELECT * FROM TRADES WHERE tradeId=" + str(trade_id))
		data = self.db_handler.cursor.fetchone()
		if data is None:
			return jsonify(error="Trade not found")
		else:
			image_handler.delete_image(data[2])
			query = "DELETE FROM TRADES WHERE tradeId=" + str(trade_id)
			self.db_handler.cursor.execute(query)
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