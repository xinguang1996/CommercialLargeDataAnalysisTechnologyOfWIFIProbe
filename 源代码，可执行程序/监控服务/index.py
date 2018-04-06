#!/usr/bin/python
#coding: utf-8

# Monitoring of the Server cluster

import time, datetime
import os
import sys
import json
import pexpect
import requests
class Monitor(object):
	def __init__(self):
		self.config_file = 'config.json'

		self.ip_list_http = []
		self.ip_list_elk = []
		self.ip_list_spark = []
		self.ip_list_web = []
		self.ip_list_wechat = []
		self.ip_list_sms = []
		self.ip_list_probe = []
		
		self.status_temp = []
		self.status_list_probe = []
		self.status_list_http = []
		self.status_list_elk = []
		self.status_list_spark = []
		self.status_list_web = []
		self.status_list_wechat = []
		self.status_list_sms = []
		self.status_list_probe = []

		self.status_dic = {}

		self.SSH_NEWKEY = '(?i)are you sure you want to continue connecting' 
		self.NO_CONNECT = 'No route to host'
		self.PING_OK = '1 packets transmitted, 1 received, 0% packet loss'
		self.PING_NO = 'errors'

	def __str__(self):
		return 'Moitoring of the Server cluster'

	__repr__ = __str__

	def get_list(self):
		#get (the serial number/IP) of Server cluster from the json
		f = file(self.config_file)
		s = json.load(f)
		self.ip_list_http = s['http']
		self.ip_list_es = s['elasticsearch']
		self.ip_list_spark = s['spark']
		self.ip_list_web = s['web']
		self.ip_list_wechat = s['wechat']
		self.ip_list_sms = s['sms']
		self.ip_list_probe = s['probe']

	def monitor(self):
		try:
			'''
					There are three monitoring methods. The probe 
				module judges the status of the probe by sending 
				the http request, and returns the http status code 
				to 200 to indicatethat the service is online. The 
				remaining modules determine whether the service is 
				online by determining whether the process exists.
			'''

			for i in [self.ip_list_http, self.ip_list_spark, self.ip_list_es, self.ip_list_web, self.ip_list_sms,self.ip_list_wechat,self.ip_list_probe]:
				if i == self.ip_list_http:
					name = 'http'
				elif i == self.ip_list_spark:
					name = 'spark'
				elif i == self.ip_list_es:
					name = 'es'
				elif i == self.ip_list_web:
					name = 'web'
				elif i == self.ip_list_sms:
					name = 'sms'
				elif i == self.ip_list_wechat:
					name = 'wechat'
				elif i == self.ip_list_probe:
					name = 'probe'
				else:
					pass
				print '[INFO]: now work server:',name
				for index in i:
					if name == 'probe':
						cmd = 'ping -c 1 {host}'.format(host=index['host'])
						ping = pexpect.spawn(cmd,timeout=10)
						check_num = ping.expect([pexpect.TIMEOUT,self.PING_OK,self.PING_NO],5)
						if check_num == 0 or check_num == 2:
							status = 'false'
						else:
							status = 'true'
						url = 'http://192.168.1.52:9200/user_table/_search?q=wid:{wid}&_source_include=id'.format(wid = index['wid'])
						data = json.loads(requests.get(url).text)
						print data
						user_table = data['hits']['hits'][0]['_source']['id']
						if len(self.status_temp) == 0:
							self.status_temp.append({str(user_table) : [{'host' : index['host'] , 'status' : status, 'server' : index['key'] , 'name' : index['name']}]})
						else:
							self.status_temp[0].update({str(user_table) : [{'host':index['host'],'server':index['key'],'name':index['name'],'status':status}]})	
					elif name == 'web':
						url = 'http://{host}:8080/softbei/test.html'.format(host = index['host'])
						rest = requests.get(url)
						status_code = rest.status_code
						if status_code == 200:
							self.status_temp.append({'name':index['name'],'host':index['host'],'status':'true','server':index['key']})
						else:
							self.status_temp.append({'name':index['name'],'host':index['host'],'status':'false','server':index['key']})
					else:
						cmd = 'ssh -l pi {host} ps -ef|grep {key}'.format(host = index['host'],key = index['key'])
						ssh = pexpect.spawn(cmd,timeout=10)
						resful_num=ssh.expect([pexpect.TIMEOUT,'password:',self.SSH_NEWKEY,self.NO_CONNECT],timeout=1)
						if resful_num == 0 or resful_num == 3:
							self.status_temp.append({'name':index['name'],'host':index['host'],'status':'false','server':index['key']})
							ssh.close()
							continue
						elif resful_num == 2:
							ssh.sendline("yes")
							resful_num=ssh.expect([pexpect.TIMEOUT,'password:',self.SSH_NEWKEY,self.NO_CONNECT],timeout=1)
							if resful_num == 1:
								ssh.sendline('raspberry')
						else:
							ssh.sendline('raspberry')
						res = ssh.readline() 
						count = 1
						while res != '':
							res = ssh.readline() 
							count +=1
						if count > int(index['num']):
							self.status_temp.append({'name':index['name'],'host':index['host'],'status':'true','server':index['key']})
						else:
							self.status_temp.append({'name':index['name'],'host':index['host'],'status':'false','server':index['key']})
						ssh.close()
				self.status_dic.update({name:self.status_temp})
				self.status_temp = []

		except Exception,e:
			print e

	def send(self):
		status_file = open('status.json', 'w')
		json.dump(self.status_dic, status_file)
		status_file.close()
		
		file_path = 'status.json'
		cmd1 = 'scp ' + file_path + ' pi@192.168.1.200:~/Monitor/'
		os.system(cmd1)
		
def main():
	while True:
		print '\n[INFO]: This round of monitoring start!\n'

		monitor = Monitor()
		monitor.get_list()
		monitor.monitor()
		monitor.send()

		print '\n[INFO]: This round of monitoring end!\n'

		time.sleep(1)

if __name__ == '__main__':
	main()
