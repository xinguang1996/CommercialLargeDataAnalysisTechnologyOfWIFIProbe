# -*- coding: utf-8 -*

import serial
import time
import datetime
import os
import re
import sys
import requests
import numpy as np
import matplotlib.pyplot as plt

# Sending interval
TIME = 3
# The http URL that sent the request
URL = '192.168.1.50'

# Define the data format that will get
# mac_phone --- 源MAC, 即手机MAC
# mac_wifi  --- 目的MAC
# data1     --- 帧类型
# data2     --- 帧子类型
# channel   --- 频道
# rssi      --- 信号强度
mac_phone = None
mac_wifi = None
data1 = None
data2 = None 
channel = None
rssi = None

myURL = "192.168.0.10"

a, b = 0, 0

struct = '%a %b %d %H:%M:%S %Y'       


# Open the serial
ser = serial.Serial("/dev/ttyAMA0", 115200)
serGPS = serial.Serial("/dev/ttyUSB0", 9600)

def mmac():
    pattern = re.compile('..:..:..:..:..:..')
    return pattern.findall(os.popen('ifconfig').read().split('\n')[0])

class GPS(object):
    """
    Get the position from the GPS model.
    lon --- 经度
    lat --- 纬度
    """
    def __init__(self):
        #self.lon_data = 0
        #self.lat_data = 0
        self.lon_data = 125.296056
        self.lat_data = 43.860721

    def __str__():
        return 'The class of getting GPS position by UART.'
    
    __repr__ = __str__

    #@property
    def run(self):
        while True:
            count = serGPS.inWaiting()
            if count != 0:
                recv = serGPS.read(count)
	    
                if 'RMC' in recv or 'GGA' in recv or 'GLL' in recv:
                    pattern = re.compile(r'\d{4,5}.\d{5},N,\d{4,5}.\d{5},E')
                    match = pattern.findall(recv)
                    if match:
                        self.lon_data = float(match[0].split(',')[2]) / 100
                        self.lat_data = float(match[0].split(',')[0]) / 100
                        print '[INFO]: lon - {lon}, lat - {lat}'.format(lon = lon_data, lat = lat_data)
                        break
                    else:
                        continue

            # clean up the cache
            serGPS.flushInput()
            time.sleep(0.1)

    @property
    def lon(self):
        return self.lon_data
    
    @property
    def lat(self):
        return self.lat_data
        
        

# Linear equation: a * |Rssi| + b = Distance
def getRelationship():
    a = [[63, 1], [93, 1]]
    a = np.array(a)
    b = [10, 100]
    b = np.array(b)
    x = np.linalg.solve(a, b)
	
    a = int(round(x[0], 2))
    b = int(round(x[1], 2))

    return a, b

def getDistance(rssi):
    #return a * rssi + b
    return (3 * rssi - 179)

def showCurve():
    x = np.array([62.00, 72.03, 77.31, 81.06, 83.97, 86.34, 90.01, 91.62, 93.00])
    y = np.arange(10, 110, 10)

    z1 = np.polyfit(x, y, 3)
    p1 = np.poly1d(z1)
    yvals = p1(x)
    plot1 = plt.plot(x, y, '*', label = 'original values')
    plot2 = plt.plot(x, yvals, 'r', label = 'polyfit values')
    plt.xlabel('x axis Rssi')
    plt.xlabel('y axis Distance')
    plt.legend(loc = 4)
    plt.title('polyfitting')
    plt.savefig('curve.png')
    plt.show()

def main():
    my_mmac = mmac()[0]

    gps = GPS()

    a, b = getRelationship()
    while True:
        # Get the str in cache
        count = ser.inWaiting()
        if count != 0:
            recv = ser.read(count)
	    recv_list = recv.split('\n')
	    #print recv_list
	    for i in recv_list:
		if len(i) == 49:
                        this_time = datetime.datetime.now().strftime(struct)
			
                        mac_phone, mac_wifi, data1, data2, channel, rssi = i.split('|')

		        distance = getDistance(abs(int(rssi)))		
			
                        print 'Time: {time}, ProbeMac: {my_mmac}, Mac: {mac_phone}, Rssi: {rssi}, Distance: {distance}, Lon: {lon}, Lat: {lat}'.format(time = this_time, my_mmac = my_mmac, mac_phone = mac_phone, rssi = abs(int(rssi)), distance = distance, lon = gps.lon, lat = gps.lat)
    
                # POST requests 
                myjson = MkJson(mmac = my_mmac, lon = gps.lon, lat = gps.lat, data = data)
                requests.post(url, myjson.get_json)

        # clean up the cache
        ser.flushInput()
        time.sleep(0.1)

if __name__ == '__main__':
    try:
        main()
    except KeyboardInterrupt:
        if ser != None:
            ser.close()
        
