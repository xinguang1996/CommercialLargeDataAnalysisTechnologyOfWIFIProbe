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

import json
import datetime
import os

class FileExistsError(IOError):
    pass

def getYear(fn):
    def year():
	return datetime.datetime.now().year
    return year

def getMonth(fn):
    def month():
	return datetime.datetime.now().month
    return month

def getDay(fn):
    def day():
	return datetime.datetime.now().day
    return day

def getHour(fn):
    def hour():
	return datetime.datetime.now().hour
    return hour

def getMinute(fn):
    def minute():
	return datetime.datetime.now().minute
    return minute

@getYear
def year():
    pass

@getMonth
def month():
    pass

@getDay
def day():
    pass

@getHour
def hour():
    pass

@getMinute
def minute():
    pass

class MkJson(object):
    def __init__(self, mmac, lon, lat, data):
	self.ID = "000001"
        self.lon = lon
        self.lat = lat
        self.data = data
        self.mmac = mmac

        self.struct = '%a %b %d %H:%M:%S %Y'
        self.time = datetime.datetime.now().strftime(self.struct)

        #the result json data
	self.json_dict = {}

    def __str__(self):
	return 'Make the result into a json, to save to the database(elasticsearch)!'

    __repr__ = __str__

    def get_json(self):
        self.json_dict = {
                "id": ('{ID}').format(ID = self.ID),
                "data": self.data,
                "mmac": self.mmac, 
                "time": self.time,
                "lon": self.lon,
                "lat": self.lat
                }	
		
	return self.json_dict

def main():
	#test = MkJson(1)
	test = MkJson(1000, 500, 0.5, 5, 200, 300, 100, 100, 100, 200, 5, 0.2, 0.3)
	
	test.get_json()
	
	print test.json_dict

if __name__ == '__main__':
	main()

'''
print(test_dict)
print(type(test_dict))
 
json_str = json.dumps(test_dict)
print(json_str)
print(type(json_str))

year = year()
month = month()
day = day()
hour = hour()
minute = minute()

#filename = '%04d' % year + '%02d' % month + '%02d' % day + '%02d' % hour + '.json'

#test
filename = '%04d' % year + '%02d' % month + '%02d' % day + '10' + '.json'

if not os.path.exists(filename):
	pass
else:
	raise FileExistsError('The JSON is existing!')

with open(filename, "w") as f:
	json.dump(test_dict, f)
	print("OK")

with open("./b.json", "w") as f:
	json.dump(json_str, f)
	print("OK")

'''
