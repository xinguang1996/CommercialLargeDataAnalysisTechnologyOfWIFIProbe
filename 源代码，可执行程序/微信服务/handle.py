# -*- coding: utf-8 -*-
# filename: handle.py
import hashlib
import reply
import receive
import web
import xml.etree.ElementTree as ET
import elasticsearch
import time
import datetime
import socket


class Handle(object):
	def __init__(self):
		self.es = elasticsearch.Elasticsearch([{'host': '192.168.1.100', 'port': 9200}])
	
		self.json_hour = None
		self.json_day = None
		self.json_week = None
		self.json_month = None


	def send(self,info):
		host = '192.168.1.201'
		port = 12345
		sock = socket.socket()
		sock.connect((host, port))
		sock.send(info)
		receive = sock.recv(1024)
		send_info=''
		if info == 'boot':
			if receive == 'yes':
				send_info = '系统开机成功'
			else:
				send_info = '系统开机失败，系统已开机'
		elif info == 'shutdown':
			if receive == 'yes':
				send_info = '系统关机成功'
			else:
				send_info = '系统关机失败，系统已关机'
		elif info == 'reboot':
			print 'in info'
			if receive == 'yes':
				print 'in reboot'
				send_info = '系统已重启成功'
			else:
				send_info = '系统重启失败，系统已关机'
		else:
			pass
		print send_info
		return send_info

	def query(self, json):
		traffic_amount = json['hits']['hits'][0]['_source']['Traffic amount']
		into_store_amount = json['hits']['hits'][0]['_source']['The amount of store']
		into_store_rate = json['hits']['hits'][0]['_source']['Into the store rate']
		visiting_cycle = json['hits']['hits'][0]['_source']['Average visiting cycle']
		resident_time = json['hits']['hits'][0]['_source']['Average the resident time']
		new = json['hits']['hits'][0]['_source']['The new and old customers']['new']
		old = json['hits']['hits'][0]['_source']['The new and old customers']['old']
		high = json['hits']['hits'][0]['_source']['Customer active']['High activity']
		mid = json['hits']['hits'][0]['_source']['Customer active']['Mid activity']
		low = json['hits']['hits'][0]['_source']['Customer active']['Low activity']
		sleep = json['hits']['hits'][0]['_source']['Customer active']['Sleep activity']
		bounce_rate = json['hits']['hits'][0]['_source']['Bounce rate']
		deep_rate = json['hits']['hits'][0]['_source']['Deep rate']
		print 'ssss'	
		#date format
		into_store_rate = '%.2f' % float(into_store_rate)
		#day 1day=86400s
		visiting_cycle = '%.1f' % float(float(visiting_cycle)/float(86400))
		#minute 1m=60s
		resident_time = '%.2f' % float(float(resident_time)/float(60))
		bounce_rate = '%.2f' % float(bounce_rate)
		deep_rate = '%.2f' % float(deep_rate)

		_type = json['hits']['hits'][0]['_type']
		
		if _type == 'hour':
			_str = '您好！\n近一小时内店铺客流信息如下：\n客流量：{traffic_amount}人次\n入店量：{into_store_amount}人次\n入店率：{into_store_rate}\n平均来访周期：{visiting_cycle}\n新顾客量：{new}人次\n老顾客量：{old}人次\n高活跃顾客量：{high}人次\n中活跃顾客量：{mid}人次\n低活跃顾客量：{low}人次\n沉睡活跃度量：{sleep}人次\n平均驻店时长：{resident_time}分钟/次\n跳出率：{bounce_rate}\t深访率：{deep_rate}'.format(traffic_amount = traffic_amount,
		  into_store_amount = into_store_amount,
		  into_store_rate = into_store_rate,
		  visiting_cycle = visiting_cycle,
		  new = new,
		  old = old,
		  high = high,
		  mid = mid,
		  low = low,
		  sleep = sleep,
		  resident_time = resident_time,
		  bounce_rate = bounce_rate,
		  deep_rate = deep_rate)

			return _str

		if _type == 'day':
			_str = '您好！\n近一天内店铺客流信息如下：\n客流量：{traffic_amount}人次\n入店量：{into_store_amount}人次\n入店率：{into_store_rate}\n平均来访周期：{visiting_cycle}天\n新顾客量：{new}人次\n老顾客量：{old}人次\n高活跃顾客量：{high}人次\n中活跃顾客量：{mid}人次\n低活跃顾客量：{low}人次\n沉睡活跃度量：{sleep}人次\n平均驻店时长：{resident_time}分钟/次\n跳出率：{bounce_rate}\t深访率：{deep_rate}'.format(traffic_amount = traffic_amount,
		  into_store_amount = into_store_amount,
		  into_store_rate = into_store_rate,
		  visiting_cycle = visiting_cycle,
		  new = new,
		  old = old,
		  high = high,
		  mid = mid,
		  low = low,
		  sleep = sleep,
		  resident_time = resident_time,
		  bounce_rate = bounce_rate,
		  deep_rate = deep_rate)

			return _str

		if _type == 'week':
			_str = '您好！\n近一周内店铺客流信息如下：\n客流量：{traffic_amount}人次\n入店量：{into_store_amount}人次\n入店率：{into_store_rate}\n平均来访周期：{visiting_cycle}天\n新顾客量：{new}人次\n老顾客量：{old}人次\n高活跃顾客量：{high}人次\n中活跃顾客量：{mid}人次\n低活跃顾客量：{low}人次\n沉睡活跃度量：{sleep}人次\n平均驻店时长：{resident_time}分钟/次\n跳出率：{bounce_rate}\t深访率：{deep_rate}'.format(traffic_amount = traffic_amount,
		  into_store_amount = into_store_amount,
		  into_store_rate = into_store_rate,
		  visiting_cycle = visiting_cycle,
		  new = new,
		  old = old,
		  high = high,
		  mid = mid,
		  low = low,
		  sleep = sleep,
		  resident_time = resident_time,
		  bounce_rate = bounce_rate,
		  deep_rate = deep_rate)

			return _str

		if _type == 'month':
			_str = '您好！\n近一个月内店铺客流信息如下：\n客流量：{traffic_amount}人次\n入店量：{into_store_amount}人次\n入店率：{into_store_rate}\n平均来访周期：{visiting_cycle}天\n新顾客量：{new}人次\n老顾客量：{old}人次\n高活跃顾客量：{high}人次\n中活跃顾客量：{mid}人次\n低活跃顾客量：{low}人次\n沉睡活跃度量：{sleep}人次\n平均驻店时长：{resident_time}分钟/次\n跳出率：{bounce_rate}\t深访率：{deep_rate}'.format(traffic_amount = traffic_amount,
		  into_store_amount = into_store_amount,
		  into_store_rate = into_store_rate,
		  visiting_cycle = visiting_cycle,
		  new = new,
		  old = old,
		  high = high,
		  mid = mid,
		  low = low,
		  sleep = sleep,
		  resident_time = resident_time,
		  bounce_rate = bounce_rate,
		  deep_rate = deep_rate)

			return _str

	def POST(self):
		try:
			#time
			now = datetime.datetime.now()
			#index
#			index = now.strftime('%Y.%m.%d_%H')
#			index = 'res-' + index
			index = 'res'




#send sucess 

			webData = web.data()
			print "Handle Post webdata is ", webData   #后台打日志
			recMsg = receive.parse_xml(webData)
			#reply sucess,it's mean had received

			flag = self.es.indices.exists(index=index)
			print flag
			if flag == True:

			#json data
				self.json_hour = self.es.search(index = index,
					                        doc_type = '0001-hour',
					                        body = {'query': {'match_all': {}}})	

				self.json_day = self.es.search(index = index,
					                       doc_type = '0001-day',
					                       body = {'query': {'match_all': {}}})	

				self.json_week = self.es.search(index = index,
					                        doc_type = '0001-week',
					                        body = {'query': {'match_all': {}}})	

				self.json_month = self.es.search(index = index,
					                        doc_type = '0001-month',
					                        body = {'query': {'match_all': {}}})
			else:
				return '索引错误'
			#reply reality message
			if isinstance(recMsg, receive.Msg) and recMsg.MsgType == 'text':
				toUser = recMsg.FromUserName
				fromUser = recMsg.ToUserName
				content = ""
				#replyMsg = reply.TextMsg(toUser, fromUser, content)
			
				if recMsg.Boot == True:
					content = self.send('boot')
				elif recMsg.Shutdown == True:
					content = self.send('shutdown')
				elif recMsg.Reboot == True:
					content = self.send('reboot')
				elif recMsg.QueryHour == True:
					if flag == True:
						content = self.query(self.json_hour)
					else:
						content = "对不起，查无此数据"
						
				elif recMsg.QueryDay == True:
					if flag == True:
						content = self.query(self.json_day)
					else:
						content = "对不起，查无此数据"
				elif recMsg.QueryWeek == True:
					if flag == True:
						content = self.query(self.json_week)
					else:
						content = "对不起，查无此数据"
				elif recMsg.QueryMonth == True:
					if flag == True:
						content = self.query(self.json_month)
					else:
						content = "对不起，查无此数据"
				else:
					content = '您好！\n\n如需提供WIFI设备服务请发送：\n\t\t\t\t\t\t开机 or 关机 or 重启\n\n如需查询店铺客流信息请发送：\n\t\t\t\t\t\t\t查询时 or 查询天\n\t\t\t\t\t\t\t查询周 or 查询月\n\n谢谢!'
				replyMsg = reply.TextMsg(toUser, fromUser, content)
				print content
				return replyMsg.send()				
			elif isinstance(recMsg, receive.EventMsg):
				toUser = recMsg.FromUserName
				fromUser = recMsg.ToUserName
				content = ""
				if recMsg.Event == 'CLICK':
					if recMsg.Eventkey == 'boot':
						print 'in boot'
						content=self.send('boot')
						print content
	
					elif recMsg.Eventkey == 'shutdown':
						content=self.send('shutdown')
					elif recMsg.Eventkey == 'reboot':
						content=self.send('reboot')
					elif recMsg.Eventkey == 'hour':
						if flag == True:
							content = self.query(self.json_hour)
						else:
							content = "对不起，查无此数据"
							
					elif recMsg.Eventkey == 'day':
						if flag == True:
							content = self.query(self.json_day)
						else:
							content = "对不起，查无此数据"
					elif recMsg.Eventkey == 'week':
						if flag == True:
							content = self.query(self.json_week)
						else:
							content = "对不起，查无此数据"
					elif recMsg.Eventkey == 'month':
						if flag == True:
							content = self.query(self.json_month)
						else:
							print 'in no month'
							content = "对不起，查无此数据"
					else:
							pass
					replyMsg = reply.TextMsg(toUser, fromUser, content)
					print 'toUser'+toUser
					return replyMsg.send()
				else:
					print "暂且不处理"
					return "suexcept Exception, Argment"

		except Exception, Argment:
			return Argment

