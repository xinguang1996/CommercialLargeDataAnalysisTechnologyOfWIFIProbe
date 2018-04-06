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

import datetime
from elasticsearch import Elasticsearch

class PyElasticsearchHour(object):
	def __init__(self, json):
		self.time = datetime.datetime.now().strftime('%Y.%m.%d_%H')
		self.json = json
		self.index = 'res-{}'.format(self.time)
		self.doc_type = 'hour'
		self.es = Elasticsearch([{'host': '192.168.1.100', 'port': 9200}, 
					{'host': '192.168.1.101', 'port': 9200}, 
					{'host': '192.168.1.102', 'port': 9200}, 
					{'host': '192.168.1.103', 'port': 9200}])

	def __str__(self):
		return 'save the hour result data(json) to the elasticsearch(ELK cluster)'

	__repr__ = __str__

	def init(self):
		self.es.indices.create(index = self.index, ignore = [400, 404])

	def save(self):
		self.es.index(index = self.index,
					doc_type = self.doc_type,
					body = self.json)

class PyElasticsearchDay(PyElasticsearchHour):
	def __init__(self, json):
		self.time = datetime.datetime.now().strftime('%Y.%m.%d_%H')
		self.json = json
		self.index = 'res-{}'.format(self.time)
		self.doc_type = 'day'
		self.es = Elasticsearch([{'host': '192.168.1.100', 'port': 9200}, 
					{'host': '192.168.1.101', 'port': 9200}, 
					{'host': '192.168.1.102', 'port': 9200}, 
					{'host': '192.168.1.103', 'port': 9200}])

	def __str__(self):
		return 'save the daily result data(json) to the elasticsearch(ELK cluster)'

	__repr__ = __str__

class PyElasticsearchWeek(PyElasticsearchHour):
	def __init__(self, json):
		self.time = datetime.datetime.now().strftime('%Y.%m.%d_%H')
		self.json = json
		self.index = 'res-{}'.format(self.time)
		self.doc_type = 'week'
		self.es = Elasticsearch([{'host': '192.168.1.100', 'port': 9200}, 
					{'host': '192.168.1.101', 'port': 9200}, 
					{'host': '192.168.1.102', 'port': 9200}, 
					{'host': '192.168.1.103', 'port': 9200}])

	def __str__(self):
		return 'save the week result data(json) to the elasticsearch(ELK cluster)'

	__repr__ = __str__

class PyElasticsearchMonth(PyElasticsearchHour):
	def __init__(self, json):
		self.time = datetime.datetime.now().strftime('%Y.%m.%d_%H')
		self.json = json
		self.index = 'res-{}'.format(self.time)
		self.doc_type = 'month'
		self.es = Elasticsearch([{'host': '192.168.1.100', 'port': 9200}, 
					{'host': '192.168.1.101', 'port': 9200}, 
					{'host': '192.168.1.102', 'port': 9200}, 
					{'host': '192.168.1.103', 'port': 9200}])

	def __str__(self):
		return 'save the month result data(json) to the elasticsearch(ELK cluster)'

	__repr__ = __str__


	

def main():
	print datetime.datetime.now()

	json = {'The new and old customers': {'new': '200', 'old': '300'}, 'Visiting cycle': '5', 'Bounce rate': '0.2', 'The amount of store': '500', 'Deep rate': '0.3', 'Customer active': {'Sleep activity': '200', 'Mid activity': '100', 'High activity': '100', 'Low activity': '100'}, 'The resident time': '5', 'Traffic amount': '1000', 'Into the store rate': '0.5'}
	

	#index = '2017-5-11-15'
	#doc_type = 'hour'


	test = PyElasticsearchHour(json)

	try:
		test.init()
		test.save()
	except:
		pass

if __name__ == '__main__':
	main()
