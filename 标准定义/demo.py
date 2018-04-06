#!coding:utf-8
from pyspark import SparkContext, SparkConf
import json
from pyspark.sql import SQLContext

if __name__ == "__main__":
        conf = SparkConf().setAppName("ESTest")
        sc = SparkContext(conf=conf)
        es_read_conf = {
                "es.nodes" : "192.168.1.10",
                "es.port" : "9200",
                "es.resource" : "sou/tz_1",     #源数据索引/type
                "es.query":'{"range":{"@timestamp":{"gte":"2017-07-30T00:00:00","lt":"2017-07-31T00:00:00"}}}'  #所要筛选的时间范围
        }	#注意es.query必须是字符串，因此先要通过json库将python字典转化为字符串
        es_rdd = sc.newAPIHadoopRDD(
                inputFormatClass = "org.elasticsearch.hadoop.mr.EsInputFormat",
                keyClass = "org.apache.hadoop.io.NullWritable",
                valueClass = "org.elasticsearch.hadoop.mr.LinkedMapWritable",
				conf = es_read_conf
        )
        #es_rdd数据格式为[({"id":{原始数据}})]
        es_write_conf = {
                "es.nodes" : "192.168.1.10",
                "es.port" : "9200",
                "es.resource" : "test/tz_1"     #储存位置：索引/type
        }
        #存储格式为[({"id":{结果数据}})]
        s = [(1,{"bb":"test2"})]
        value_counts = sc.parallelize(s)

        value_counts.saveAsNewAPIHadoopFile(
                path = '-',
                outputFormatClass = "org.elasticsearch.hadoop.mr.EsOutputFormat",
                keyClass = "org.apache.hadoop.io.NullWritable",
                valueClass="org.elasticsearch.hadoop.mr.LinkedMapWritable",
                conf=es_write_conf
        )
