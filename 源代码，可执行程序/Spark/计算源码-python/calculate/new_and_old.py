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
'''
#import the necessary packages
from pyspark import SparkContext, SparkConf
import copy
import json
import re
import time
import datetime
#import numpy as np
#import pandas
#import matplotlib
#from elasticsearch import Elasticsearch


#import the packages of mine
from mytime import *

import sys
#sys.path.append('../')

#from base import Counter

# set the SPARK
spark_conf = SparkConf().setAppName('WifiProbeBigData')
sc = SparkContext(conf = spark_conf)
'''
def new_and_old(es_rdd):
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
    #return '1', '2'
'''
def main():
    #now = datetime.datetime.now()
    #now = datetime.datetime(2017, 5, 21, 22, 0, 0, 0)
    now = datetime.datetime(2017, 8, 1, 0, 0, 0, 0)

    interval_hour = datetime.timedelta(hours = 1)
    interval_day = datetime.timedelta(days = 1)
    interval_week = datetime.timedelta(weeks = 1)
    interval_month = datetime.timedelta(days = 30)

    es_read_conf = {
        "es.nodes" : "192.168.1.52",
 	"es.port" : "9200",
	"es.resource" : "sou/tz_1",   #源数据索引/type
	"es.query": '{"range": {"@timestamp": {"gte": "2017-07-30T00:00:00", "lt": "2017-08-30T00:00:00"}}}'
        }

    es_rdd = sc.newAPIHadoopRDD(
        inputFormatClass = "org.elasticsearch.hadoop.mr.EsInputFormat",
        keyClass = "org.apache.hadoop.io.NullWritable",
        valueClass = "org.elasticsearch.hadoop.mr.LinkedMapWritable",
        conf = es_read_conf
        )
    
    data = new_and_old(es_rdd)
    print data

if __name__ == '__main__':
    main()
'''
