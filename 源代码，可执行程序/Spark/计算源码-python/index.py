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

#import the necessary packages
from pyspark import SparkContext, SparkConf
import copy
import json
import re
import time
import datetime
import numpy as np
import pandas
import matplotlib
from elasticsearch import Elasticsearch


#import the packages of mine
from base import *
from mytime import *
from calculate import *
from mkjson import *
from pyelasticsearch import *
from myerror import *

now = datetime.datetime.now()
#now = datetime.datetime(2017, 5, 21, 22, 0, 0, 0) 

interval_hour = datetime.timedelta(hours = 1)
interval_day = datetime.timedelta(days = 1)
interval_week = datetime.timedelta(weeks = 1)
interval_month = datetime.timedelta(days = 30)

# set the SPARK
spark_conf = SparkConf().setAppName('WifiProbeBigData')
sc = SparkContext(conf = spark_conf)

es_read_conf = {
    "es.nodes": "192.168.1.10",
    "es.port": "9200",
    "es.resource": "sou/tz_1",
    "es.query": '{
        "range": {
            "@timestamp": {
                "gte": (now - interval_hour).strftime('%Y-%m-%dT%H:00:00'),
                "lt": now.strftime('%Y-%m-%dT%H:00:00')
                }
            }
        }'
    }

es_rdd = sc.newAPIHadoopRDD(
    inputFormatClass = "org.elasticsearch.hadoop.mr.EsInputFormat",
    keyClass = "org.apache.hadoop.io.NullWritable",
    valueClass = "org.elasticsearch.hadoop.mr.LinkedMapWritable",
    conf = es_read_conf
    )





'''
#define the config of the SPARK
#conf = {'es.resource': 'wifi-2017.04.08_20/logs', 'es.nodes': '192.168.1.104, 192.168.1.105, 192.168.1.106, 192.168.1.107', 'es.port': '9200'}
conf = {'es.resource': 'wifi-2017.04.08_20,wifi-2017.05.11_16', 'es.nodes': '192.168.1.104, 192.168.1.105, 192.168.1.106, 192.168.1.107', 'es.port': '9200'}

#SPARK_RDD
rdd=sc.newAPIHadoopRDD(inputFormatClass="org.elasticsearch.hadoop.mr.EsInputFormat",keyClass="org.apache.hadoop.io.NullWritable",valueClass="org.elasticsearch.hadoop.mr.LinkedMapWritable", conf=conf)

data2 = rdd.take(2)[0][1]
data = rdd
#data = rdd.take(2)[0][1]
data = str(data)

data = data.replace("'", "\"")
data = data.replace("u", "")
data = data.replace("(", "[")
data = data.replace(")", "]")

data = re.sub(r"\\[a-zA-Z0-9]{4}", r"", data)

#data = json.loads(data)

#print data2["@version"]
#print rdd.take(rdd.count())
#print data
#print data["@version"]
#print data["data"][0]["range"]


rdd_test = rdd.take(rdd.count())

'''

'''
#test the es-hadoop
conf = {'es.resource': 'wifi-2017.04.08_20/logs', 'es.nodes': '192.168.1.104, 192.168.1.105, 192.168.1.106, 192.168.1.107', 'es.port': '9200'}
rdd=sc.newAPIHadoopRDD(inputFormatClass="org.elasticsearch.hadoop.mr.EsInputFormat",keyClass="org.apache.hadoop.io.NullWritable",valueClass="org.elasticsearch.hadoop.mr.LinkedMapWritable", conf=conf)
rdd_test = rdd.take(rdd.count())
'''

now = datetime.datetime.now()
#now = datetime.datetime(2017, 5, 21, 22, 0, 0, 0) 

interval_hour = datetime.timedelta(hours = 1)
interval_day = datetime.timedelta(days = 1)
interval_week = datetime.timedelta(weeks = 1)
interval_month = datetime.timedelta(days = 30)


#init the Elasticsearch, connect to the db
es = Elasticsearch([{'host': '192.168.1.104' , 'port': 9200}])

#匹配elasticsearch索引
#match_hour = {'query': {'range': {'@timestamp': {'gte': (now - interval_hour).strftime(%Y-%m-%dT%H:%M:%S), 'lt': 'temp'}}}}
match_hour = {'sort': {'@timestamp': {'order': 'asc'}}, 'query': {'range': {'@timestamp': {'gte': (now - interval_hour).strftime('%Y-%m-%dT%H:00:00'), 'lt': now.strftime('%Y-%m-%dT%H:00:00')}}}}

match:_day = {'sort': {'@timestamp': {'order': 'asc'}}, 'query': {'range': {'@timestamp': {'gte': (now - interval_day).strftime('%Y-%m-%dT%H:00:00'), 'lt': now.strftime('%Y-%m-%dT%H:00:00')}}}}

match_week = {'sort': {'@timestamp': {'order': 'asc'}}, 'query': {'range': {'@timestamp': {'gte': (now - interval_week).strftime('%Y-%m-%dT%H:00:00'), 'lt': now.strftime('%Y-%m-%dT%H:00:00')}}}}

match_month = {'sort': {'@timestamp': {'order': 'asc'}}, 'query': {'range': {'@timestamp': {'gte': (now - interval_month).strftime('%Y-%m-%dT%H:00:00'), 'lt': now.strftime('%Y-%m-%dT%H:00:00')}}}}

#月统计
if now.day == 1 and now.hour == 0:
	#根据月份取相对应的整月数据索引
	if now.month in [1, 3, 5, 7, 8, 10, 12]:

		day = 31
		month = 12 if now.month == 1 else now.month - 1
		year = now.year - 1 if now.month == 1 else now.year
		
		start_time = datetime.datetime(year, month, 1, 0, 0, 0).strftime('%Y-%m-%dT%H:%M:%S')
		end_time = datetime.datetime(year, month, day, 23, 59, 59).strftime('%Y-%m-%dT%H:%M:%S')

		match_month = {'sort': {'@timestamp': {'order': 'asc'}}, 'query': {'range': {'@timestamp': {'gte': start_time, 'lt': end_time}}}}

	elif now.month in [4, 6, 9, 11]:
		
		day = 30
		month = now.month - 1
		year = now.year

		start_time = datetime.datetime(year, month, 1, 0, 0, 0).strftime('%Y-%m-%dT%H:%M:%S')
		end_time = datetime.datetime(year, month, day, 23, 59, 59).strftime('%Y-%m-%dT%H:%M:%S')

		match_month = {'sort': {'@timestamp': {'order': 'asc'}}, 'query': {'range': {'@timestamp': {'gte': start_time, 'lt': end_time}}}}
	
	elif now.month == 2:
		#平闰年的判断

		#pass	the function is in the base package or mytime package
		flag = judge(year)
	
		#True, 是闰年
		if flag == True:
			
			day = 29
			month = now.month - 1
			year = now.year

			start_time = datetime.datetime(year, month, 1, 0, 0, 0).strftime('%Y-%m-%dT%H:%M:%S')
			end_time = datetime.datetime(year, month, day, 23, 59, 59).strftime('%Y-%m-%dT%H:%M:%S')

			match_month = {'sort': {'@timestamp': {'order': 'asc'}}, 'query': {'range': {'@timestamp': {'gte': start_time, 'lt': end_time}}}}
	
		#False, 不是闰年（即平年）
		elif flag == False:
			
    			day = 28
			month = now.month - 1
			year = now.year

			start_time = datetime.datetime(year, month, 1, 0, 0, 0).strftime('%Y-%m-%dT%H:%M:%S')
			end_time = datetime.datetime(year, month, day, 23, 59, 59).strftime('%Y-%m-%dT%H:%M:%S')

			match_month = {'sort': {'@timestamp': {'order': 'asc'}}, 'query': {'range': {'@timestamp': {'gte': start_time, 'lt': end_time}}}}
			
			
	else:
		raise DateError


#获取各维度count
count_hour = es.count(index = ['sou-*'],
		      doc_type = 'logs',
		      body = match_hour)

count_day = es.count(index = ['sou-*'],
		      doc_type = 'logs',
		      body = match_day)

count_week = es.count(index = ['sou-*'],
		      doc_type = 'logs',
		      body = match_week)

count_month = es.count(index = ['sou-*'],
		      doc_type = 'logs',
		      body = match_month)

json_hour = es.search(index = ['sou-*'],
		      doc_type = 'logs',
		      body = match_hour,
		      size = count_hour['count'])

json_day = es.search(index = ['sou-*'],
		      doc_type = 'logs',
		      body = match_day,
		      size = count_hour['count'])

json_week = es.search(index = ['sou-*'],
		      doc_type = 'logs',
		      body = match_week,
		      size = count_hour['count'])

json_month = es.search(index = ['sou-*'],
		      doc_type = 'logs',
		      body = match_month,
		      size = count_hour['count'])


class Hour(object):
	def __init__(self, json):
		self.json = json

		self.traffic_amount = 0
		self.into_store_amount = 0
		self.into_store_rate = 0
		self.visiting_cycle = 0
		self.new = 0
		self.old = 0
		self.high = 0
		self.mid = 0
		self.low = 0
		self.sleep = 0
		self.resident_time = 0
		self.bounce_rate = 0
		self.deep_rate = 0

		#global rdd_test


		#customer distance(range[])
		self.range_list = []
		self.range_temp = []
		
		#customer mac(mac[])
		self.mac_list = []
		self.mac_temp = []

		# timestamp list
		self.time_list = []
		
		# 顾客最后时间记录表
		self.mac_lasttime_list = {}
		self.mac_lasttime_temp = {}
		
		# 本次数据中各顾客第一次出现的时间记录，用来测试info列表，作为初始的最后时间
		self.mac_firsttime_list = {}
		self.mac_firsttime_temp = {}

		# 客户驻店时长之和及驻店次数记录（根据lasttime_list进行更新）
		self.resident_list = {}
		self.resident_temp = {}
	
		# 客户平均访问间隔
		self.interval_list = {}
		self.interval_temp = {}

		# 所需时间段内每个客户的平均驻店时长
		# mac_time = {mac1:time, mac2:time......}
		self.resident_ave_list = {}
		self.resident_ave_temp = {}

		#平均访问间隔
		self.interval_ave_list = {}
		self.interval_ave_temp = {}

		#顾客活跃度在生成数据结构时同时获得
		self.high = 0
		self.mid = 0
		self.low = 0
		self.sleep = 0

	def __str__(self):
		return 'Calculation of the hourly dimension.'
		'''			
		function:
			my_flush: flush the temp lists
			init: initialization data
			calculate: calculate the result
		'''

	__repr__ = __str__
	'''
	#define the flush(flush all temp list)
	def my_flush(self):
		#flush
		self.range_temp = []
		self.mac_temp = []
	'''
	def init(self):
	#range_list/mac_list/time_list
		for i in self.json:
			#my_flush()
			self.range_temp = []
			self.mac_temp = []
			timeTemp = 0

			for j in range(len(i["_source"]["data"])):
				rangeTemp = i["_source"]["data"][j]["range"]
				self.range_temp.append(rangeTemp)
	
				macTemp = i["_source"]["data"][j]["mac"]
				self.mac_temp.append(macTemp)

			#the timestamp
			timeTemp = i["_source"]["time"]
			self.time_list.append(timeTemp)
			'''
			if timeTemp == 'Sat May 20 11:00:10 2017':
				print 'yes'
			else:
				print 'no'
			'''
			#test the number of temp list
			#print len(range_temp)
			#print len(mac_temp)
	
			#for k in range(len(i[1]["data"]))

			self.range_list.append(self.range_temp)
			self.mac_list.append(self.mac_temp)	

		
		'''
		########################
		#three list sort!
		index_dir = {}

		for index, val in enumerate(self.mac_list):
			#index_temp = {原索引：排序后正确的索引}
			index_temp = {index: None}
			
			#原索引与排序后真确索引的映射关系
			index_dir.update(index_temp)
		
		time_list = copy.deepcopy(self.time_list)
		print time_list

		for index, time in enumerate(time_list):
			time_list[index] = MyTime(time).ctime_to_timestamp()

		#根据计算机元年时间戳排序
		time_list.sort()

		print time_list
		print self.time_list

		for index, time in enumerate(time_list):
			print datetime.datetime.utcfromtimestamp(time).strftime('%a %b %d %H:%M:%S %Y')
			tmp = self.time_list.index(datetime.datetime.utcfromtimestamp(time).strftime('%a %b %d %H:%M:%S %Y'))
			

			index_temp[tmp] = index

		print index_temp
		

		#sort finish!
		#######################
		'''
	

		#mac_lasttime_list/mac_firsttime_list
		for index, val in enumerate(self.mac_list):
			# the 'index' is the index of list
			
			#if no func: enumerate
			#index = mac_list.index(i)

			time_temp = self.time_list[index]
	
			for i in val:
				self.mac_lasttime_temp = {i: time_temp}
				#mac_lasttime_temp = mac_lasttime_temp.fromkeys(i, time_temp)
		
				#test print
				#print i, time_temp	
	
				#print mac_lasttime_temp.items()		
	
				# update the last time list
				self.mac_lasttime_list.update(self.mac_lasttime_temp)

				#firsttime update
				if not self.mac_firsttime_list.has_key(i):
					self.mac_firsttime_list.update(self.mac_lasttime_temp)
				else:
					pass
			
			
		#resident_list/interval_list
		#{mac1:[时长之和，次数],......}/{mac1:[时间间隔之和，次数],......}
		for index, mac in enumerate(self.mac_firsttime_list):
			#self.resident_list.append([value, 0, 1]) #{mac1:[时长之和，次数], ......}
			self.resident_temp = {mac:[0, 1]}
			self.resident_list.update(self.resident_temp)
			
			self.interval_temp = {mac:[0, 1]}
			self.interval_list.update(self.interval_temp)

		for index, value in enumerate(self.mac_list):
			time_temp = self.time_list[index]
		
			#func timechange time_temp->datetime_temp
			testtime1 = MyTime(time_temp)

			datetime_temp = testtime1.ctime_to_datetime()

			print time_temp

			for i in value:
				#如果遍历到的mac在firsttime字典中
				if self.mac_firsttime_list.has_key(i):
						mac_firsttime = self.mac_firsttime_list[i]
				
						#print mac_firsttime

						#timechang ->type(datatime.datetime)
						testtime2 = MyTime(mac_firsttime)

						datetime_firsttime = testtime2.ctime_to_datetime()

						interval = (datetime_temp - datetime_firsttime).seconds
						#print 'interval:',interval
						#print type(interval)
						if interval < 60:
							#时间差累加在驻店时长之和上
							#change

							#index_in_info = self.mac_firsttime_list.keys().index(i)
							#self.mac_info_list[index_in_info][1] += (datetime_temp - datetime_firsttime).seconds
							
							self.resident_temp = {i:[self.resident_list[i][0] + interval, self.resident_list[i][1]]}
							self.resident_list.update(self.resident_temp)


						else:
							#print 'aaaaaaaaa'
							'''
							#活跃度
							if interval < (60 * 60 * 24 * 2):
								self.high += 1
							elif interval < (60 * 60 * 24 * 4):
								self.mid += 1
							elif interval < (60 * 60 * 24 * 6):
								self.low += 1
							elif interval >= (60 * 60 * 24 * 6):
								self.sleep += 1
							else:	
								raise ValueError
							'''

							#index_in_info = self.mac_firsttime_list.keys().index(i)
							#self.mac_info_list[index_in_info][2] += 1
							self.resident_temp = {i:[self.resident_list[i][0], self.resident_list[i][1] + 1]}	
							self.resident_list.update(self.resident_temp)
							
							self.interval_temp = {i:[self.interval_list[i][0] + interval, self.interval_list[i][1]]}
							self.interval_temp.update(self.interval_temp)
						
						#update the lasttime 
						self.mac_firsttime_temp = {i: time_temp}
						self.mac_firsttime_list.update(self.mac_firsttime_temp)

		#resident_ave_list/interval_ave_list
		#所ss需时间段内每个客户平均驻店时长
		#相关数据结构: resident_list[]/interval_list[]
		#resident_list = {mac1:[时长，次数], mac2:[,]......}

		for index, key in enumerate(self.resident_list):
			#mac = i[0]
			#ave_time = i[1] / i[2]
	
			#self.mac_time_temp = [mac, ave_time]

			#self.mac_time_list.append(self.mac_time_temp)
			
			resident_ave = self.resident_list[key][0] / float(self.resident_list[key][1])
			
			self.resident_ave_temp = {key: resident_ave}
			self.resident_ave_list.update(self.resident_ave_temp)

		for index, key in enumerate(self.interval_list):
			interval_ave = self.interval_list[key][0] / float(self.interval_list[key][1])
			
			self.interval_ave_temp = {key: interval_ave}
			self.interval_ave_list.update(self.interval_ave_temp)

	def calculate(self):
		a = TrafficAmount(self.mac_list)
		b = IntoStoreAmount(self.range_list)
		c = IntoStoreRate(self.range_list)
		d = VisitingCycle(self.interval_ave_list)
		e = NewAndOld(self.resident_list)
		f = CustomerActive(self.interval_ave_list)
		g = ResidentTime(self.resident_ave_list)
		h = BounceRate(self.resident_ave_list)
		i = DeepRate(self.resident_ave_list)
	
		for i in [a, b, c, d, e, f, g, h, i]:
			i.get_value()
	
		self.traffic_amount = a.return_value()
		self.into_store_amount = b.return_value()
		self.into_store_rate = c.return_value()
		self.visiting_cycle = d.return_value()
		self.new = e.new
		self.old = e.old
		self.high = f.high
		self.mid = f.mid
		self.low = f.low
		self.sleep = f.sleep
		self.resident_time = g.return_value()
		self.bounce_rate = h.return_value()
		self.deep_rate = i.return_value()

		'''
		#make the json object
		result = MkJson(traffic_amount, into_store_amount, into_store_rate, visiting_cycle, new, old, high, mid, low, sleep, resident_time, bounce_rate, deep_rate)
	
		res_json = result.get_json()
		'''


		'''
		#test print data	
		print 'mac_list len:' + '{}'.format(len(mac_list))
		print 'range_list len:' + '{}'.format(len(range_list))
		print 'mac_info_list len:' + '{}'.format(len(mac_info_list))
		print 'mac_time_list len:' + '{}'.format(len(mac_time_list))
	
		for i in mac_list:
			for j in i:
				test_sum += 1
	
		print test_sum
		'''
		#a.print_value()
		#b.print_value()
	
		#test print result
		print self.traffic_amount
		print self.into_store_amount
		print self.into_store_rate
		print self.visiting_cycle
		print self.new, self.old
		print self.high, self.mid, self.low, self.sleep
		print self.resident_time
		print self.bounce_rate
		print self.deep_rate
		
		print self.range_list
		print self.mac_list
		print self.time_list
		print self.resident_list
		print self.interval_list
		print self.resident_ave_list
		print self.interval_ave_list
		print self.mac_lasttime_list
		print self.mac_firsttime_list
	
		#print self.mac_lasttime_list == self.mac_firsttime_list

		'''
		#customer distance(range[])
		self.range_list = []
		self.range_temp = []
		
		#customer mac(mac[])
		self.mac_list = []
		self.mac_temp = []

		# timestamp list
		self.time_list = []
		
		# 顾客最后时间记录表
		self.mac_lasttime_list = {}
		self.mac_lasttime_temp = {}
		
		# 本次数据中各顾客第一次出现的时间记录，用来测试info列表，作为初始的最后时间
		self.mac_firsttime_list = {}
		self.mac_firsttime_temp = {}

		# 客户驻店时长之和及驻店次数记录（根据lasttime_list进行更新）
		self.resident_list = {}
		self.resident_temp = {}
	
		# 客户平均访问间隔
		self.interval_list = {}
		self.interval_temp = {}

		# 所需时间段内每个客户的平均驻店时长
		# mac_time = {mac1:time, mac2:time......}
		self.resident_ave_list = {}
		self.resident_ave_temp = {}

		#平均访问间隔
		self.interval_ave_list = {}
		self.interval_ave_temp = {}

		#顾客活跃度在生成数据结构时同时获得
		self.high = 0
		self.mid = 0
		self.low = 0
		'''

	def save(self):
		#make the json object
		result = MkJson(self.traffic_amount, self.into_store_amount, self.into_store_rate, self.visiting_cycle, self.new, self.old, self.high, self.mid, self.low, self.sleep, self.resident_time, self.bounce_rate, self.deep_rate)
		#result = MkJson(439, 78, 0.18, 2.3, 23, 55, 12, 42, 11, 23, 18.5, 0.23, 0.35)
		
		res_json = result.get_json()

		savecontrol = PyElasticsearchHour(res_json)
		savecontrol.init()
		savecontrol.save()
		

class Day(Hour):
	def __str__(self):
		return 'Calculation of daily dimension.'	
		'''			
		function:
			my_flush: flush the temp lists
			init: initialization data
			calculate: calculate the result
		'''
		
	__repr__ = __str__

	def save(self):
		#make the json object
		result = MkJson(self.traffic_amount, self.into_store_amount, self.into_store_rate, self.visiting_cycle, self.new, self.old, self.high, self.mid, self.low, self.sleep, self.resident_time, self.bounce_rate, self.deep_rate)
		
		res_json = result.get_json()

		savecontrol = PyElasticsearchDay(res_json)
		savecontrol.init()
		savecontrol.save()

class Week(Hour):
	def __str__(self):
		return 'Calculation of week dimension.'	
		'''			
		function:
			my_flush: flush the temp lists
			init: initialization data
			calculate: calculate the result
		'''

	__repr__ = __str__

	def save(self):
		#make the json object
		result = MkJson(self.traffic_amount, self.into_store_amount, self.into_store_rate, self.visiting_cycle, self.new, self.old, self.high, self.mid, self.low, self.sleep, self.resident_time, self.bounce_rate, self.deep_rate)
		
		res_json = result.get_json()

		savecontrol = PyElasticsearchWeek(res_json)
		savecontrol.init()
		savecontrol.save()
		

class Month(Hour):
	def __str__(self):
		return 'Calculation of month dimension.'	
		'''			
		function:
			my_flush: flush the temp lists
			init: initialization data
			calculate: calculate the result
		'''

	__repr__ = __str__

	def save(self):
		#make the json object
		result = MkJson(self.traffic_amount, self.into_store_amount, self.into_store_rate, self.visiting_cycle, self.new, self.old, self.high, self.mid, self.low, self.sleep, self.resident_time, self.bounce_rate, self.deep_rate)
		
		res_json = result.get_json()

		savecontrol = PyElasticsearchMonth(res_json)
		savecontrol.init()
		savecontrol.save()
		

def main():
	hour = Hour(json_hour['hits']['hits'])
	hour.init()
	hour.calculate()
	hour.save()

	day = Day(json_day['hits']['hits'])
	day.init()
	day.calculate()
	day.save()

	week = Week(json_week['hits']['hits'])
	week.init()
	week.calculate()
	week.save()

	month = Month(json_month['hits']['hits'])
	month.init()
	month.calculate()
	month.save()

'''
#the final main function
def main():
	try:
		while True:
			while not ( int(datetime.datetime.now().strftime('%M')) == 0 ):
				pass
			
			#init data and calculate
			hour = Hour()
			hour.init()
			hour.calculate()

			day = Day()
			day.init()
			day.calculate()
		
			week = Week()
			week.init()
			week.calculate()

			month = Month()
			month.init()
			month.calculate()

'''

if __name__ == '__main__':
	main()
		


###############
###############
#打印测试
#print traffic_amount
#print into_store_amount
#print into_store_rate_amount

#for key in mac_lasttime_list:
#	print key, mac_lasttime_list[key]

#for key in mac_firsttime_list:
#	print key, mac_firsttime_list[key]

#for i in mac_info_list:
#	print i

'''
def main():
	global mac_list, range_list, mac_info_list, mac_time_list

	test_sum = 0

	a = TrafficAmount(mac_list)
	b = IntoStoreAmount(range_list)
	c = IntoStoreRate(range_list)
	d = VisitingCycle(mac_info_list)
	e = NewAndOld(mac_info_list)
	f = CustomerActive(mac_info_list)
	g = ResidentTime(mac_time_list)
	h = BounceRate(mac_time_list)
	i = DeepRate(mac_time_list)

	for i in [a, b, c, d, e, f, g, h, i]:
		i.get_value()

	traffic_amount = a.traffic_amount
	into_store_amount = b.into_store_amount
	into_store_rate = c.into_store_rate
	visiting_cycle = d.visiting_cycle
	new = e.new
	old = e.old
	high = f.high
	mid = f.mid
	low = f.low
	sleep = f.sleep
	resident_time = g.resident_time
	bounce_rate = h.bounce_rate
	deep_rate = i.deep_rate

	#make the json object
	result = MkJson(traffic_amount, into_store_amount, into_store_rate, visiting_cycle, new, old, high, mid, low, sleep, resident_time, bounce_rate, deep_rate)

	res_json = result.get_json()

	#test print data	
	print 'mac_list len:' + '{}'.format(len(mac_list))
	print 'range_list len:' + '{}'.format(len(range_list))
	print 'mac_info_list len:' + '{}'.format(len(mac_info_list))
	print 'mac_time_list len:' + '{}'.format(len(mac_time_list))

	for i in mac_list:
		for j in i:
			test_sum += 1

	print test_sum
	#a.print_value()
	#b.print_value()

	#test print result
	print traffic_amount
	print into_store_amount
	print into_store_rate
	print visiting_cycle
	print new, old
	print high, mid, low, sleep
	print resident_time
	print bounce_rate
	print deep_rate

	savecontrol = PyElasticsearch('day', res_json)
	savecontrol.init()
	savecontrol.save()

if __name__ == '__main__':
	main()
'''
