# TestTradeHandler: Tests for a class that's currently not implemented
# Fulfilling the requirement on the spec
import unittest
import json
from flask import Flask
from server.server.handlers import user_handler
from server.server.handlers import db_handler

# create the app instance
app = Flask(__name__)

# create the MySQL database handler instance
db_handler = db_handler.DBHandler(app)
user_handler = user_handler.UserHandler(db_handler)

class TestUserHandler(unittest.TestCase):
    def test_get_user_invalid_id(self):
        return

    def test_create_user_new_user(self):
        return
        
    def test_create_user_existing_user(self):
        return
        
    def test_update_user_photo(self):
        return

    def test_update_user_zipcode(self):
        return
        
    def test_update_user_all_null_fields(self):
        return
        
    def test_update_user_wishlist_empty(self):
        return

    def test_update_user_wishlist_one_item(self):
        return

    def test_update_user_wishlist_multiple_items(self):
        return

if __name__ == '__main__':
    unittest.main()