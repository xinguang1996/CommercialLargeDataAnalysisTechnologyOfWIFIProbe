# Author: Jiahui Tang
# Date: 2017-3-15
# Project: SMS control WIFI probe by Raspberry Pi, Arduino mega 2560 and SIM800 module

#import the necessary packages
import RPi.GPIO as GPIO
import serial
import traceback
import time

#import the local packages
import const

#define the const
const.WIFI = 7

#define a global variable mark the status of the WIFI probe
#status 1 ---- on
#status 2 ---- off
status = True

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

#the main function
def main():
	#serial_init()
	
	gpio_init()

	global status

	try:
		while True:
			sim_read = serial_from_arduino.read()

			#test the value
			print sim_read

			#the boot control
			if sim_read == '1':
				if status == False:
					GPIO.output(const.WIFI, GPIO.HIGH)
					
					status = True

					#boot successfully
					send_message_success('1')
					
				elif status == True:
					#boot failure
					send_message_failure('2')
				else:	
					raise ValueError
				
			#the shutdown control
			elif sim_read == '2':
				if status == True:
					GPIO.output(const.WIFI, GPIO.LOW)

					status = False

					#shutdown successfully
					send_message_success('3')

				elif status == False:
					#shutdown failure
					send_message_failure('4')
				else:
					raise ValueError

			#the reboot control
			elif sim_read == '3':
				if status == True:
					GPIO.output(const.WIFI, GPIO.LOW)
					time.sleep(1)
					GPIO.output(const.WIFI, GPIO.HIGH)
					
					#reboot successfully
					send_message_success('5')

				elif status == False:
					#reboot failure
					send_message_failure('6')
				else:
					raise ValueError

			else:
				pass

	except Exception, e:
		 print Exception, ':', e

if __name__ == '__main__':
	main()
