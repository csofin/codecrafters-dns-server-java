#!/bin/bash

function connect() {
  printf 'Running test for Stage #UX2 (Setup UDP server)\n'
  out=$(dig +time=1 +tries=1 @localhost -p 2053 codecrafters.io | head -1)
  if [[ -z "$out" ]]; then
    printf 'Expected to connect to localhost:2053 using UDP\nTest Failed'
    exit 1
  fi
  printf 'Connected to localhost:2053 using UDP\nTest Passed\n'
}

function write_header() {
  printf 'Running test for Stage #TZ1 (Write header section)\n'
  out=$(dig +time=1 +tries=1 @localhost -p 2053 codecrafters.io | head -1 | awk '{print $(NF)}')
  if [[ ! $out =~ 1234 ]]; then
    printf 'Expected identifier value to be 1234, got %s\nTest Failed' "$out"
    exit 1
  fi
  printf 'Received response with identifier value 1234\nTest Passed\n'
}

function test() {
  connect
  printf '\n'
  write_header
}

if [ $# -eq 0 ]; then
  test
fi

$1