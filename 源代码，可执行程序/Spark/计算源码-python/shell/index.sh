#!/bin/bash

COLOR_G="\x1b[0;32m"  # green 
COLOR_R="\x1b[1;31m"  # red 
RESET="\x1b[0m"

STR_ERR="[Oops!! Error occurred!! Please see the message upside!!]"
STR_OK="[Job done!]"

MSG_ERR=$COLOR_R$STR_ERR$RESET
MSG_OK=$COLOR_G$STR_OK$RESET


myexit()
{
if [ $1 -eq "1" ]
then
	echo -e $MSG_ERR 
	exit
fi
}

#sudo python hebin.py
echo `date`

if [ $? -eq 0 ]
then
	#run successfully
	echo -e $MSG_OK
	
	#sudo $SPARK_HOME/bin/spark-submit index3.py
	echo 'okokok'
else
	#run failure
	echo -e $MSG_ERR	
	
	exit
fi
