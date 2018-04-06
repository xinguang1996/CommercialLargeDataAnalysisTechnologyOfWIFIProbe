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

import sys
#sys.path.append('../')

#from base import Counter

def traffic_amount(es_rdd):
	def get_range(x):
		for value in x['data']:
			yield value['range']

	# Return format: [(time, range_amount), (time, range_amount), ..., (str, int)]
	def get_range_amount(x):
		acount = 0
		for i in x:
			acount += 1
		return acount

	traffic_rdd = es_rdd.mapValue(traffic_amount)

	return traffic_rdd.mapValue(get_range_amount).value().sum()