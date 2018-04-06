# -*- coding: utf-8 -*-
# filename: receive.py
import xml.etree.ElementTree as ET

def parse_xml(web_data):
    if len(web_data) == 0:
        return None
    xmlData = ET.fromstring(web_data)
    msg_type = xmlData.find('MsgType').text
    if msg_type == 'text':
        return TextMsg(xmlData)
    elif msg_type == 'event':
	event_type = xmlData.find('Event').text
	if event_type == 'CLICK':
		return Click(xmlData)
    return ImageMsg(xmlData)

class EventMsg(object):
    def __init__(self, xmlData):
        self.ToUserName = xmlData.find('ToUserName').text
        self.FromUserName = xmlData.find('FromUserName').text
        self.CreateTime = xmlData.find('CreateTime').text
        self.MsgType = xmlData.find('MsgType').text
        self.Event = xmlData.find('Event').text
	
'''
	self.Boot = False
	self.Shutdown = False
	self.Reboot = False
	self.QueryHour = False
	self.QueryDay = False
	self.QueryWeek = False
	self.QueryMonth = False
'''
class Click(EventMsg):
    def __init__(self, xmlData):
#	print 'aaaa'
        EventMsg.__init__(self, xmlData)
        self.Eventkey = xmlData.find('EventKey').text
'''
	print self.Eventkey 
	if self.Eventkey == 'boot':
		print 'in boot'
		self.Boot = True
	elif self.Eventkey == 'shutdown':
		self.Shutdown = True
	elif self.Eventkey == 'reboot':
		self.Reboot = True
	elif self.Eventkey == 'hour':
		self.QueryHour = True
	elif self.Eventkey == 'day':
		self.QueryDay = True
	elif self.Eventkey == 'week':
		self.QueryWeek = True
	elif self.QueryMonth == 'month':
		self.QueryMonth = True
	else:
		pass
'''
class Msg(object):
    def __init__(self, xmlData):
        self.ToUserName = xmlData.find('ToUserName').text
        self.FromUserName = xmlData.find('FromUserName').text
        self.CreateTime = xmlData.find('CreateTime').text
        self.MsgType = xmlData.find('MsgType').text
        self.MsgId = xmlData.find('MsgId').text

	self.Boot = False
	self.Shutdown = False
	self.Reboot = False

	self.QueryHour = False
	self.QueryDay = False
	self.QueryWeek = False
	self.QueryMonth = False

class TextMsg(Msg):
    def __init__(self, xmlData):
        Msg.__init__(self, xmlData)
        self.Content = xmlData.find('Content').text.encode("utf-8")
	print self.Content
	print 'aaaaaaaa'
	print type(self.Content)
	
	if '开机' in self.Content:
		self.Boot = True
	elif '关机' in self.Content:
		self.Shutdown = True
	elif '重启' in self.Content:
		self.Reboot = True
	else:
		pass
	
	if '时' in self.Content:
		self.QueryHour = True
	elif '天' in self.Content:
		self.QueryDay = True
	elif '周' in self.Content:
		self.QueryWeek = True
	elif '月' in self.Content:
		self.QueryMonth = True
	else:
		pass

	#test print
	print '开机', self.Boot
	print '关机', self.Shutdown
	print '重启', self.Reboot
	print 'hour', self.QueryHour
	print 'day', self.QueryDay
	print 'week', self.QueryWeek
	print 'month', self.QueryMonth		

class ImageMsg(Msg):
    def __init__(self, xmlData):
        Msg.__init__(self, xmlData)
        self.PicUrl = xmlData.find('PicUrl').text
        self.MediaId = xmlData.find('MediaId').text
