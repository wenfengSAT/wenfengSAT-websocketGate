FROM amazoncorretto:11-alpine

MAINTAINER "wenfengSAT@163.com"

ARG WORK_DIR=/data

ENV SERVER_PORT=8080

WORKDIR ${WORK_DIR}
ADD ./target/*.jar  ${WORK_DIR}/demo-apigate.jar

RUN mkdir ${WORK_DIR}/config
COPY ./target/classes/*.xml         ${WORK_DIR}/config/
COPY ./target/classes/*.properties  ${WORK_DIR}/config/

EXPOSE ${SERVER_PORT}

VOLUME ["${WORK_DIR}/config", "${WORK_DIR}/logs"]

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dserver.port=${SERVER_PORT}", "-jar","demo-apigate.jar"]