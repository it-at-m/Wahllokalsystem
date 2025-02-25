#!/usr/bin/env bash
cat wilma.txt
cat wahllokal.txt
cd server_mock
mkdir -p data
npm install
npm start
