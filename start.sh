#!/bin/bash
if [ "$1" = "run" ]
then
    java A
else
    make $1
fi
