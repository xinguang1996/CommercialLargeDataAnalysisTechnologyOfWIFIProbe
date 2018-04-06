# Author: Jiahui Tang
# Date: 2017-3-15
# Project: SMS control WIFI probe by Raspberry Pi, Arduino mega 2560 and SIM800 module

#import the necessary packages
import RPi.GPIO as GPIO
import serial
import traceback
import time
import datetime
import threading
import socket
import json
import os

#import the local packages
import const

#define the const
const.WIFI = 7

#define a global variable mark the status of the WIFI probe
#status 1 ---- on
#status 2 ---- off
status = False

#define the automatic switch time
automatic_boot_hour = None
automatic_boot_minute = None
automatic_shutdown_hour = None
automatic_shutdown_minute = None 

#initialization serial
port = "/dev/ttyACM0"
serial_from_arduino = serial.Serial(port, 9600)

#define the used for initialization gpio function 
def gpio_init():
	GPIO.setmode(GPIO.BOARD)
	GPIO.setup(const.WIFI, GPIO.OUT)

def send_message_success(msg):
	serial_from_arduino.write(msg)

def send_message_failure(msg):
	serial_from_arduino.write(msg)

def wechat():
	global status
	s1 = socket.socket()	
	host = '192.168.1.201'
	port = 12345
	s1.bind((host,port))
	while True:
		gpio_init()
		s1.listen(5)
		cron,address = s1.accept()
		info = cron.recv(1024)
		if info == 'boot':
			if status == False:
				GPIO.output(const.WIFI, GPIO.HIGH)
				status = True
				cron.send('yes')
			else:
				cron.send('no')
		
		elif info == 'shutdown':
			if status == True:
				GPIO.output(const.WIFI, GPIO.LOW)
				status = False
				cron.send('yes')
			else:
				cron.send('no')
		
			
		elif info == 'reboot':
			if status == True:
				GPIO.output(const.WIFI, GPIO.LOW)
				time.sleep(1)
				GPIO.output(const.WIFI, GPIO.HIGH)
				cron.send('yes')
			else:
				cron.send('no')
		
		else:
			pass

def web():
	global status
	s2 = socket.socket()
	host = '192.168.1.201'
	port = 12346
	s2.bind((host, port))
	while True:
		gpio_init()
		s2.listen(5)
		cron, address = s2.accept()
		info = cron.recv(1024)
		if info == 'boot':
			if status == False:
				GPIO.output(const.WIFI, GPIO.HIGH)
				status = True
				cron.send('yes')
			else:
				cron.send('no')
		
		elif info == 'shutdown':
			if status == True:
				GPIO.output(const.WIFI, GPIO.LOW)
				status = False
				cron.send('yes')
			else:
				cron.send('no')
		
		elif info == 'reboot':
			if status == True:
				GPIO.output(const.WIFI, GPIO.LOW)
				time.sleep(1)
				GPIO.output(const.WIFI, GPIO.HIGH)
				cron.send('yes')
			else:
				cron.send('no')

		else:
			pass

def automatic():
	global status
	
	global automatic_boot_hour
	global automatic_boot_minute
	global automatic_shutdown_hour
	global automatic_shutdown_minute

	while True:
		try:
			gpio_init()
			now = datetime.datetime.now()
#			to_path = '/home/pi/apache-tomcat-7.0.75/webapps/softbei_wifi/Automatic/'
#			cmd1 = 'scp ../Automatic/automatic.json pi@192.168.1.250:{to_path}'.format(to_path = to_path)
#			cmd2 = 'scp ../Automatic/automatic.json pi@192.168.1.251:{to_path}'.format(to_path = to_path)
#			cmd3 = 'scp ../Automatic/automatic.json pi@192.168.1.252:{to_path}'.format(to_path = to_path)
#			os.system(cmd1)
#			os.system(cmd2)
#			os.system(cmd3)
			with open('../Automatic/automatic.json') as json_file:
				data = json.load(json_file)
			
				if data['boot']:
					automatic_boot_hour, automatic_boot_minute = data['boot'].split(':')
				else:
					automatic_boot_hour = None
					automatic_boot_minute = None
		
				if data['shutdown']:	
					automatic_shutdown_hour, automatic_shutdown_minute = data['shutdown'].split(':')
				else:
					automatic_shutdown_hour = None
					automatic_shutdown_hour = None				

			if automatic_boot_hour:
				automatic_boot_hour = int(automatic_boot_hour)
			else:
				pass
		
			if automatic_boot_minute:
				automatic_boot_minute = int(automatic_boot_minute)
			else:
				pass
		
			if automatic_shutdown_hour:		
				automatic_shutdown_hour = int(automatic_shutdown_hour)
			else:
				pass
		
			if automatic_shutdown_minute:	
				automatic_shutdown_minute = int(automatic_shutdown_minute)
			else:
				pass		
	
			#automatic switch
			if now.hour == automatic_boot_hour and now.minute == automatic_boot_minute:
				if status == False:
					GPIO.output(const.WIFI, GPIO.HIGH)
					status = True
				else:
					pass
			elif now.hour == automatic_shutdown_hour and now.minute == automatic_shutdown_minute:
				if status == True:
					GPIO.output(const.WIFI, GPIO.LOW)
					status = False
				else:
					pass
			else:
				pass

		except Exception, e:
			 print e
							
		
		
				
	 	

threads = []
t1 = threading.Thread(target = wechat, args = ())
t2 = threading.Thread(target = web, args = ())
t3 = threading.Thread(target = automatic, args = ())
threads.append(t1)
threads.append(t2)
threads.append(t3)



#the main function
def main():
#	serial_init()
	
	gpio_init()

	global status

	for i in threads:
		i.setDaemon(True)
		i.start()
	try:
		while True:
			gpio_init()
			sim_read = serial_from_arduino.read()

			#test the value
			print sim_read

			#the boot control
			if sim_read == '1':
				if status == False:
					GPIO.output(const.WIFI, GPIO.HIGH)
					
					status = True

					send_message_success('1')
					
				elif status == True:
					#
					send_message_failure('2')
				else:	
					pass #raise error 
				
			#the shutdown control
			elif sim_read == '2':
				if status == True:
					GPIO.output(const.WIFI, GPIO.LOW)

					status = False

					send_message_success('3')

				elif status == False:
					#
					send_message_failure('4')
				else:
					pass #raise error

			#the reboot control
			elif sim_read == '3':
				if status == True:
					GPIO.output(const.WIFI, GPIO.LOW)
					time.sleep(1)
					GPIO.output(const.WIFI, GPIO.HIGH)

					send_message_success('5')

				elif status == False:
					send_message_failure('6')
				else:
					pass #raise error

			else:
				pass

	except Exception, e:
		 print Exception, ':', e

if __name__ == '__main__':
	main()
