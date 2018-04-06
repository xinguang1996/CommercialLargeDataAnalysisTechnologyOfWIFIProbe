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

class ResidentTime(object):
	def __init__(self, resident_ave_list):
		self.resident_ave_list = resident_ave_list
		#self.ave_time_total = Counter()
		self.ave_time_total = 0
		self.resident_time = 0

	def __str__(self):
		return '功能：统计平均驻店时长\n所需数据结构：mac_time_list'

	__repr__ = __str__
	
	def get_value(self):
		for index, key in enumerate(self.resident_ave_list):
			self.ave_time_total += self.resident_ave_list[key]
			
		self.resident_time = self.ave_time_total / float(len(self.resident_ave_list.items()))
		
	def print_value(self):
		print self.resident_time
		
	def return_value(self):
		return self.resident_time
