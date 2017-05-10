#!/bin/bash
echo " " > extracted.log 
ls update/*.zip|awk -F".zip" '{print "unzip -o "$0" -d "$1}'|sh > extracted.log
chmod 777 -R update/*
var=$(find update/ -print | grep -i '.*[.]sh')
./$var > upgrade.log
#rm -rf update/*
