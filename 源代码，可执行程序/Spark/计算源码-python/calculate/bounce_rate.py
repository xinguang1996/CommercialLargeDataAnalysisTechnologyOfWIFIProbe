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

class BounceRate(object):
	def __init__(self, resident_ave_list):
		self.resident_ave_list = resident_ave_list	
		#self.bounce = Counter()
		self.bounce = 0
		self.bounce_rate = 0
		self.BOUNCE = 5 * 60

	def __str__(self):
		return '功能：统计跳出率\n所需数据结构：resident_ave_list'

	__repr__ = __str__
	
	def get_value(self):
		for index, key in enumerate(self.resident_ave_list):
			if self.resident_ave_list[key] < self.BOUNCE:
				#self.bounce.add(1)
				self.bounce += 1
			elif self.resident_ave_list[key] >= self.BOUNCE:
				pass
			else:
				raise ValueError
			
		self.bounce_rate = self.bounce / float(len(self.resident_ave_list.items()))

	def print_value(self):
		print self.bounce_rate

	def return_value(self):
		return self.bounce_rate
