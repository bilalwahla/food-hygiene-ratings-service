echo "Building with travis release: $BUILD_NAME ..."
./mvnw clean package docker:build
