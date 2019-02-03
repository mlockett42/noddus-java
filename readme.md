The start this up

`docker-compose build`

Creates the docker container

`docker-compose run mark-java compile`

Runs the Java compiler

`docker-compose run mark-java clean`

Cleans up the build temp files

`docker-compose up mark-java`

Runs the web server. The relevant URL to post to is http://127.0.0.1:8000/test
The body of the request should be the JSON

The postman directory contains a postman file which runs this request.
