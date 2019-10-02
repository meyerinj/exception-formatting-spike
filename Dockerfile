FROM openjdk:8u171-alpine3.7
RUN apk --no-cache add curl
COPY build/libs/*-all.jar qr-code-demo.jar
CMD java ${JAVA_OPTS} -jar qr-code-demo.jar