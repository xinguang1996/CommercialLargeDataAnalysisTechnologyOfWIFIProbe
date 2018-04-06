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

class DeepRate(object):
	def __init__(self, resident_ave_list):
		self.resident_ave_list = resident_ave_list	
		#self.deep = Counter()
		self.deep = 0
		self.deep_rate = 0
		self.DEEP = 15 * 60

	def __str__(self):
		return '功能：统计深访率\n所需数据结构：resident_ave_list'

	__repr__ = __str__
	
	def get_value(self):
		for index, key in enumerate(self.resident_ave_list):
			if self.resident_ave_list[key] > self.DEEP:
				#self.deep.add(1)
				self.deep += 1
			elif self.resident_ave_list[key] <= self.DEEP:
				pass
			else:
				raise ValueError
			
		self.deep_rate = self.deep / float(len(self.resident_ave_list.items()))

	def print_value(self):
		print self.deep_rate

	def return_value(self):
		return self.deep_rate
