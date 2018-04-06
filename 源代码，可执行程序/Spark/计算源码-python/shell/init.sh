#!/bin/bash

COLOR_G="\x1b[0;32m"  # green 
COLOR_R="\x1b[1;31m"  # red 
RESET="\x1b[0m"

STR_ERR_ES="[Error! Start es failure! Please see the message upside!!]"
STR_ERR_SPARK="[Error! Start spark failure! Please see the message upside!!]"
STR_OK_ES="[Job done! Start es successfully!]"
STR_OK_SPARK="[Job done! Start spark successfully!]"

MSG_ERR_ES=$COLOR_R$STR_ERR_ES$RESET
MSG_ERR_SPARK=$COLOR_R$STR_ERR_SPARK$RESET
MSG_OK_ES=$COLOR_G$STR_OK_ES$RESET
MSG_OK_SPARK=$COLOR_G$STR_OK_SPARK$RESET


myexit()
{
if [ $1 -eq "1" ]
then
	echo -e $MSG_ERR 
	exit
fi
}

echo `date`
start-es

if [ $? -eq 0 ]
then
	#start es successfully
	echo -e $MSG_OK_ES
	
	start-spark
	
	if [ $? -eq 0 ]
	then
		#start spark successfully
		echo -e $MSG_OK_SPARK

	else
		#start spark failure
		echo -e $MSG_ERR_SPARK

		exit
	fi
else
	#run failure
	echo -e $MSG_ERR_ES	
	
	exit
fi
