#!/bin/bash


cd cpu_27.5
./total.sh ./run_fixed.sh
sleep 60
cd ../cpu_30
./total.sh ./run_fixed.sh
sleep 60
cd ../cpu_32.5
./total.sh ./run_fixed.sh
sleep 60
cd ../cpu_35
./total.sh ./run_fixed.sh
sleep 60
cd ../cpu_37.5
./total.sh ./run_fixed.sh
sleep 60
cd ../cpu_40
./total.sh ./run_fixed.sh

