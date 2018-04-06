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

import json
import datetime
import os

traffic_amount = 1000
#traffic_trend = 1

amount_of_store_amount = 200
#amount_of_store_trend = 2

into_rate_amount = 0.2
#into_rate_trend = 2

visiting_cycle = 5

customers_new = 100
customers_old = 100

customer_active_high = 50
customer_active_mid = 50
customer_active_low = 50
customer_active_sleep = 50

resident_time = 2

bounce_rate = 0.2

deep_rate = 0.3

#class FileExistsError(Exception):
#	pass

class FileExistsError(IOError):
	pass


def getYear(fn):
	def year():
		return datetime.datetime.now().year
	
	return year

def getMonth(fn):
	def month():
		return datetime.datetime.now().month

	return month

def getDay(fn):
	def day():
		return datetime.datetime.now().day

	return day

def getHour(fn):
	def hour():
		return datetime.datetime.now().hour

	return hour

def getMinute(fn):
	def minute():
		return datetime.datetime.now().minute

	return minute

@getYear
def year():
	pass

@getMonth
def month():
	pass

@getDay
def day():
	pass

@getHour
def hour():
	pass

@getMinute
def minute():
	pass

class MkJson(object):
	def __init__(self, traffic_amount, amount_of_store, into_rate, visiting_cycle, customer_new, customer_old, customer_active_high, customer_active_mid, customer_active_low, customer_active_sleep, resident_time, bounce_rate, deep_rate):
		self.traffic_amount = traffic_amount
		self.amount_of_store = amount_of_store
		self.into_rate = into_rate
		self.visiting_cycle = visiting_cycle
		self.customer_new = customer_new
		self.customer_old = customer_old
		self.customer_active_high = customer_active_high
		self.customer_active_mid = customer_active_mid
		self.customer_active_low = customer_active_low
		self.customer_active_sleep = customer_active_sleep
		self.resident_time = resident_time
		self.bounce_rate = bounce_rate
		self.deep_rate = deep_rate

		#the result json data
		self.json_dict = {}

	def __str__(self):
		return 'Make the result into a json, to save to the database(elasticsearch)!'

	__repr__ = __str__

	def get_json(self):
		self.json_dict = {'Traffic amount': ('{}').format(self.traffic_amount),
					'The amount of store': ('{}').format(self.amount_of_store),
					'Into the store rate': ('{}').format(self.into_rate),
					'Visiting cycle': ('{}').format(self.visiting_cycle),
					'The new and old customers': {
													'new': ('{}').format(self.customer_new),
													'old': ('{}').format(self.customer_old)
												},
					'Customer active': {
											'High activity': ('{}').format(self.customer_active_high), 
											'Mid activity': ('{}').format(self.customer_active_mid),
											'Low activity': ('{}').format(self.customer_active_low),
											'Sleep activity': ('{}').format(self.customer_active_sleep)
										},
					'The resident time': ('{}').format(self.resident_time),
					'Bounce rate': ('{}').format(self.bounce_rate),
					'Deep rate': ('{}').format(self.deep_rate)
					}	
		
		return self.json_dict

def main():
	#test = MkJson(1)
	test = MkJson(1000, 500, 0.5, 5, 200, 300, 100, 100, 100, 200, 5, 0.2, 0.3)
	
	test.get_json()
	
	print test.json_dict

if __name__ == '__main__':
	main()

'''
print(test_dict)
print(type(test_dict))
 
json_str = json.dumps(test_dict)
print(json_str)
print(type(json_str))

year = year()
month = month()
day = day()
hour = hour()
minute = minute()

#filename = '%04d' % year + '%02d' % month + '%02d' % day + '%02d' % hour + '.json'

#test
filename = '%04d' % year + '%02d' % month + '%02d' % day + '10' + '.json'

if not os.path.exists(filename):
	pass
else:
	raise FileExistsError('The JSON is existing!')

with open(filename, "w") as f:
	json.dump(test_dict, f)
	print("OK")

with open("./b.json", "w") as f:
	json.dump(json_str, f)
	print("OK")

'''
