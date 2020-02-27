#!/usr/bin/env bash
mvn clean package -U

mkdir ${BUILD_ROOT}/bin
mv mix-recall-server/target/mix-recall-server-1.0-SNAPSHOT.jar ${BUILD_ROOT}/bin/
mv run_server.sh ${BUILD_ROOT}/bin/
mv exclude.txt ${BUILD_ROOT}