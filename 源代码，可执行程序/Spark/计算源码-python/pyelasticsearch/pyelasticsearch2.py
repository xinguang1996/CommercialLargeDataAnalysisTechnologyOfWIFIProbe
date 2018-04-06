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

class EsHadoopWrite(object):
    '''The class that stores the results to elasticsearch by Es-Hadoop.'''
    def __init__(self, store_id, dimension, json)：
        self.time = datetime.datetime.now().strftime('%Y.%m.%d_%H')
        self.store_id = store_id
        self.dimension = dimension
        self.json = json
        self.data = []
        self.exc = None
        
        self.es_nodes = '192.168.1.10'
        self.es_port = '9200'
        self.es_index = 'res'

        self.es_write_conf = {
                'es.nodes': self.es_nodes,
                'es.port': self.es_port,
                'es.resource': self.es_index + '/' + self.es_doc_type
                }

    def __str__(self):
        return 'Parameters:\n
                <store_id>: The id of the store that counts.\n
                <dimension>: The time dimension of this calculate.\n
                <json>: JSON file that need to be converted to sparkRDD to be stored in the datebase.
                '        
    __repr__ = __str__

    @property
    def es_doc_type(self):
        '''Return the doc_type.'''
        return self.store_id + '-' + self.dimension

    @property
    def es_resource(self):
        '''Return the es_resource.'''
        return self.es_index + '/' + self.es_doc_type

    def run(self):
        es_write_conf = {
            'es.nodes': self.es_nodes,
            'es.port': self.es_port,
            'es.resource': self.es_resource
            }

        # converted to sparkRDD
        self.data = [(self.time, self.json)]
        value_counts = sc.parallelize(self.data)
        
        value_counts.saveAsNewAPIHadoopFile(
                path = '-',
                outputFormatClass = 'org.elasticsearch.hadoop.mr.EsOutputFormat',
                keyClass = 'org.apache.hadoop.io.NullWritable',
                valueClass = 'org.elasticsearch.hadoop.mr.LinkedMapWritable',
                conf = es_write_conf
                )

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
