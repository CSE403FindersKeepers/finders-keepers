import unittest
import json
from server.server import app
from flask import Flask, jsonify

class TestTradeHandler(unittest.TestCase):
    @classmethod
    def setUpClass(self):
        app.testing = True
        self.app = app.test_client()

        result = self.app.post('/api/create_user', data=json.dumps({
            'email':'testtradehandler1@email.com'
        }), content_type='application/json')
        data = json.loads(result.data)
        self.test_initiator = data['user_id']

        result = self.app.post('/api/create_user', data=json.dumps({
            'email':'testtradehandler2@email.com'
        }), content_type='application/json')
        data = json.loads(result.data)
        self.test_recipient = data['user_id']

        self.app.post('/api/create_item', data=json.dumps({
            'user_id':self.test_initiator,
            'title':'cactus',
            'description':'spikey and fits in window sill',
            'tags':['plants'],
            'item_image':'bar'
        }), content_type='application/json')
        result = self.app.get('/api/get_inventory/' + str(self.test_initiator))
        data = json.loads(result.data)
        self.test_initiator_inventory = [data['items'][0]['item_id']]

        self.app.post('/api/create_item', data=json.dumps({
            'user_id':self.test_recipient,
            'title':'roses',
            'description':'12 mini yellow roses',
            'tags':['flowers', 'plants'],
            'item_image':'bar'
        }), content_type='application/json')
        result = self.app.get('/api/get_inventory/' + str(self.test_recipient))
        data = json.loads(result.data)
        self.test_recipient_inventory = [data['items'][0]['item_id']]

    @classmethod
    def tearDownClass(self):
        for item_id in self.test_initiator_inventory:
            self.app.delete('/api/delete_item/' + str(item_id))
        for item_id in self.test_recipient_inventory:
            self.app.delete('/api/delete_item/' + str(item_id))
        self.app.delete('/api/delete_user/' + str(self.test_recipient))
        self.app.delete('/api/delete_user/' + str(self.test_initiator))

    def tearDown(self):
        result = self.app.get('/api/get_trades/' + str(self.test_initiator))
        data = json.loads(result.data)
        for trade in data['trades']:
            trade_id = trade['trade_id']
            self.app.delete('/api/delete_trade/' + str(trade_id))

    def test_start_trade(self):
        result = self.app.post('/api/start_trade', data=json.dumps({
            'initiator_id': self.test_initiator,
            'recipient_id': self.test_recipient,
            'offered_item_ids': self.test_initiator_inventory,
            'requested_item_ids': self.test_recipient_inventory
        }), content_type='application/json')
        self.assertEquals(result.status, '200 OK')
        data = json.loads(result.data)
        trade_id = data['trade_id']

        result = self.app.get('/api/get_trade/' + str(trade_id))
        self.assertEquals(result.status, '200 OK')
        data = json.loads(result.data)
        self.assertEquals(data['trade']['status'], 'PENDING')

    def test_get_trades_for_initiator(self):
        result = self.app.post('/api/start_trade', data=json.dumps({
            'initiator_id': self.test_initiator,
            'recipient_id': self.test_recipient,
            'offered_item_ids': self.test_initiator_inventory,
            'requested_item_ids': self.test_recipient_inventory
        }), content_type='application/json')
        self.assertEquals(result.status, '200 OK')
        data = json.loads(result.data)
        trade_id = data['trade_id']

        result = self.app.get('/api/get_trades/' + str(self.test_initiator))
        self.assertEquals(result.status, '200 OK')
        data = json.loads(result.data)
        self.assertTrue('trades' in data)
        self.assertEquals(len(data['trades']), 1)
        self.assertEquals(data['trades'][0]['trade_id'], trade_id)
        self.assertEquals(data['trades'][0]['status'], 'PENDING')

    def test_get_trades_for_recipient(self):
        result = self.app.post('/api/start_trade', data=json.dumps({
            'initiator_id': self.test_initiator,
            'recipient_id': self.test_recipient,
            'offered_item_ids': self.test_initiator_inventory,
            'requested_item_ids': self.test_recipient_inventory
        }), content_type='application/json')
        self.assertEquals(result.status, '200 OK')
        data = json.loads(result.data)
        trade_id = data['trade_id']

        result = self.app.get('/api/get_trades/' + str(self.test_recipient))
        self.assertEquals(result.status, '200 OK')
        data = json.loads(result.data)
        self.assertTrue('trades' in data)
        self.assertEquals(len(data['trades']), 1)
        self.assertEquals(data['trades'][0]['trade_id'], trade_id)
        self.assertEquals(data['trades'][0]['status'], 'PENDING')

    def test_accept_trade(self):
        result = self.app.post('/api/start_trade', data=json.dumps({
            'initiator_id': self.test_initiator,
            'recipient_id': self.test_recipient,
            'offered_item_ids': self.test_initiator_inventory,
            'requested_item_ids': self.test_recipient_inventory
        }), content_type='application/json')
        self.assertEquals(result.status, '200 OK')
        data = json.loads(result.data)
        trade_id = data['trade_id']

        result = self.app.put('/api/accept_trade', data=json.dumps({
            'user_id': self.test_recipient,
            'trade_id': trade_id
        }), content_type='application/json')
        self.assertEquals(result.status, '200 OK')

        result = self.app.get('/api/get_trade/' + str(trade_id))
        self.assertEquals(result.status, '200 OK')
        data = json.loads(result.data)
        self.assertEquals(data['trade']['status'], 'ACCEPTED')

    def test_deny_trade(self):
        result = self.app.post('/api/start_trade', data=json.dumps({
            'initiator_id': self.test_initiator,
            'recipient_id': self.test_recipient,
            'offered_item_ids': self.test_initiator_inventory,
            'requested_item_ids': self.test_recipient_inventory
        }), content_type='application/json')
        self.assertEquals(result.status, '200 OK')
        data = json.loads(result.data)
        trade_id = data['trade_id']

        result = self.app.put('/api/deny_trade', data=json.dumps({
            'user_id': self.test_recipient,
            'trade_id': trade_id
        }), content_type='application/json')
        self.assertEquals(result.status, '200 OK')

        result = self.app.get('/api/get_trade/' + str(trade_id))
        self.assertEquals(result.status, '200 OK')
        data = json.loads(result.data)
        self.assertEquals(data['trade']['status'], 'DENIED')

    def test_delete_item_cancels_trade(self):
        result = self.app.post('/api/start_trade', data=json.dumps({
            'initiator_id': self.test_initiator,
            'recipient_id': self.test_recipient,
            'offered_item_ids': self.test_initiator_inventory,
            'requested_item_ids': self.test_recipient_inventory
        }), content_type='application/json')
        self.assertEquals(result.status, '200 OK')
        data = json.loads(result.data)
        trade_id = data['trade_id']

        item_id = self.test_initiator_inventory[0]
        result = self.app.delete('/api/delete_item/' + str(item_id))
        self.assertEquals(result.status, '200 OK')

        result = self.app.get('/api/get_trade/' + str(trade_id))
        self.assertEquals(result.status, '200 OK')
        data = json.loads(result.data)
        self.assertEquals(data['trade']['status'], 'CANCELLED')
        self.assertEquals(len(data['trade']['offered_items']), 0)