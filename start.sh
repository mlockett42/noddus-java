#!/bin/bash
if [ "$1" = "run" ]
then
    java -cp '.:protobuf_build/:lib/json-20180813.jar:lib/protobuf-java-3.6.1.jar'  WebServer
else
    make $1
fi
