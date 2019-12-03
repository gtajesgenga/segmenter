#!/usr/bin/env bash

set -e

echo "PORT=$PORT, JAVA_OPS=$JAVA_OPS"
./mvnw spring-boot:run -Dserver.port=$PORT $JAVA_OPTS