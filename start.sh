#!/bin/bash
sudo service postgresql start 
mvn package
java -jar $WORK/serv/target/demo-0.0.1-SNAPSHOT.jars