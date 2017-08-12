echo "Pushing FHRS docker images to docker hub ..."
docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD
docker push df/food-hygiene-ratings-service:$BUILD_NAME
