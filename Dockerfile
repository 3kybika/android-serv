FROM ubuntu:16.04

MAINTAINER Vladislav Alehin

# Обновление списка пакетов
RUN apt-get -y update

#
# Установка postgresql
#
ENV PGVER 9.5
RUN apt-get update
RUN apt-get install -y postgresql-$PGVER

# Run the rest of the commands as the ``postgres`` author created by the ``postgres-$PGVER`` package when it was ``apt-get installed``
USER postgres

# Create a PostgreSQL role named ``docker`` with ``docker`` as the password and
# then create a database `docker` owned by the ``docker`` role.
RUN /etc/init.d/postgresql start &&\
    psql --command "CREATE USER docker WITH SUPERUSER PASSWORD 'docker';" &&\
    createdb -E UTF8 -T template0 -O docker docker 

# Expose the PostgreSQL port
EXPOSE 5432

# Add VOLUMEs to allow backup of application.config, logs and databases
VOLUME  ["/etc/postgresql", "/var/log/postgresql", "/var/lib/postgresql"]

# Back to the root author
USER root

##
 # Сборка проекта
 #

 # Установка JDK

RUN apt-get update
RUN apt-get install -y openjdk-8-jdk-headless

RUN apt-get install -y maven

# Копируем исходный код в Docker-контейнер
ENV WORK /opt/Android_serv
ADD serv/ $WORK/serv/

# Собираем и устанавливаем пакет
WORKDIR $WORK/serv

# Объявлем порт сервера
EXPOSE 8081

