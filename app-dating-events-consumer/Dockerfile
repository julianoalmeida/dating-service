# Usa a imagem oficial do OpenJDK 21 com JDK completo
FROM openjdk:21-jdk

MAINTAINER dating-devs

LABEL source="https://github.com/dating/dating-service" \
      maintainer="dating-devs" \
      author="dating-devs"

ADD ./app-dating-events-consumer/build/libs/*.CONSUMERS.jar /app.jar

## @TODO depois precisa criar certificados quando for subir nos ambientes.
#ADD ./certs/ /tmp/certs

RUN adduser dating

USER dating

ENTRYPOINT exec java $JAVA_OPTS -jar app.jar
