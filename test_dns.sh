#!/bin/bash

function connect() {
  printf 'Running test for Stage #UX2 (Setup UDP server)\n'
  out=$(nc -v -w 1 -u localhost 2053 2>/dev/null && echo "OK" || echo "NOK")
  if [ "$out" != "OK" ]; then
    printf 'Expected to connect to localhost:2053 using UDP\nTest Failed'
    exit 1
  fi
  printf 'Connected to localhost:2053 using UDP\nTest Passed\n'
}

function test() {
  connect
}

if [ $# -eq 0 ]; then
  test
fi

$1