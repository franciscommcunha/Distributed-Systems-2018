#!/bin/bash
plink -ssh sd0301@l040101-ws01.ua.pt -pw ChicoJoao "rm -rf ~/Private/sd_projecto2018/AfternoonRacesDistributed/src/Log.txt; bash ~/Private/run.sh; cat ~/Private/sd_projecto2018/AfternoonRacesDistributed/src/Log.txt"
read -rsn1