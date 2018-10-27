#!/usr/bin/env bash
rmiregistry -J-Djava.rmi.server.codebase="http://192.168.8.177/sd0301/projects/"\
            -J-Djava.rmi.server.useCodebaseOnly=true $1