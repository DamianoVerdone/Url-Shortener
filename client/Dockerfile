FROM amazoncorretto:11

ARG version="*"

RUN \
    mkdir /tmp/liquibase && \
	cd /tmp/liquibase

ENV DCKR_APP_HOME=/tmp/shortnerUrl
ENV ENV_NAME=docker

WORKDIR ${DCKR_APP_HOME}

COPY ./build/libs/client-$version.jar ${DCKR_APP_HOME}/client.jar

COPY src/envs ${DCKR_APP_HOME}/envs


ENTRYPOINT "java -jar cleint.jar $ENV_NAME"
