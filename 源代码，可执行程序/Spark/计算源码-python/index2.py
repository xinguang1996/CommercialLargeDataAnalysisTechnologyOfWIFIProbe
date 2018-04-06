#coding: utf-8

'''
Match: 2017 China Software Cup
Topic: Commercial large data analysis technology based on WIFI probe.
School: CCUT(Changchun University Of Technology)
:x
Team: teamName   --- Victors
	  teamLeader --- Jiahui Tang
	  teamMember --- Pengyue Zhao
	  teamMember --- Xinguang Guo
'''

#import the necessary packages
from pyspark import SparkContext, SparkConf
from operator import *
import copy
import json
import re
import time
import datetime
import numpy as np
import pandas
import matplotlib
import elasticsearch 

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
'''
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

def traffic_amount(es_rdd):
    def get_range(x):
        for value in x['data']:
	    yield value['range']

    # Return format: [(time, range_amount), (time, range_amount), ..., (str, int)]
    def get_range_amount(x):
    	acount = 0
    	for i in x:
    	    acount += 1
    	return acount

    traffic_rdd = es_rdd.mapValues(get_range)

    return traffic_rdd.mapValues(get_range_amount).values().sum()

def new_and_old(es_rdd):
    global sc

    # Threshold value
    INTO = 50

    new_amount = 0
    old_amount = 0

    def get_mac_into(x):
        #test = []
        for value in x['data']:
            if value['range'] < INTO:
                yield value['mac']
                #test.append('mac')  

    # Return format: [(time, macs_into), ...]
    temp_rdd = es_rdd.mapValues(get_mac_into)
   
    def test(x):
        t = []
        for i in x:
            t.append(i)
        return t

    #test = temp_rdd.mapValues(test).values().filter(lambda x: x == 1).count()

    #print test

    # Return format: [(mac, count), (mac, count), ...]
    #temp_rdd = sorted(temp_rdd.values().countByKey().items())
    #temp_rdd = sorted(temp_rdd.values().countByKey())
    #temp_rdd = temp_rdd.values()

    #temp_rdd = sorted(temp_rdd.mapValues(test).values().countByKey().items())
    temp_rdd = temp_rdd.mapValues(test).values().countByKey()

    print temp_rdd.values()

    new_amount = sc.parallelize(temp_rdd.values()).filter(lambda x: x == 1).count()
    old_amount = sc.parallelize(temp_rdd.values()).filter(lambda x: x > 1).count()

    #new_amount = temp_rdd.values().filter(lambda x: x == 1).count()
    #old_amount = temp_rdd.values().filter(lambda x: x > 1).count()

    #print new_amount.collect()

    return new_amount, old_amount

def visiting_cycle(es_rdd):
    LOW = 200
    MID = 120
    HIGH = 70
    
    BOUNCE = 30
    DEEP = 400

    high, mid, low, sleep = 0, 0, 0, 0
    bounce, deep = 0, 0

    # Format: {mac1: [时间间隔之和, 次数], mac2: [], ...}
    interval_list = {}
    
    # Format: {mac1: [驻店时长之和, 次数], mac2: [], ...}
    resident_list = {}

    # Format: {mac1: lasttime, mac2: lasttime, ...}
    mac_lasttime_list = {}

    # Format: {mac1: ave_interval, mac2: ...}
    ave_interval_list = {}

    # Format: {mac1: ave_resident, mac2: ...}
    ave_resident_list = {}

    # Convert to PythonList
    # all_info = es_rdd.collect()
   
    # print es_rdd.collect()
    
    all_info = es_rdd
    
    '''
    # Get the K-V pair(mac-firsttime) in this time period
    for i in all_info:
        for j in i[-1]['data']:
            temp_mac_lasttime = {j['mac']: i[-1]['time']}
            
            if not mac_lasttime_list.has_key(j['mac']):
                mac_lasttime_list.update(temp_mac_lasttime)
            else:
                pass
    '''

    for i in all_info['hits']['hits']:
        for j in i['_source']['data']:
            temp_mac_lasttime = {j['mac']: i['_source']['time']}
            
            if not mac_lasttime_list.has_key(j['mac']):
                mac_lasttime_list.update(temp_mac_lasttime)
            else:
                pass

    # Init the lists
    for index, mac in enumerate(mac_lasttime_list):
        temp_resident = {mac: [0, 1]}
        resident_list.update(temp_resident)
        
        temp_interval = {mac: [0, 1]}
        interval_list.update(temp_interval)

    for i in all_info['hits']['hits']:
        for j in i['_source']['data']:
            temp_mac = j['mac']
            temp_time = i['_source']['time']
            
            #func timechange time_temp->datetime_temp
            testtime1 = MyTime(temp_time)
            temp_datetime = testtime1.ctime_to_datetime()

            if mac_lasttime_list.has_key(temp_mac):
                mac_lasttime = mac_lasttime_list[temp_mac]

                #timechang ->type(datatime.datetime)
                testtime2 = MyTime(mac_lasttime)
                temp_lasttime = testtime2.ctime_to_datetime()

                temp_interval = (temp_datetime - temp_lasttime).seconds
		
		#print temp_interval

                # Update the resident and interval
                if temp_interval < 100:
                    temp_resident = {temp_mac: [resident_list[temp_mac][0] + temp_interval, resident_list[temp_mac][1]]}
                    resident_list.update(temp_resident)

                else:
                    temp_interval = {temp_mac: [resident_list[temp_mac][0], resident_list[temp_mac][1] + 1]}
                    interval_list.update(temp_interval)

                # Update the lasttime
                temp_mac_lasttime = {temp_mac: temp_time}
                mac_lasttime_list.update(temp_mac_lasttime)

    # Get the ave
    for index, key in enumerate(interval_list):
        ave_interval = div(interval_list[key][0], float(interval_list[key][1]))

        ave_interval_temp = {key: ave_interval}
        ave_interval_list.update(ave_interval_temp)

    for index, key in enumerate(resident_list):
        ave_resident = div(resident_list[key][0], float(resident_list[key][1]))

        ave_resident_temp = {key: ave_resident}
        ave_resident_list.update(ave_resident_temp)
        
    # Get the active of customer
    for index, key in enumerate(ave_interval_list):
        if ave_interval_list[key] < HIGH and ave_interval_list[key] >= 0:
            high += 1
        elif ave_interval_list[key] < MID:
            mid += 1
        elif ave_interval_list[key] < LOW:
            low += 1
        elif ave_interval_list[key] >= LOW:
            sleep += 1
        else:
            raise ValueError

    # Get the bounce rate and deep rate
    for index, key in enumerate(ave_resident_list):
	if ave_resident_list[key] < BOUNCE:
            bounce += 1
	elif ave_resident_list[key] >= BOUNCE:
	    pass
	else:
	    raise ValueError

    return sleep, low, mid, high, bounce, deep

class Hour(object):
    global now
    interval = now - datetime.timedelta(hours = 1)

    def __init__(self):
        self.traffic = 0
        self.into_store_amount = 0
        self.into_store_rate = 0
        self.visiting = 0
        self.new = 0
        self.old = 0
        self.high = 0
        self.mid = 0
        self.low = 0
        self.sleep = 0
        self.resident_time = 0
        self.bounce_rate = 0
        self.deep_rate = 0

    def __str__(self):
        return 'Calculation of hourly dimension.'

    __repr__ = __str__  

    def run(self):
        # Es-Hadoop Get the data

	query = json.dumps({"query": {"range": {"@timestamp": {"gte": self.interval.strftime("%Y-%m-%dT%H:00:00"), "lt": now.strftime("%Y-%m-%dT%H:00:00")}}}})

	es_read_conf = {
                "es.nodes" : "192.168.1.50",
                "es.port" : '9200',
                "es.resource" : "sou_1/tz_1",   
                "es.query":query 
        }

	es_rdd = sc.newAPIHadoopRDD(
                inputFormatClass = "org.elasticsearch.hadoop.mr.EsInputFormat",
                keyClass = "org.apache.hadoop.io.NullWritable",
                valueClass = "org.elasticsearch.hadoop.mr.LinkedMapWritable",
                conf = es_read_conf
                )

        # Elasticsearch API get the data
        es = elasticsearch.Elasticsearch(['192.168.1.52:9200','192.168.1.51:9200'], request_timeout=1000)

        query1 = {"query": {"range": {"@timestamp": {"gte": self.interval.strftime("%Y-%m-%dT%H:00:00"), "lt": now.strftime("%Y-%m-%dT%H:00:00")}}}}
        query2 = {"query": {"range": {"@timestamp": {"gte": self.interval.strftime("%Y-%m-%dT%H:00:00"), "lt": now.strftime("%Y-%m-%dT%H:00:00")}}}, "sort": {"@timestamp": {"order": "asc"}}}

        count = es.count(index = 'sou_1', doc_type = 'tz_1', body = query1)
        
        data = {
                'hits': {
                    'hits': []
                    }
                }

        for num in range(0, count['count'], 1000):
            temp_dic = es.search(index = 'sou_1', doc_type = 'tz_1', body = query2, size = 1000, _source = True, from_ = num)
            for temp_num in range(len(temp_dic['hits']['hits'])):
                data['hits']['hits'].append(temp_dic['hits']['hits'][temp_num])

##########Get data finished!##########

        # Get the traffic amount
        self.traffic = traffic_amount(es_rdd)

        # Get the into store amount
        self.into_store_amount = into_amount(es_rdd)
        
        # Get the into store rate
        self.into_store_rate = self.into_store_amount / 2
        # Get the new and old
        self.new, self.old = new_and_old(es_rdd)

        # Get the visiting cycle
        #self.visiting_cycle = visiting_cycle(data)
   
        # Get the active of customer
        self.sleep, self.low, self.mid, self.high, self.bounce, self.deep = visiting_cycle(data)

        # Get the resident time
        #self.resident_time = 
        
        # Get the bounce rate and deep rate
        #self.bounce_rate = 
        #self.deep_rate =
        
##########Calculate finished!##########

class Day(Hour):
    interval = now - datetime.timedelta(days = 1)

class Week(Hour):
    interval = now - datetime.timedelta(weeks = 1)

class Month(Hour):
    interval = now - datetime.timedelta(days = 14)

def main():
    '''
    print '[INFO]: start get hour data'
    hour = Hour()
    print '[INFO]: finish get hour data'
    
    print '[INFO]: start get day data'
    day = Day()
    print '[INFO]: finish get day data'

    print '[INFO]: start get week data'
    week = Week()
    print '[INFO]: finish get week data'
    
    print '[INFO]: start get month data'
    month = Month()
    print '[INFO]: finish get month data'

    print '[INFO]: start run hour'
    hour.run()
    print '[INFO]: finish run hour'

    print '[INFO]: start run day'
    day.run()
    print '[INFO]: finish run day'
    
    print '[INFO]: start run week'
    week.run()
    print '[INFO]: finish run week'
    
    print '[INFO]: start run month'
    month.run()
    print '[INFO]: finish run month'
    
    print '[HOUR]: traffic-{traffic_amount}, into-{into_store_amount}, into_rate-{into_store_rate}, new-{new}, old-{old}, sleep-{sleep}, low-{low}, mid-{mid}, high-{high}, bounce_rate-{bounce_rate}, deep_rate-{deep_rate}***'.format(traffic_amount = hour.traffic, into_store_amount = hour.into_store_amount, into_store_rate = hour.into_store_rate, new = hour.new, old = hour.old, sleep = hour.sleep, low = hour.low, mid = hour.mid, high = hour.high, bounce_rate = hour.bounce_rate, deep_rate = hour.deep_rate)
    print '[DAY]: traffic-{traffic_amount}, into-{into_store_amount}, into_rate-{into_store_rate}, new-{new}, old-{old}, sleep-{sleep}, low-{low}, mid-{mid}, high-{high}, bounce_rate-{bounce_rate}, deep_rate-{deep_rate}***'.format(traffic_amount = day.traffic, into_store_amount = day.into_store_amount, into_store_rate = day.into_store_rate, new = day.new, old = day.old, sleep = day.sleep, low = day.low, mid = day.mid, high = day.high, bounce_rate = day.bounce_rate, deep_rate = day.deep_rate)
    print '[WEEK]: traffic-{traffic_amount}, into-{into_store_amount}, into_rate-{into_store_rate}, new-{new}, old-{old}, sleep-{sleep}, low-{low}, mid-{mid}, high-{high}, bounce_rate-{bounce_rate}, deep_rate-{deep_rate}***'.format(traffic_amount = week.traffic, into_store_amount = week.into_store_amount, into_store_rate = week.into_store_rate, new = week.new, old = week.old, sleep = week.sleep, low = week.low, mid = week.mid, high = week.high, bounce_rate = week.bounce_rate, deep_rate = week.deep_rate)
    print '[MONTH]: traffic-{traffic_amount}, into-{into_store_amount}, into_rate-{into_store_rate}, new-{new}, old-{old}, sleep-{sleep}, low-{low}, mid-{mid}, high-{high}, bounce_rate-{bounce_rate}, deep_rate-{deep_rate}***'.format(traffic_amount = month.traffic, into_store_amount = month.into_store_amount, into_store_rate = month.into_store_rate, new = month.new, old = month.old, sleep = month.sleep, low = month.low, mid = month.mid, high = month.high, bounce_rate = month.bounce_rate, deep_rate = month.deep_rate)
    '''

    print '[INFO]: start hour!!!!!!!!!!!!!!!!!!!!'
    res_data = Hour()
    res_data.run()
    hour_traffic, hour_into_store_amount, hour_into_store_rate, hour_new, hour_old, hour_sleep, hour_low, hour_mid, hour_high, hour_bounce_rate, hour_deep_rate = res_data.traffic, res_data.into_store_amount, res_data.into_store_rate, res_data.new, res_data.old, res_data.sleep, res_data.low, res_data.mid, res_data.high, res_data.bounce_rate, res_data.deep_rate

    print '[INFO]: start day!!!!!!!!!!!!!!!!!!!!'
    res_data = Day()
    res_data.run()
    day_traffic, day_into_store_amount, day_into_store_rate, day_new, day_old, day_sleep, day_low, day_mid, day_high, day_bounce_rate, day_deep_rate = res_data.traffic, res_data.into_store_amount, res_data.into_store_rate, res_data.new, res_data.old, res_data.sleep, res_data.low, res_data.mid, res_data.high, res_data.bounce_rate, res_data.deep_rate

    print '[INFO]: start week!!!!!!!!!!!!!!!!!!!!'
    res_data = Week()
    res_data.run()
    week_traffic, week_into_store_amount, week_into_store_rate, week_new, week_old, week_sleep, week_low, week_mid, week_high, week_bounce_rate, week_deep_rate = res_data.traffic, res_data.into_store_amount, res_data.into_store_rate, res_data.new, res_data.old, res_data.sleep, res_data.low, res_data.mid, res_data.high, res_data.bounce_rate, res_data.deep_rate

    print '[INFO]: start month!!!!!!!!!!!!!!!!!!!!'
    res_data = Month()
    res_data.run()
    month_traffic, month_into_store_amount, month_into_store_rate, month_new, month_old, month_sleep, month_low, month_mid, month_high, month_bounce_rate, month_deep_rate = res_data.traffic, res_data.into_store_amount, res_data.into_store_rate, res_data.new, res_data.old, res_data.sleep, res_data.low, res_data.mid, res_data.high, res_data.bounce_rate, res_data.deep_rate

    print '[HOUR]: traffic-{traffic_amount}, into-{into_store_amount}, into_rate-{into_store_rate}, new-{new}, old-{old}, sleep-{sleep}, low-{low}, mid-{mid}, high-{high}, bounce_rate-{bounce_rate}, deep_rate-{deep_rate}***'.format(traffic_amount = hour_traffic, into_store_amount = hour_into_store_amount, into_store_rate = hour_into_store_rate, new = hour_new, old = hour_old, sleep = hour_sleep, low = hour_low, mid = hour_mid, high = hour_high, bounce_rate = hour_bounce_rate, deep_rate = hour_deep_rate)
    print '[DAY]: traffic-{traffic_amount}, into-{into_store_amount}, into_rate-{into_store_rate}, new-{new}, old-{old}, sleep-{sleep}, low-{low}, mid-{mid}, high-{high}, bounce_rate-{bounce_rate}, deep_rate-{deep_rate}***'.format(traffic_amount = day_traffic, into_store_amount = day_into_store_amount, into_store_rate = day_into_store_rate, new = day_new, old = day_old, sleep = day_sleep, low = day_low, mid = day_mid, high = day_high, bounce_rate = day_bounce_rate, deep_rate = day_deep_rate)
    print '[WEEK]: traffic-{traffic_amount}, into-{into_store_amount}, into_rate-{into_store_rate}, new-{new}, old-{old}, sleep-{sleep}, low-{low}, mid-{mid}, high-{high}, bounce_rate-{bounce_rate}, deep_rate-{deep_rate}***'.format(traffic_amount = week_traffic, into_store_amount = week_into_store_amount, into_store_rate = week_into_store_rate, new = week_new, old = week_old, sleep = week_sleep, low = week_low, mid = week_mid, high = week_high, bounce_rate = week_bounce_rate, deep_rate = week_deep_rate)
    print '[MONTH]: traffic-{traffic_amount}, into-{into_store_amount}, into_rate-{into_store_rate}, new-{new}, old-{old}, sleep-{sleep}, low-{low}, mid-{mid}, high-{high}, bounce_rate-{bounce_rate}, deep_rate-{deep_rate}***'.format(traffic_amount = month_traffic, into_store_amount = month_into_store_amount, into_store_rate = month_into_store_rate, new = month_new, old = month_old, sleep = month_sleep, low = month_low, mid = month_mid, high = month_high, bounce_rate = month_bounce_rate, deep_rate = month_deep_rate)

if __name__ == '__main__':
    main()
