[![Build Status](https://travis-ci.org/bilalwahla/food-hygiene-ratings-service.svg?branch=master)](https://travis-ci.org/bilalwahla/food-hygiene-ratings-service)

# Food Hygiene Ratings Service

NOTE: if you have received similar work as part of job interview  process for a developer role, beware that this work (done in 5-6 hours) might be considered "technically not strong enough" to get you the job.

The project consists of only one microservice.

1.  food-hygiene-ratings-service that allows you to see how food hygiene ratings are distributed by percentage across a selected Local Authority so that you can understand the profile of establishments in that authority.

# Software needed to manually build and run
1.  [Docker] (http://docker.com).

## Manually building the Docker Image
To build the project as a docker image, open a command-line window change to the directory where you have downloaded the project source code.

Run the following maven command. This command will execute the [Spotify docker plugin](https://github.com/spotify/docker-maven-plugin) defined in the pom.xml file.  

   **./mvnw clean test package docker:build**

If everything builds successfully you should see a message indicating that the build was successful.

## And then manually running the service

I have created a docker-compose file that we can use to start the actual image.  To start the docker image,
change to the docker-compose directory in this project's source code and issue the following docker-compose command:

   **docker-compose -f docker/common/docker-compose.yml up**

If everything starts correctly you should see a bunch of Spring Boot information fly by on standard out.  At this point the service will be up and running.

# Available endpoints on this service

## Retrieve authorities

   **GET localhost:8080/authorities**

## Retrieve ratings

Was developed to be used by the main endpoint to calculate food hygiene rating percentage by authority.

   **GET localhost:8080/ratings**

## Calculate FHRS Percentage By Authority

   **GET localhost:8080/authorities/{localAuthorityId}/ratingsPercentage**
