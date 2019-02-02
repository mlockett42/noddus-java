#!/bin/bash
if [ "$1" = "run" ]
then
    java -cp '.:lib/json-20180813.jar'  WebServer
else
    make $1
fi
