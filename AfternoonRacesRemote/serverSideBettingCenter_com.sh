#!/usr/bin/env bash
java -Djava.rmi.server.codebase=$3\
     -Djava.rmi.server.useCodebaseOnly=true\
     -Djava.security.policy=java.policy\
     -cp .. AfternoonRacesRemote.Monitors.BettingCenter.BettingCenterServer $1 $2