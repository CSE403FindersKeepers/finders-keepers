"""
This defines an Item object with the following fields:
	item_id: integer
	user_id: integer
	name: string
	image_url: string
	tags: string[]
"""
class Item:
	def __init__(self, item_id=-1, user_id=-1, name="", image_url="", tags=[]):
		self.item_id = item_id
		self.user_id = user_id
		self.name = name
		self.image_url = image_url
		self.tags = tags

"""
This defines a User object with the following fields:
	user_id: integer
	zipcode: integer
	name: string
	email: string
	image_url: string
	wishlist: string[]
	inventory: item[]
"""
class User:
	def __init__(
		self, user_id=-1, zipcode=-1,
		name="", email="", image_url="",
		wishlist=[], inventory=[]
	):
		self.user_id = user_id
		self.zipcode = zipcode
		self.name = name
		self.email = email
		self.image_url = image_url
		self.wishlist = wishlist
		self.inventory = inventory

	def add_inventory_item(self, item):
		self.inventory.append(item)

"""
This defines a Trade object with the following fields:
	trade_id: integer
	initiator_id: integer
	recipient_id: integer
	requested_items: Item[]
	offered_items: Item[]
	status: string
"""
class Trade:
	def __init__(
		self, trade_id=-1, initiator_id=-1, recipient_id=-1,
		requested_items=[], offered_items=[], status=""
	):
		self.trade_id = trade_id
		self.initiator_id = initiator_id
		self.recipient_id = recipient_id
		self.requested_items = requested_items
		self.offered_items = offered_items
		self.status = status

"""
This defines an error with the following fields:
	failure: bool
	error_message: strin
"""		
class Error:
	def __init__(self, failure=False, error_message=""):
		self.failure = failure
		self.error_message = error_message
