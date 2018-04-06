'''
Match: 2017 China Software Cup
Topic: Commercial large data analysis technology based on WIFI probe.
School: CCUT(Changchun University Of Technology)
Date: 2017.03 - 2017-06
Team: teamName   --- Victors
	  teamLeader --- Jiahui Tang
	  teamMember --- Pengyue Zhao
	  teamMember --- Xinguang Guo
'''

class Counter(object):
	value = 0

	def add(self, x):
		Counter.value = Counter.value + x

		return Counter.value

	@staticmethod
	def add(x):
		Counter.value = Counter.value + x

		return Counter.value

	def minus(self, x):
		Counter.value = Counter.value - x

		return Counter.value
	
	@staticmethod
	def minus(x):
		Counter.value = Counter.value - x

		return Counter.value
		
	def multiply(self, x):
		Counter.value = Counter.value * x

		return Counter.value

	@staticmethod
	def multiply(x):
		Counter.value = Counter.value * x

		return Counter.value

	def divide(self, x):
		Counter.value = Counter.value / x

		return Counter.value

	@staticmethod
	def divide(x):
		Counter.value = Counter.value / x

		return Counter.value
