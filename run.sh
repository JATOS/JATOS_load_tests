#!/bin/bash

# Gatling logs are in jatos_load_test.log

gatlingCmd="./gatling-charts-highcharts-bundle-3.2.1/bin/gatling.sh"

run () {
   export JATOS_CPUS=$1
   export JATOS_MEM=$2
   export JAVA_XMX=$3
   export JATOS_DB_CONNECTIONPOOL_SIZE=$4
   export JATOS_THREADPOOL_SIZE=$5
   export JATOS_HOST=$6
   export JATOS_STUDY_CODE=$7
   export SIMULATION=$8
   export INJECT_TYPE=$9
   export USERS=${10}
   export DURATION=${11}
   export SUCCESS_RATE=${12}
   description="cpus:$JATOS_CPUS mem:$JATOS_MEM javaXmx:$JAVA_XMX dbPoolSize:$JATOS_DB_CONNECTIONPOOL_SIZE threadPoolSize:$JATOS_THREADPOOL_SIZE $JATOS_HOST $JATOS_STUDY_CODE $SIMULATION $INJECT_TYPE users:$USERS duration:$DURATION successRate:$SUCCESS_RATE"
   echo -n $description
   # Shutdown before start to be sure
   docker compose -f ~/Downloads/JATOS_with_docker_compose-main/compose.yaml down > /dev/null 2>&1
   docker compose -f ~/Downloads/JATOS_with_docker_compose-main/compose.yaml up --wait > /dev/null 2>&1
   eval "$gatlingCmd -s $SIMULATION -rd '$description' -nr" >> jatos_load_test.log 2>&1 && echo " OK" || echo " KO"
   docker compose -f ~/Downloads/JATOS_with_docker_compose-main/compose.yaml down > /dev/null 2>&1
}


# run cpus mem javaXmx dbPoolSize threadPoolSize host studyCode simulation injectType users duration fail
run 2.0 2G 750M 75 112 www.example.com B64GEqy93Fe JatosDefaultSimulation atOnceUsers 60 600 100
run 2.0 2G 750M 75 112 www.example.com B64GEqy93Fe JatosDefaultSimulation atOnceUsers 70 600 100
run 2.0 2G 750M 75 112 www.example.com B64GEqy93Fe JatosDefaultSimulation atOnceUsers 80 600 100
run 2.0 2G 750M 75 112 www.example.com B64GEqy93Fe JatosDefaultSimulation atOnceUsers 90 600 100
run 2.0 2G 750M 75 112 www.example.com B64GEqy93Fe JatosDefaultSimulation atOnceUsers 100 600 100

run 2.0 2G 750M 100 150 www.example.com B64GEqy93Fe JatosDefaultSimulation atOnceUsers 60 600 100
run 2.0 2G 750M 100 150 www.example.com B64GEqy93Fe JatosDefaultSimulation atOnceUsers 70 600 100
run 2.0 2G 750M 100 150 www.example.com B64GEqy93Fe JatosDefaultSimulation atOnceUsers 80 600 100
run 2.0 2G 750M 100 150 www.example.com B64GEqy93Fe JatosDefaultSimulation atOnceUsers 90 600 100
run 2.0 2G 750M 100 150 www.example.com B64GEqy93Fe JatosDefaultSimulation atOnceUsers 100 600 100

run 2.0 2G 750M 120 180 www.example.com B64GEqy93Fe JatosDefaultSimulation atOnceUsers 60 600 100
run 2.0 2G 750M 120 180 www.example.com B64GEqy93Fe JatosDefaultSimulation atOnceUsers 70 600 100
run 2.0 2G 750M 120 180 www.example.com B64GEqy93Fe JatosDefaultSimulation atOnceUsers 80 600 100
run 2.0 2G 750M 120 180 www.example.com B64GEqy93Fe JatosDefaultSimulation atOnceUsers 90 600 100
run 2.0 2G 750M 120 180 www.example.com B64GEqy93Fe JatosDefaultSimulation atOnceUsers 100 600 100
