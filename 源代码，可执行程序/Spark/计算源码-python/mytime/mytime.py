#coding: utf-8

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

import time
import datetime

class MyTime(object):
	def __init__(self, ctime):
		self.ctime = ctime
		self.structtime = None
		self.timestamp = None	
		self.datetime = None
		self.struct = '%a %b %d %H:%M:%S %Y'

	def __str__(self):
		pass

	__repr__ = __str__

	def ctime_to_structtime(self):
		self.structtime = time.strptime(self.ctime, self.struct)	
		
		return self.structtime	

	def structtime_to_timestamp(self):
		self.timestamp = time.mktime(self.structtime)

		return self.timestamp

	def timestamp_to_datetime(self):
		self.datetime = datetime.datetime.utcfromtimestamp(self.timestamp)

		return self.datetime

	def ctime_to_timestamp(self):
		self.timestamp = time.mktime(time.strptime(self.ctime, self.struct))
		
		return self.timestamp

	def ctime_to_datetime(self):
		self.datetime = datetime.datetime.utcfromtimestamp(time.mktime(time.strptime(self.ctime, self.struct)))
		
		return self.datetime
	
	'''
	@staticmethod
	def ctime_to_structtime():
		self.structtime = time.strptime(self.ctime, self.struct)	
		
		return self.structtime	

	@staticmethod
	def structtime_to_timestamp():
		self.timestamp = time.mktime(self.structtime)

		return self.timestamp

	@staticmethod
	def timestamp_to_datetime():
		self.datetime = datetime.datetime(self.timestamp)

		return self.datetime

	@staticmethod
	def ctime_to_timestamp():
		self.timestamp = time.mktime(time.strptime(self.ctime, self.struct))
		
		return self.timestamp
	
	@staticmethod
	def ctime_to_datetime():
		self.datetime = datetime.datetime(time.mktime(time.strptime(self.ctime, self.struct)))
		
		return self.datetime
	'''

class LeapYear(object):
	def __init__(self, year):
		self.flag = None
		self.year = year

	def __str__(self):
		return 'Determine whether a leap year'

	__repr__ = __str__

	def judge(self):
		if (self.year % 4 == 0) and (self.year % 100 != 0):
			#是闰年
			self.flag = True

			return self.flag
		elif self.year % 400 == 0:
			#是闰年
			self.flag = True

			return self.flag
		else:
			#不是闰年
			self.flag = False

			return self.flag

	@staticmethod
	def judge(year):
		if (year % 4 == 0) and (year % 100 != 0):
			#是闰年
			return True
		elif year % 400 == 0:
			#是闰年
			return True
		else:
			#不是闰年
			return False

