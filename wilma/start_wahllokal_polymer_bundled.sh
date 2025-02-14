#!/usr/bin/env bash
cat wilma.txt
cat wahllokal.txt
cd server_mock
npm install
npm start "/../../gui_wahllokalsystem_polymer/build/static/" ''
