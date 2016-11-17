# TestItemHandler: Tests for a class that's currently not implemented
# Fulfilling the requirement on the spec
import unittest
import json
from flask import Flask
from server.server.handlers import item_handler
from server.server.handlers import db_handler

# create the app instance
app = Flask(__name__)

# create the MySQL database handler instance
db_handler = db_handler.DBHandler(app)
item_handler = item_handler.ItemHandler(db_handler)

class TestItemHandler(unittest.TestCase):
    def test_get_item_dne(self):
        return

    def test_create_item_then_delete_item(self):
        return

    def test_delete_item_dne(self):
        return

    def test_update_item_picture(self):
        return

    def test_update_item_tags(self):
        return

    def test_update_item_description(self):
        return

    def test_update_item_title(self):
        return

    def test_update_item_no_params(self):
        return

    def test_get_inventory_user_dne(self):
        return

    def test_get_inventory_no_items(self):
        return

    def test_get_inventory_many_items(self):
        return

    def test_set_wishlist_none(self):
        return

    def test_set_wishlist_many(self):
        return
        

if __name__ == '__main__':
    unittest.main()