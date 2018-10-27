#!/usr/bin/env bash
url=http://192.168.8.177/sd0301/projects/
username=sd0301
password=ChicoJoao
registryHostName=l040101-ws07.ua.pt
GeneralRepositoryHostName=l040101-ws01.ua.pt
StableHostName=l040101-ws02.ua.pt
PaddockHostName=l040101-ws03.ua.pt
BettingCenterHostName=l040101-ws04.ua.pt
ControlCenterHostName=l040101-ws06.ua.pt
RacingTrackHostName=l040101-ws05.ua.pt
BrokerHostName=l040101-ws08.ua.pt
HorseHostName=l040101-ws10.ua.pt
SpectatorHostName=l040101-ws09.ua.pt
registryPortNum=22970

kill 'lsof -t -i:22970'
kill 'lsof -t -i:22961'

kill 'lsof -t -i:22310'
kill 'lsof -t -i:22311'
kill 'lsof -t -i:22312'
kill 'lsof -t -i:22313'
kill 'lsof -t -i:22314'
kill 'lsof -t -i:22315'
kill 'lsof -t -i:22316'
kill 'lsof -t -i:22317'
kill 'lsof -t -i:22318'

sshpass -p $password ssh $username@$registryHostName -o StrictHostKeyChecking=no "kill -13 \$(netstat -tlnp | awk '/:22970 */ {split(\$NF,a,\"/\"); print a[1]}')"
sshpass -p $password ssh $username@$registryHostName -o StrictHostKeyChecking=no "kill -13 \$(netstat -tlnp | awk '/:22961 */ {split(\$NF,a,\"/\"); print a[1]}')"
