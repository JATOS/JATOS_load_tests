#!/bin/bash

export JATOS_VERSION="3.9.7"
export JATOS_HOST="www.example.com"
export JATOS_STUDY_CODE="EIAESDcnbB2"

GATLING_CMD="./gatling-charts-highcharts-bundle-3.2.1/bin/gatling.sh"
LOG_FILE="./gatling_run.log"

exec > >(tee -a "$LOG_FILE") 2>&1
set -e  # Exit on failure

validate_env() {
  REQUIRED_VARS=(JATOS_VERSION JATOS_CPUS JATOS_MEM JAVA_XMX JATOS_DB_CONNECTIONPOOL_SIZE JATOS_THREADPOOL_SIZE JATOS_HOST JATOS_STUDY_CODE SIMULATION INJECT_TYPE USERS DURATION SUCCESS_RATE)

  for var in "${REQUIRED_VARS[@]}"; do
    if [ -z "${!var}" ]; then
      echo "Error: $var is not set."
      exit 1
    fi
  done
}

run () {
  export JATOS_CPUS=$1
  export JATOS_MEM=$2
  export JAVA_XMX=$3
  export JATOS_DB_CONNECTIONPOOL_SIZE=$4
  export JATOS_THREADPOOL_SIZE=$5
  export SIMULATION=$6
  export INJECT_TYPE=$7
  export USERS=$8
  export DURATION=$9
  export SUCCESS_RATE=${10}
  validate_env

  description="v$JATOS_VERSION cpus:$JATOS_CPUS mem:$JATOS_MEM javaXmx:$JAVA_XMX dbPoolSize:$JATOS_DB_CONNECTIONPOOL_SIZE threadPoolSize:$JATOS_THREADPOOL_SIZE $JATOS_HOST $JATOS_STUDY_CODE $SIMULATION $INJECT_TYPE users:$USERS duration:$DURATION successRate:$SUCCESS_RATE"

  echo -n "Starting test: $description"

  docker compose down > /dev/null 2>&1 || { echo -e "\nError during 'docker compose down'."; exit 1; }
  docker compose up --wait > /dev/null 2>&1 || { echo -e "\nError during 'docker compose up --wait'."; exit 1; }

  eval "$GATLING_CMD -s $SIMULATION -rd '$description' -nr" >> jatos_load_test.log 2>&1 && echo " OK" || echo " KO"

  docker compose down > /dev/null 2>&1 || { echo -e "\nError during 'docker compose down'."; exit 1; }
}


# run cpus mem javaXmx dbPoolSize threadPoolSize host studyCode simulation injectType users duration fail
run 2.0 2G 750M 75 112 JatosDefaultSimulation atOnceUsers 60 600 100
run 2.0 2G 750M 75 112 JatosDefaultSimulation atOnceUsers 70 600 100
run 2.0 2G 750M 75 112 JatosDefaultSimulation atOnceUsers 80 600 100
run 2.0 2G 750M 75 112 JatosDefaultSimulation atOnceUsers 90 600 100
run 2.0 2G 750M 75 112 JatosDefaultSimulation atOnceUsers 100 600 100

run 2.0 2G 750M 100 150 JatosDefaultSimulation atOnceUsers 60 600 100
run 2.0 2G 750M 100 150 JatosDefaultSimulation atOnceUsers 70 600 100
run 2.0 2G 750M 100 150 JatosDefaultSimulation atOnceUsers 80 600 100
run 2.0 2G 750M 100 150 JatosDefaultSimulation atOnceUsers 90 600 100
run 2.0 2G 750M 100 150 JatosDefaultSimulation atOnceUsers 100 600 100

run 2.0 2G 750M 120 180 JatosDefaultSimulation atOnceUsers 60 600 100
run 2.0 2G 750M 120 180 JatosDefaultSimulation atOnceUsers 70 600 100
run 2.0 2G 750M 120 180 JatosDefaultSimulation atOnceUsers 80 600 100
run 2.0 2G 750M 120 180 JatosDefaultSimulation atOnceUsers 90 600 100
run 2.0 2G 750M 120 180 JatosDefaultSimulation atOnceUsers 100 600 100
