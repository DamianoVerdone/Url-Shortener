FROM amazoncorretto:11

RUN \
    mkdir /tmp/shortnerUrl && \
	cd /tmp/shortnerUrl

ENV DCKR_APP_HOME=/tmp/shortnerUrl
ENV ENV_NAME=docker

WORKDIR ${DCKR_APP_HOME}

COPY  ./build/libs/server-0.0.1.jar ${DCKR_APP_HOME}/server.jar



ENTRYPOINT java -jar server.jar $ENV_NAME
