#!/bin/bash

term_handler() {
  if [ $PID -ne 0 ]; then
    echo "Forwarding TERM signal to JVM -- pid $PID"
    kill -SIGTERM "$PID"
    wait $PID
    EXIT_STATUS=$?
    echo "Exited with code $EXIT_STATUS"
  fi
  exit 143; # 128 + 15 -- SIGTERM
}

trap "echo 'Starting handler for termination...'; term_handler" SIGINT SIGTERM
echo "PID of shell is $$"
java -jar opsly-backend-test-0.1.jar &
PID=$!
wait $PID