#!/bin/bash

port=`ls -1 /dev`;echo "Insérer le périphérique USB puis appuyer sur 'Entrée'";read n;port2=`ls -1 /dev`;diff <(echo "$port") <(echo "$port2") | sed "s/> /\/dev\//g"
