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

def into_amount(es_rdd):
    # Threshold value
    INTO = 50

    def get_range(x):
	for value in x['data']:
            yield value['range']

    # Return format: [(time, range_amount), (time, range_amount), ..., (str, int)]
    def get_into_amount(x):
	acount = 0
	for i in x:
	    if i < INTO:
                acount += 1
	return acount

    into_rdd = es_rdd.mapValues(get_range)

    return into_rdd.mapValues(get_into_amount).values().sum()

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
    
    into = into_amount(es_rdd)
    print into

if __name__ == '__main__':
    main()
'''
