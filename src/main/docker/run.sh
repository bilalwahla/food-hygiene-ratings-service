#!/bin/sh

echo "********************************************************"
echo "Starting Food Hygiene Rating Service"
echo "********************************************************"
java -Djava.security.egd=file:/dev/./urandom -Dserver.port=$SERVER_PORT   \
     -Dspring.profiles.active=$PROFILE -jar /usr/local/food-hygiene-ratings-service/@project.build.finalName@.jar
