# TestTradeHandler: Tests for a class that's currently not implemented
# Fulfilling the requirement on the spec
import unittest
import json
from flask import Flask
from server.server.handlers import trade_handler
from server.server.handlers import db_handler

# create the app instance
app = Flask(__name__)

# create the MySQL database handler instance
db_handler = db_handler.DBHandler(app)
trade_handler = trade_handler.TradeHandler(db_handler)

class TestTradeHandler(unittest.TestCase):
#get_trade(self, trade_id)
#get_trades(self, user_id)
#start_trade(self, json)
#def accept_trade(self, json)
#def deny_trade(self, json)
    def test_start_trade(self):
        return
        
    def test_accept_trade(self):
        return
        
    def test_deny_trade(self):
        return
        
    def test_get_trade(self):
        return
        
    def test_get_trades(self):
        return

    def test_start_get_gets_accept_deny(self):
        return 
        trade1 =  {
            "initiator_id": -1,
            "recipient_id": -2,
            "offered_items": [1,2],
            "requested_items": [3,4]
        }
        trade2 =  {
            "initiator_id": -1,
            "recipient_id": -4,
            "offered_items": [5,6]
            "requested_items": [7,8]
        }
        # create json for trade 1 and trade 2
        json1 = json.dumps(trade1)
        json2 = json.dumps(trade2)
        # assert that trade1 is started successfully
        startTradeJson1 = trade_handler.start_trade(json1)
        self.assertFalse(self.is_valid_json(['error'], startTradeJson1))
        self.assertTrue(self.is_valid_json(['trade_id'], startTradeJson1))
        # assert that trade2 is started successfully
        startTradeJson2 = trade_handler.start_trade(json2)
        self.assertFalse(self.is_valid_json(['error'], startTradeJson2))
        self.assertTrue(self.is_valid_json(['trade_id'], startTradeJson2))
        # assert that getting trade2 is working correctly
        self.assertFalse(self.is_valid_json(['error'], trade_handler.get_trade(startTradeJson2['trade_id'])))
        self.assertTrue(self.is_valid_json(['trade_id', 'initiator_id', 'recipient_id', 'requested_items', 'offered_items', 'status'], trade_handler.get_trade(startTradeJson2['trade_id'])))
        # assert that getting all trades for user_id=-1 is working correctly
        self.assertFalse(self.is_valid_json(['error'], trade_handler.get_trades(-1)))
        self.assertTrue(self.is_valid_json(['trades'], trade_handler.get_trades(-1)))
        # make sure that accepting a trade is working correctly
        acceptance = {
            "user_id": -2,
            "trade_id": startTradeJson1['trade_id']
        }
        json3 = json.dumps(acceptance)
        self.assertTrue(self.is_valid_json(['error'], trade_handler.accept_trade(json3)))
        self.assertTrue(trade_handler.get_trade(startTradeJson1['trade_id'])['status'] is "Accepted")
        # make sure that denying a trade is working correctly
        deny = {
            "user_id": -4,
            "trade_id": startTradeJson2['trade_id']
        }
        json4 = json.dumps(deny)
        self.assertTrue(self.is_valid_json(['error'], trade_handler.deny_trade(json4)))
        self.assertTrue(trade_handler.get_trade(startTradeJson2['trade_id'])['status'] is "Denied")

    # makes sure that the returned json is correct
    def is_valid_json(self, expected_fields, json):
        # check that there is json data
        if not json:
            return False
        
        # validate the expected fields exist
        for field in expected_fields:
            if field not in json:
                return False
        
        return True

if __name__ == '__main__':
    unittest.main()