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


'''
只有周月维度

按与上次来访的时间间隔划分等级：

高活跃：1-2天
中活跃：3-4天
低活跃：5-6天
沉睡活跃：7天及以上

'''

class CustomerActive(object):
	def __init__(self, interval_ave_list):
		self.interval_ave_list = interval_ave_list
		#self.high = Counter()
		#self.mid = Counter()
		#self.low = Counter()
		#self.sleep = Counter()
		self.high = 0
		self.mid = 0
		self.low = 0
		self.sleep = 0

		#三个活跃度阈值
		self.HIGH = 60 * 60 * 24 * 2
		self.MID = 60 * 60 * 24 *4
		self.LOW = 60 * 60 * 24 * 6

	def __str__(self):
		return '功能：统计顾客活跃度\n所需数据结构：interval_ave_list'

	__repr__ = __str__
	
	def get_value(self):
		for index, key in enumerate(self.interval_ave_list):
			if self.interval_ave_list[key] < self.HIGH and self.interval_ave_list[key] > 0:
				#self.high.add(1)
				self.high += 1
			elif self.interval_ave_list[key] < self.MID:
				#self.mid.add(1)
				self.mid += 1
			elif self.interval_ave_list[key] < self.LOW:
				#self.low.add(1)
				self.low += 1
			elif self.interval_ave_list[key] >= self.LOW:
				#self.sleep.add(1)
				self.sleep += 1
			elif self.interval_ave_list[key] == 0:
				pass
			else:
				raise ValueError
	
	def print_value(self):
		print self.high, self.mid, self.low, self.sleep
