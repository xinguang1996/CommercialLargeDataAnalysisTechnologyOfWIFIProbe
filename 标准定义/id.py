#!/usr/bin/python
#-*-coding:utf-8-*-
import json,datetime
import sys
from  random import randrange as rg
from random import uniform as uf
from  dateutil import relativedelta
import elasticsearch
import time as te
import requests
from elasticsearch import helpers

form = '{"addr": "\\u5409\\u6797", "lon": 0, "wmac": "02:1a:11:fb:d7:a7", "rate": "3", "port": 9100, "host": "192.168.1.10", "data":[ {"rssi": "-55", "mac": "", "range": "4.2"}], "time": "", "lat": 0, "mmac": "a2:20:a6:15:0c:c4", "wssid": "soft", "id": ""}'


es = elasticsearch.Elasticsearch([{'host':'192.168.1.50','port':'9200'},{'host':'192.168.1.52','port':'9200'}],request_timeout=1000)
data = json.loads(form)
start_time = datetime.datetime(2017,9,2,00,00,00)	#格式化开始时间
start_times = start_time.strftime('%a %b %d %H:%M:%S %Y')	#格式化开始时间

end_times = start_time + relativedelta.relativedelta(days=8)
now_time = datetime.datetime.now()
time_mac = {}		#未到达时间的mac字典
start_id = '2'		#设置id
data.update({'id':start_id,'time':start_times})

data_s = []

def jindu(count,p):		#进度条
	width = 50
	parsent = int(round(float(p)/float(count),2)*100)
	sys.stdout.write('{p}/{count}:'.format(p = p,count = count))
	sys.stdout.write('[' + parsent/2 * '*' + '-' * (width-parsent/2) + ']' + ':'+'{p}/{count}'.format(p = parsent,count=100)+'\r')
	sys.stdout.flush()



def mkjson():


	global now_time
	global data
	global time_mac
	global start_id
	global data_s
	
	temp = {}
	stime = datetime.datetime.strptime(data['time'],'%a %b %d %H:%M:%S %Y')	#获取上一个时间
	time = stime + datetime.timedelta(seconds = 10) #+10s
	now_time = time
	etime = time.strftime('%a %b %d %H:%M:%S %Y')
	hour = time.hour
	mac_count = 0					#mac总数
	mac_incount = len(time_mac.keys())		#上一状态店内顾客数
	mac_outcount = 0				#未进店mac数
	mac_list = []					#新的data列表
	mac0 = '00:00:00:00'
	mac1 = 0
	mac2 = 0
	mac_tmp = mac_incount
	if hour >= 6 and hour <= 18:
		mac_count = rg(20,30)
		mac_tmp = rg(8,12)			#随机出进店人数
	elif hour > 18 and hour <=24:
		mac_count = rg(18,28)
		mac_tmp = rg(6,10)
	elif hour >= 0 and hour < 6:
		mac_count = rg(12,24)
		mac_tmp = rg(4,8)
	if len(time_mac.keys())	!= 0:
		for x in time_mac.keys():	#清除超时
			if (time_mac[x] - time).total_seconds() <= 0: #超时
				time_mac.pop(x)
				mac_incount-=1
			else:				#添加未超时数据到新列表
				s = {}
				s['range'] = rg(30)
				s['mac'] = x
				s['rssi'] = '-'+str(rg(40,100))
				mac_list.append(s)
	mac_outcount = mac_count - mac_incount
	if mac_tmp - mac_incount > 4:			#当随机的进店人数比实际进店人数多4时
		for x in range(rg(mac_tmp-mac_incount)): #补足入店人数
			mac = ''
			while True:
				mac1 = str(rg(99)).zfill(2)
				mac2 = str(rg(99)).zfill(2)
				mac = mac0+':'+mac1+':'+mac2
				if mac not in time_mac:
					break
			t = rg(3,20)			#设置改mac店内时间
			time_mac[mac] = time+datetime.timedelta(minutes = t) 	#改mac截止时间
			s = {}	
			s['mac'] = mac
			s['range'] = rg(30)
			s['rssi'] = '-'+str(rg(40,100))
			mac_list.append(s)
	else:
		mac_tmp = mac_incount
	mac_outcount = mac_count - mac_tmp
	for x in range(mac_outcount):		#未入店顾客
		mac = ''
		while True:
			mac1 = str(rg(99)).zfill(2)
			mac2 = str(rg(99)).zfill(2)
			mac = mac0+':'+mac1+':'+mac2
			if mac not in time_mac:
				break
		s = {}	
		s['mac'] = mac
		s['range'] =rg(30,100)
		s['rssi'] = '-'+str(rg(40,100))
		mac_list.append(s)


	temp['time'] = etime
	temp['data'] = mac_list[:]
	temp['lat'] = str(round(uf(125.293995,125.300265),6))	#定义经度
	temp['lon'] = str(round(uf(43.860149,43.860461),6))		#定义维度
	temp['location'] = [temp['lon'],temp['lat']]
	time = datetime.datetime.strptime(temp['time'],'%a %b %d %H:%M:%S %Y')
        temp['@timestamp'] = time.strftime('%Y-%m-%dT%H:%M:%S.000Z')            #定义存储时间
        index = 'sou'
        doc_type = 'tz_{number}'.format(number=start_id)
	_id = time.strftime('%a %b %d %H:%M:%S %Y')
	dic = {'_id':_id,'_index':index,'_type':doc_type,'_source':temp}
	data_s.append(dic)
	data = temp
	if not es.indices.exists(index=index):
		es.indices.create(index = index)
		
	
x = (end_times - start_time).total_seconds()

def write_file(s):
	if len(s):
		with open('sou.txt','a+') as f:
			f.write(json.dumps(s[0]['_source'])+'\n')
while True:
	global now_time
	global data_s
	global es
#	end_time = datetime.datetime.now()
	i = (end_times-now_time).total_seconds()
	if i > 0:
		jindu(x,x-i+1)
		mkjson()
#		write_file(data_s)
		if len(data_s) >= 300:
			helpers.bulk(es,data_s,request_timeout=6000)
			data_s = []
			te.sleep(0.5)
	else:
		break
	


