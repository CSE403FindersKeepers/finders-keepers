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
            'email':'testcreatenewuser@email.com'
        }), content_type='application/json')
        self.assertEqual(result.status, '200 OK')
        self.assertTrue(result.data is not None)
        data = json.loads(result.data)
        self.assertTrue('user_id' in data)
        user_id = data['user_id']
        result = self.app.get('/api/get_user/' + str(user_id))
        self.assertTrue(result.data is not None)
        data = json.loads(result.data)
        self.assertEqual(data['user']['email'], 'testcreatenewuser@email.com')
        # delete the user (not implemented yet)
        
    def test_create_user_existing_user(self):
        result1 = self.app.post('/api/create_user', data=json.dumps({
            'email':'testcreateexistinguser@email.com'
        }), content_type='application/json')
        result2 = self.app.post('/api/create_user', data=json.dumps({
            'email':'testcreateexistinguser@email.com'
        }), content_type='application/json')
        self.assertEqual(result2.status, '200 OK')
        self.assertTrue(result2.data is not None)
        data2 = json.loads(result2.data)
        self.assertTrue('user_id' in data2)
        data1 = json.loads(result1.data)
        self.assertEqual(data2['user_id'], data1['user_id'])
        # delete the user (not implemented yet)


    def test_update_user_zipcode(self):
        result = self.app.post('/api/create_user', data=json.dumps({
            'email':'testupdateuserzipcode@email.com'
        }), content_type='application/json')
        data = json.loads(result.data)
        user_id = data['user_id']
        result = self.app.put('/api/update_user', data=json.dumps({
            'user_id':user_id,
            'zipcode':98684
        }), content_type='application/json')
        self.assertEqual(result.status, '200 OK')
        result = self.app.get('/api/get_user/' + str(user_id))
        self.assertTrue(result.data is not None)
        data = json.loads(result.data)
        self.assertEqual(data['user']['zipcode'], 98684)

    def test_update_user_name(self):
        result = self.app.post('/api/create_user', data=json.dumps({
            'email':'testupdateusername@email.com'
        }), content_type='application/json')
        data = json.loads(result.data)
        user_id = data['user_id']
        result = self.app.put('/api/update_user', data=json.dumps({
            'user_id':user_id,
            'name':'Draco Malfoy'
        }), content_type='application/json')
        self.assertEqual(result.status, '200 OK')
        result = self.app.get('/api/get_user/' + str(user_id))
        self.assertTrue(result.data is not None)
        data = json.loads(result.data)
        self.assertEqual(data['user']['name'], 'Draco Malfoy')
        
    def test_update_user_all_null_fields(self):
        result = self.app.post('/api/create_user', data=json.dumps({
            'email':'testupdateusernull@email.com'
        }), content_type='application/json')
        data = json.loads(result.data)
        user_id = data['user_id']
        result = self.app.put('/api/update_user', data=json.dumps({
            'user_id':user_id
        }), content_type='application/json')
        self.assertEqual(result.status, '400 BAD REQUEST')
        
    def test_set_wishlist_to_empty(self):
        result = self.app.post('/api/create_user', data=json.dumps({
            'email':'testsetwishlistempty@email.com'
        }), content_type='application/json')
        data = json.loads(result.data)
        user_id = data['user_id']
        result = self.app.put('/api/set_wishlist', data=json.dumps({
            'user_id':user_id,
            'wishlist':[]
        }), content_type='application/json')
        result = self.app.get('/api/get_user/' + str(user_id))
        self.assertTrue(result.data is not None)
        data = json.loads(result.data)
        wishlist = data['user']['wishlist']
        self.assertEqual(len(wishlist), 0)

    def test_set_wishlist_to_one_item(self):
        result = self.app.post('/api/create_user', data=json.dumps({
            'email':'testsetwishlistoneitem@email.com'
        }), content_type='application/json')
        data = json.loads(result.data)
        user_id = data['user_id']
        result = self.app.put('/api/set_wishlist', data=json.dumps({
            'user_id':user_id,
            'wishlist':['cats']
        }), content_type='application/json')
        result = self.app.get('/api/get_user/' + str(user_id))
        self.assertTrue(result.data is not None)
        data = json.loads(result.data)
        wishlist = data['user']['wishlist']
        self.assertEqual(len(wishlist), 1)
        self.assertEqual(wishlist[0], 'cats')

    def test_set_wishlist_to_multiple_items(self):
        result = self.app.post('/api/create_user', data=json.dumps({
            'email':'testsetwishlistmanyitems@email.com'
        }), content_type='application/json')
        data = json.loads(result.data)
        user_id = data['user_id']
        result = self.app.put('/api/set_wishlist', data=json.dumps({
            'user_id':user_id,
            'wishlist':['i', 'like', 'cats']
        }), content_type='application/json')
        result = self.app.get('/api/get_user/' + str(user_id))
        self.assertTrue(result.data is not None)
        data = json.loads(result.data)
        wishlist = data['user']['wishlist']
        self.assertEqual(len(wishlist), 3)
        self.assertEqual(wishlist[0], 'i')
        self.assertEqual(wishlist[1], 'like')
        self.assertEqual(wishlist[2], 'cats')

if __name__ == '__main__':
    unittest.main()