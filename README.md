# JATOS load tests

Uses Gatling (gatling.io) to run load tests on JATOS.

Uses Docker Compose to run JATOS with MySQL and Nginx.

Allows limiting CPUs, memory, thread pool and DB connection pool.

Configuration via `jatos.conf`, `mysql.conf`, `nginx.conf`, evironment variables and `compose.yaml`.

Needs the line `127.0.0.1 www.example.com` in `/etc/hosts`.

Gatling simulations are under `./gatling-charts-highcharts-bundle-3.2.1/user-files/simulations`.

Can run parallel to a local JATOS + MySQL since it is using port 9001 and 3307 respectively. 

To just start JATOS: `docker compose up`

Import `jatos_load_test.jzip` into JATOS.

Use `run.sh` script to run a batch of load tests. You will have to change the study code.

Gatling logs: `tail -f ./jatos_load_test.log`.

JATOS logs: `tail -f jatos-logs/application.log`

Script logs: `tail -f gatling_run.log`
