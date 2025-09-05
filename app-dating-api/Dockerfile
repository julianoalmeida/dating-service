# Usa a imagem oficial do OpenJDK 21 com JDK completo
FROM openjdk:21-jdk

MAINTAINER dating-devs

LABEL source="https://github.com/dating/dating-service" \
      maintainer="dating-devs" \
      author="dating-devs"

ADD ./app-dating-api/build/libs/*.API.jar /app.jar

USER 65000

ENTRYPOINT exec java $JAVA_OPTS -jar app.jar
