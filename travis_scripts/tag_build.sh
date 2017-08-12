echo "Tagging build with $BUILD_NAME"
export TARGET_URL="https://api.github.com/repos/bilalwahla/food-hygiene-ratings-service/releases?access_token=$GITHUB_TOKEN"

body="{
  \"tag_name\": \"$BUILD_NAME\",
  \"target_commitish\": \"master\",
  \"name\": \"$BUILD_NAME\",
  \"body\": \"FHRS release version: $BUILD_NAME\",
  \"draft\": false,
  \"prerelease\": false
}"

curl -k -X POST \
  -H "Content-Type: application/json" \
  -d "$body" \
  $TARGET_URL
