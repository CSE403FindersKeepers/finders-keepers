class Item:
	"""
	This defines an Item object with the following fields:
		id: integer
		name: string
		image_url: string
		tags: string[]
	"""

	def __init__(self, iid=-1, name="", image_url="", tags=[]):
		self.id = iid
		self.name = name
		self.image_url = image_url
		self.tags = tags

class User:
	"""
	This defines a User object with the following fields:
		id: integer
		zipcode: integer
		name: string
		email: string
		image_url: string
		wishlist: string[]
		inventory: item[]
	"""

	def __init__(
		self, uid=-1, zipcode=-1,
		name="", email="", image_url="",
		wishlist=[], inventory=[]
	):

		self.id = uid
		self.zipcode = zipcode
		self.name = name
		self.email = email
		self.image_url = image_url
		self.wishlist = wishlist
		self.inventory = inventory

	def add_inventory_item(self, item):
		self.inventory.append(item)

class Trade:
	"""
	This defines a Trade object with the following fields:
		id: integer
		initiator: User
		recipient: User
		requested_items: Item[]
		offered_items: Item[]
		status: string
		is_recipient: bool
	"""

	def __init__(
		self, tid=-1, initiator=None, recipient=None,
		requested_items=[], offered_items=[],
		status="", is_recipient=False
	):

		self.id = tid
		self.initiator = initiator
		self.recipient = recipient
		self.requested_items = requested_items
		self.offered_items = offered_items
		self.status = status
		self.is_recipient = is_recipient

class Error:
	"""
	This defines an error with the following fields:
		failure: bool
		error_message: strin
	"""

	def __init__(self, failure=False, error_message=""):
		self.failure = failure
		self.error_message = error_message
