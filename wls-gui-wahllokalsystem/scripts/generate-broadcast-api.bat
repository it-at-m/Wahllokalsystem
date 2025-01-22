@echo off
openapi-generator-cli generate -i ../src/resources/openapis/openapi.broadcast.0.2.0.json -g typescript-fetch -o ../src/api/wls-clients/generated-broadcast-api --template-dir ../src/api/wls-clients/custom-openapi-template-files
