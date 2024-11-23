#!/bin/bash

function connect() {
  printf 'Running test for Stage #UX2 (Setup UDP server)\n'
  out=$(dig +time=1 +tries=1 @localhost -p 2053 codecrafters.io | tail -1 | awk '{print $2,$3,$4}' | sed 's/\;//g')
  if [ "$out" == "connection timed out" ]; then
    printf 'Expected to connect to localhost:2053 using UDP\nTest Failed'
    exit 1
  fi
  printf 'Connected to localhost:2053 using UDP\nTest Passed\n'
}

function write_header() {
  printf 'Running test for Stage #TZ1 (Write header section)\n'
  out=$(dig +time=1 +tries=1 @localhost -p 2053 codecrafters.io | grep -oE "id: [0-9]+" | awk '{print $2}')
  if [ -z "$out" ]; then
    printf 'Expected reply with header id, got %s\nTest Failed' "$out"
    exit 1
  fi
  printf 'Received reply with header id\nTest Passed\n'
}

function write_question() {
  printf 'Running test for Stage #BF2 (Write question section)\n'
  out=$(dig +time=1 +tries=1 @localhost -p 2053 codecrafters.io | grep -A 2 'QUESTION SECTION' | head -2 | tail -1 | awk '{print $1}' | awk '{print substr($0, 2, length($0) - 2)}')
  if [ "$out" != "codecrafters.io" ]; then
    printf 'Expected question section with label codecrafters.io, got %s\nTest Failed' "$out"
    exit 1
  fi
  printf 'Received reply with question label codecrafters.io\nTest Passed\n'
}

function write_answer() {
  printf 'Running test for Stage #XM2 (Write answer section)\n'
  out=$(dig +time=1 +tries=1 @localhost -p 2053 codecrafters.io | grep -A 2 'ANSWER SECTION' | head -2 | tail -1 | awk '{print $1}' | awk '{print substr($0, 1, length($0) - 1)}')
  if [ "$out" != "codecrafters.io" ]; then
    printf 'Expected answer section with label codecrafters.io, got %s\nTest Failed' "$out"
    exit 1
  fi
  printf 'Received reply with answer label codecrafters.io\nTest Passed\n'
}

function test() {
  connect
  printf '\n'
  write_header
  printf '\n'
  write_question
  printf '\n'
  write_answer
}

if [ $# -eq 0 ]; then
  test
fi

$1