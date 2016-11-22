import unittest
import json
from server.server import app
from flask import Flask, jsonify

class TestSearchHandler(unittest.TestCase):
    @classmethod
    def setUpClass(self):
        app.testing = True
        self.app = app.test_client()

        result = self.app.post('/api/create_user', data=json.dumps({
            'email':'testsearchhandler1@email.com'
        }), content_type='application/json')
        data = json.loads(result.data)
        self.actor = data['user_id']
        self.app.put('/api/update_user', data=json.dumps({
            'user_id':self.actor,
            'zipcode': 97202 #portland
        }), content_type='application/json')

        result = self.app.post('/api/create_user', data=json.dumps({
            'email':'testsearchhandler2@email.com'
        }), content_type='application/json')
        data = json.loads(result.data)
        self.test_user_1m_away = data['user_id']
        self.app.put('/api/update_user', data=json.dumps({
            'user_id':self.test_user_1m_away,
            'zipcode': 97201 #portland
        }), content_type='application/json')

        result = self.app.post('/api/create_user', data=json.dumps({
            'email':'testsearchhandler3@email.com'
        }), content_type='application/json')
        data = json.loads(result.data)
        self.test_user_100m_away = data['user_id']
        self.app.put('/api/update_user', data=json.dumps({
            'user_id':self.test_user_100m_away,
            'zipcode': 98105 #seattle
        }), content_type='application/json')

        result = self.app.post('/api/create_user', data=json.dumps({
            'email':'testsearchhandler4@email.com'
        }), content_type='application/json')
        data = json.loads(result.data)
        self.test_user_same_zip = data['user_id']
        self.app.put('/api/update_user', data=json.dumps({
            'user_id':self.test_user_same_zip,
            'zipcode': 97202 #portland
        }), content_type='application/json')

    @classmethod
    def tearDownClass(self):
        self.app.delete('/api/delete_user/' + str(self.actor))
        self.app.delete('/api/delete_user/' + str(self.test_user_1m_away))
        self.app.delete('/api/delete_user/' + str(self.test_user_100m_away))
        self.app.delete('/api/delete_user/' + str(self.test_user_same_zip))

    def test_get_users_within_radius_same_zip(self):
        result = self.app.post('/api/get_users_within_radius', data=json.dumps({
            'zipcode':97202, #portland
            'radius':100
        }), content_type='application/json')
        self.assertEquals(result.status, '200 OK')
        data = json.loads(result.data)
        self.assertTrue('users' in data)
        self.assertEquals(len(data['users']), 2) # the test_user_1m_away might actually be in here too
        user1 = data['users'][0]['user_id']
        user2 = data['users'][1]['user_id']
        self.assertEquals(set([user1, user2]), set([self.actor, self.test_user_same_zip]))