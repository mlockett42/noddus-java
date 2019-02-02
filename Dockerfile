# Build / run:
# docker build --tag="${PWD##*/}" .
# docker run --tty --interactive --volume "${PWD}":/opt/project --publish=8000:8000 "${PWD##*/}"
# docker run --tty --interactive --volume "${PWD}":/opt/project --entrypoint="bash" "${PWD##*/}"
# Cleanup:
# docker rm $(docker ps --all --quiet)
# docker rmi $(docker images --quiet --filter "dangling=true")
# docker volume rm $(docker volume ls --quiet)
# docker network rm $(docker network ls --quiet)

FROM ubuntu:18.04

ENV last_update 20190202


# Install required packages

RUN apt-get update && apt-get install -y software-properties-common
RUN add-apt-repository --yes ppa:webupd8team/java
RUN apt-get update --quiet --yes && apt-get install --quiet --yes --force-yes ca-certificates && \
  echo oracle-java7-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections && \
  apt-get install --yes oracle-java8-installer && \
  apt-get install --yes build-essential

# Entrypoint
# Also need
EXPOSE 8000-8100
WORKDIR /opt/project/
ENTRYPOINT ["make"]
CMD ["classes"]
