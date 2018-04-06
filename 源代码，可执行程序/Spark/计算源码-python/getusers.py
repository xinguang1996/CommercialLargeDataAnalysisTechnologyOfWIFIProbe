#coding: utf-8

'''
Match: 2017 China Software Cup
Topic: Commercial large data analysis technology based on WIFI probe.
School: CCUT(Changchun University Of Technology)
Date: 2017.03 - 2017-08
Team: teamName   --- Victors
	  teamLeader --- Jiahui Tang
	  teamMember --- Pengyue Zhao
	  teamMember --- Xinguang Guo
'''

import datetime
import traceback
from elasticsearch import Elasticsearchs
from pyspark import SparkContext, SparkConf

conf = SparkConf().setAppName("WifiProbeBigData")
sc = SparkContext(conf = conf)

class GetUsers(object):
    '''Get the users information from Elasticsearch in the user_table.'''
    def __init__(self, store_id, dimension, json)：
        self.time = datetime.datetime.now().strftime('%Y.%m.%d_%H')
        
        self.users = {}
        self.data = []
        self.exc = None
        
        self.es_nodes = '192.168.1.10'
        self.es_port = '9200'
        self.es_index = 'user_table'
        self.es_resource = self.es_index

    def __str__(self):
        return 'Parameters:\n
                <store_id>: The id of the store that counts.\n
                <dimension>: The time dimension of this calculate.\n
                <json>: JSON file that need to be converted to sparkRDD to be stored in the datebase.
                '        
    __repr__ = __str__

    def run(self):
        es_read_conf = {
            'es.nodes': self.es_nodes,
            'es.port': self.es_port,
            'es.resource': self.es_resource,
            'es.query': '{}'
            }

        es_rdd = sc.newAPIHadoopRDD(
                inputFormatClass = 'org.elasticsearch.hadoop.mr.EsInputFormat',
                keyClass = 'org.apache.hadoop.io.NullWritable',
                valueClass = 'org.elasticsearch.hadoop.mr.LinkedMapWritable',
                conf = es_read_conf
                )

        # Convert from sparkRDD to PythonList 
        #user_table = es_rdd.collect()
        
        # The function to get the probeID.
        # Is equivalent to 'lambda x: x['wid']'.
        def get_probe_id(x):
            return x['wid']
        
        # Format: [(user_id, probe_id)]
        id_rdd = es_rdd,mapValue(get_probe_id).collect()
        
        return id_rdd

def main():
	print datetime.datetime.now()

	json = {'The new and old customers': {'new': '200', 'old': '300'}, 'Visiting cycle': '5', 'Bounce rate': '0.2', 'The amount of store': '500', 'Deep rate': '0.3', 'Customer active': {'Sleep activity': '200', 'Mid activity': '100', 'High activity': '100', 'Low activity': '100'}, 'The resident time': '5', 'Traffic amount': '1000', 'Into the store rate': '0.5'}
	

	#index = '2017-5-11-15'
	#doc_type = 'hour'


	test = PyElasticsearchHour(json)

	try:
		test.init()
		test.save()
	except:
		pass

if __name__ == '__main__':
	main()
