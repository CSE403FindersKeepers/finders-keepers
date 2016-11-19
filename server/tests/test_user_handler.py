# TestTradeHandler: Tests for a class that's currently not implemented
# Fulfilling the requirement on the spec
import unittest
import json
from server.server import app
from flask import Flask, jsonify

class TestUserHandler(unittest.TestCase):
    def setUp(self):
        app.testing = True
        self.app = app.test_client()

    def test_get_user_invalid_id(self):
        result = self.app.get('/api/get_user/49834')
        self.assertTrue(result.data is not None)
        data = json.loads(result.data)
        self.assertEqual(data['user_id'], -1)

    def test_create_user_new_user(self):
        result = self.app.post('/api/create_user', data=json.dumps({
            'email':'test34@email.com'
        }), content_type='application/json')
        self.assertEqual(result.status, '200 OK')
        self.assertTrue(result.data is not None)
        data = json.loads(result.data)
        self.assertTrue('user_id' in data)
        user_id = data['user_id']
        result = self.app.get('/api/get_user/' + str(user_id))
        self.assertTrue(result.data is not None)
        data = json.loads(result.data)
        self.assertEqual(data['user']['email'], 'test34@email.com')
        
    def test_create_user_existing_user(self):
        return
        
    def test_update_user_photo(self):
        return

    def test_update_user_zipcode(self):
        return
        
    def test_update_user_all_null_fields(self):
        return
        
    def test_set_wishlist_to_empty(self):
        return

    def test_set_wishlist_to_one_item(self):
        return

    def test_set_wishlist_to_multiple_items(self):
        return

if __name__ == '__main__':
    unittest.main()