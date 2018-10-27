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

echo "Cleaning machines..."

sshpass -p $password ssh -o StrictHostKeyChecking=no -f $username@$registryHostName "cd ~/projects/ ; rm -rf *"
sshpass -p $password ssh -o StrictHostKeyChecking=no -f $username@$GeneralRepositoryHostName "cd ~/projects/ ; rm -rf *"
sshpass -p $password ssh -o StrictHostKeyChecking=no -f $username@$StableHostName "cd ~/projects/ ; rm -rf *"
sshpass -p $password ssh -o StrictHostKeyChecking=no -f $username@$PaddockHostName "cd ~/projects/ ; rm -rf *"
sshpass -p $password ssh -o StrictHostKeyChecking=no -f $username@$BettingCenterHostName "cd ~/projects/ ; rm -rf *"
sshpass -p $password ssh -o StrictHostKeyChecking=no -f $username@$RacingTrackHostName "cd ~/projects/ ; rm -rf *"
sshpass -p $password ssh -o StrictHostKeyChecking=no -f $username@$ControlCenterHostName "cd ~/projects/ ; rm -rf *"
sshpass -p $password ssh -o StrictHostKeyChecking=no -f $username@$BrokerHostName "cd ~/projects/ ; rm -rf *"
sshpass -p $password ssh -o StrictHostKeyChecking=no -f $username@$HorseHostName "cd ~/projects/ ; rm -rf *"
sshpass -p $password ssh -o StrictHostKeyChecking=no -f $username@$SpectatorHostName "cd ~/projects/ ; rm -rf *"

echo "Cleaning done! all .class & folders deleted!"