#!/bin/bash
PING='ping -c 3 $1 | grep '0 received' | wc -l'
echo $PING:
