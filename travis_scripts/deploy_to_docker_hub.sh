echo "Pushing FHRS docker images to docker hub ..."
docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD
docker push bilalwahla/df-fhrs:$BUILD_NAME
