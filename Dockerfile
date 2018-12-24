FROM ubuntu:16.04 

MAINTAINER Vladislav Alehin

RUN apt-get update
RUN apt-get install -y openjdk-8-jdk-headless
RUN apt-get install -y maven

# Копируем исходный код в Docker-контейнер
ENV WORK /opt/Android_serv
ADD serv/ $WORK/serv/

# Собираем и устанавливаем пакет
WORKDIR $WORK/serv

#
# Запускаем  сервер
#
CMD mvn package && java -jar $WORK/serv/target/server-0.0.1-SNAPSHOT.jar

