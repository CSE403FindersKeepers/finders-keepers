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
		query = "SELECT * FROM TRADES WHERE id=" + str(trade_id)
		self.db_handler.cursor.execute(query)
		result = self.db_handler.cursor.fetchone()
		self.db_handler.cursor.fetchall()

		if result is None:
			return jsonify(error="Cannot find trade with id" + str(trade_id))

		return jsonify(trade=readTradeIntoObj(result))

	def get_trades(self, user_id):
		query = "SELECT * FROM TRADES WHERE "
		query += "recipientId=" + str(user_id) + " OR initiatorId=" + str(user_id)
		self.db_handler.cursor.execute(query)
		results = self.db_handler.cursor.fetchall()

		if result is None:
			return jsonify(trades=[])

		trades = []
		for result in results:
			trades.append(readTradeIntoObj(result))
		return jsonify(trades=trades)

	def start_trade(self, json):
		query = "INSERT INTO TRADE "
		query += "(initiatorId, recipientId, offeredItem1, offeredItem2, requestedItem1, requestedItem2, status) "
		
		query += "VALUES(" + str(json["initiator_id"]) + "," + str(json["recipient_id"])
		if len(json["offered_items"]) > 1:
			query += "," + json["offered_items"][0]
		if len(json["offered_items"]) > 2:
			query += "," + json["offered_items"][1]
		if len(json["requested_items"]) > 1:
			query += "," + json["requested_items"][0]
		if len(json["requested_items"]) > 2:
			query += "," + json["requested_items"][1]
		query += ")"
		self.db_handler.cursor.execute(query)
		self.db_handler.connection.commit()

		query = "SELECT * FROM TRADE WHERE "
		query += "initiatorId=" + str(json["initiator_id"]) + " AND "
		query += "recipientId=" + str(json["recipient_id"])
		self.db_handler.cursor.execute(query)
		result = self.db_handler.cursor.fetchone()
		self.db_handler.cursor.fetchall()
		return jsonify(result=result) # todo change to match spec

	def accept_trade(self, json):
		# check if json["user"] is recipient?
		query = "UPDATE TRADE SET status=ACCEPTED WHERE id=" + json["id"]
		self.db_handler.cursor.execute(query)
		self.db_handler.connection.commit()
		return jsonify(error=None) # todo change to match spec

	def deny_trade(self, json):
		# check if json["user"] is recipient?
		query = "UPDATE TRADE SET status=ACCEPTED WHERE id=" + json["id"]
		self.db_handler.cursor.execute(query)
		self.db_handler.connection.commit()
		return jsonify(error=None) # todo change to match spec

def readTradeIntoObj(result):
	initiator_id, recipient_id, offered_item1, offered_item2, requested_item1, requested_item2, status, trade_id = result
	offered_items = []
	offered_items.append(offered_item1) if offered_item1 is not None else None
	offered_items.append(offered_item2) if offered_item2 is not None else None
	requested_items = []
	requested_items.append(requested_item1) if requested_item1 is not None else None
	requested_items.append(requested_item2) if requested_item1 is not None else None

	return {
		"trade_id":trade_id, 
		"initiator_id":owner_id, 
		"recipient_id":recipient_id, 
		"offered_items":offered_items, 
		"requested_items":requested_items, 
		"status":status
	}

def concat_arr(**arr):
	return ",".join(arr)