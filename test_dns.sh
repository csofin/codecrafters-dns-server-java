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

function parse_header() {
  printf 'Running test for Stage #UC8 (Parse header section)\n'
  out=$(dig +time=1 +tries=1 @localhost -p 2053 codecrafters.io)
  header=$(echo "$out" | grep "HEADER")
  if [ -z "$header" ]; then
    printf 'Expected reply with header\nTest Failed'
    exit 1
  fi
  printf '%s\n' "$header"
  printf 'Received reply with header\nTest Passed\n'
}

function parse_question() {
  printf 'Running test for Stage #HD8 (Parse question section)\n'
  out=$(dig +time=1 +tries=1 @localhost -p 2053 codecrafters.io)
  question=$(echo "$out" | grep -A 2 "QUESTION SECTION")
  if [ -z "$question" ]; then
    printf 'Expected reply with question section\nTest Failed'
    exit 1
  fi
  printf '%s\n' "$question"
  answer=$(echo "$out" | grep -A 2 "ANSWER SECTION")
  if [ -z "$answer" ]; then
    printf 'Expected reply with answer section\nTest Failed'
    exit 1
  fi
  printf '%s\n' "$answer"
  printf 'Received reply with question and answer\nTest Passed\n'
}

function parse_compressed_packet() {
  printf 'Running test for Stage #YC9 (Parse compressed packet)\n'
  make -C test_dns/.
}

function forward() {
  printf 'Running test for Stage #GT1 (Forwarding Server)\n'
  out=$(dig +time=1 +tries=1 @localhost -p 2053 codecrafters.io)
  answer=$(echo "$out" | grep -A 2 "ANSWER SECTION" | tail -2 | awk '{print $NF}')
  if [ "$answer" != "76.76.21.21" ]; then
    printf 'Expected answer section with rdata 76.76.21.21, got %s\nTest Failed' "$answer"
    exit 1
  fi
  printf '%s\n' "$answer"
  printf 'Received forwarded answer with expected rdata value\nTest Passed\n'
}

function test() {
  connect
  printf '\n'
  write_header
  printf '\n'
  write_question
  printf '\n'
  write_answer
  printf '\n'
  parse_header
  printf '\n'
  parse_question
  printf '\n'
  parse_compressed_packet
  printf '\n'
  forward
}

if [ $# -eq 0 ]; then
  test
fi

$1