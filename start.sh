#!/bin/bash
if [ "$1" = "run" ]
then
    java WebServer
else
    make $1
fi
