import unittest
import server

class NothingTestCase(unittest.TestCase):
	# Tests, essentially, nothing. Used to determine
	# that batching unit tests actually works.

	def test_nothing(self):
		self.assertTrue(True)

if __name__ == '__main__':
	unittest.main()
