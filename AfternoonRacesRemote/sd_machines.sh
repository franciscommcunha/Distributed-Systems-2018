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

bash build_and_deploy.sh

echo "Compressing source code..."
cd ..
tar -czf deploy.tar.gz AfternoonRacesRemote/

sleep 5

echo "Unzipping Code in Machine Registry"      
sshpass -p $password scp deploy.tar.gz $username@$registryHostName:~/projects/
sshpass -p $password ssh -o StrictHostKeyChecking=no -f $username@$registryHostName "cd ~/projects/ ; tar -xmzf deploy.tar.gz" &

echo "Unzipping Code in Machine General Repository"
sshpass -p $password scp deploy.tar.gz $username@$GeneralRepositoryHostName:~/projects/
sshpass -p $password ssh -o StrictHostKeyChecking=no -f $username@$GeneralRepositoryHostName "cd ~/projects/ ; tar -xmzf deploy.tar.gz" &

echo "Unzipping Code in Machine Stable"
sshpass -p $password scp deploy.tar.gz $username@$StableHostName:~/projects/
sshpass -p $password ssh -o StrictHostKeyChecking=no -f $username@$StableHostName "cd ~/projects/ ; tar -xmzf deploy.tar.gz" &

echo "Unzipping Code in Machine Paddock"
sshpass -p $password scp deploy.tar.gz $username@$PaddockHostName:~/projects/
sshpass -p $password ssh -o StrictHostKeyChecking=no -f $username@$PaddockHostName "cd ~/projects/ ; tar -xmzf deploy.tar.gz" &

echo "Unzipping Code in Machine Betting Center"
sshpass -p $password scp deploy.tar.gz $username@$BettingCenterHostName:~/projects/
sshpass -p $password ssh -o StrictHostKeyChecking=no -f $username@$BettingCenterHostName "cd ~/projects/ ; tar -xmzf deploy.tar.gz" &

echo "Unzipping Code in Machine Control Center"
sshpass -p $password scp deploy.tar.gz $username@$ControlCenterHostName:~/projects/
sshpass -p $password ssh -o StrictHostKeyChecking=no -f $username@$ControlCenterHostName "cd ~/projects/ ; tar -xmzf deploy.tar.gz" &

echo "Unzipping Code in Machine Racing Track"
sshpass -p $password scp deploy.tar.gz $username@$RacingTrackHostName:~/projects/
sshpass -p $password ssh -o StrictHostKeyChecking=no -f $username@$RacingTrackHostName "cd ~/projects/ ; tar -xmzf deploy.tar.gz" &

echo "Unzipping Code in Machine Broker"
sshpass -p $password scp deploy.tar.gz $username@$BrokerHostName:~/projects/
sshpass -p $password ssh -o StrictHostKeyChecking=no -f $username@$BrokerHostName "cd ~/projects/ ; tar -xmzf deploy.tar.gz" &

echo "Unzipping Code in Machine Horses"
sshpass -p $password scp deploy.tar.gz $username@$HorseHostName:~/projects/
sshpass -p $password ssh -o StrictHostKeyChecking=no -f $username@$HorseHostName "cd ~/projects/ ; tar -xmzf deploy.tar.gz" &

echo "Unzipping Code in Machine Spectators"
sshpass -p $password scp deploy.tar.gz $username@$SpectatorHostName:~/projects/
sshpass -p $password ssh -o StrictHostKeyChecking=no -f $username@$SpectatorHostName "cd ~/projects/ ; tar -xmzf deploy.tar.gz" &

echo "Setting RMI repository.... "
echo " "
sshpass -p $password scp AfternoonRacesRemote/set_rmiregistry.sh $username@$registryHostName:~/projects/
sshpass -p $password ssh -o StrictHostKeyChecking=no -f $username@$registryHostName "cd ~/projects/ ; sh set_rmiregistry.sh $registryPortNum" &
sleep 5
echo " "

echo "Setting Service Register.... "
echo " "
sshpass -p $password ssh -o StrictHostKeyChecking=no -f $username@$registryHostName "cd ~/projects/AfternoonRacesRemote/ ; bash registry_com.sh $registryHostName $registryPortNum $url" &
sleep 5
echo " "

echo "Setting General Repository.... "
echo " "
sshpass -p $password ssh -o StrictHostKeyChecking=no -f $username@$GeneralRepositoryHostName "cd ~/projects/AfternoonRacesRemote/ ; bash serverSideGeneralRepository_com.sh $registryHostName $registryPortNum $url" &
sleep 5
echo " "

echo "Setting Stable.... "
echo " "
sshpass -p $password ssh -o StrictHostKeyChecking=no -f $username@$StableHostName "cd ~/projects/AfternoonRacesRemote/ ; bash serverSideStable_com.sh $registryHostName $registryPortNum $url" &
sleep 5
echo " "

echo "Setting Paddock.... "
echo " "
sshpass -p $password ssh -o StrictHostKeyChecking=no -f $username@$PaddockHostName "cd ~/projects/AfternoonRacesRemote/ ; bash serverSidePaddock_com.sh $registryHostName $registryPortNum $url" &
sleep 5
echo " "

echo "Setting Betting Center.... "
echo " "
sshpass -p $password ssh -o StrictHostKeyChecking=no -f $username@$BettingCenterHostName "cd ~/projects/AfternoonRacesRemote/ ; bash serverSideBettingCenter_com.sh $registryHostName $registryPortNum $url" &
sleep 5
echo " "

echo "Setting Racing Tack.... "
echo " "
sshpass -p $password ssh -o StrictHostKeyChecking=no -f $username@$RacingTrackHostName "cd ~/projects/AfternoonRacesRemote/ ; bash serverSideRacingTrack_com.sh $registryHostName $registryPortNum $url" &
sleep 5
echo " "

echo "Setting Control Center.... "
echo " "
sshpass -p $password ssh -o StrictHostKeyChecking=no -f $username@$ControlCenterHostName "cd ~/projects/AfternoonRacesRemote/ ; bash serverSideControlCenter_com.sh $registryHostName $registryPortNum $url" &
sleep 5
echo " "

echo "Setting Broker.... "
echo " "
sshpass -p $password ssh -o StrictHostKeyChecking=no -f $username@$BrokerHostName "cd ~/projects/AfternoonRacesRemote/ ; bash clientSideBroker_com.sh $registryHostName $registryPortNum" &
sleep 5
echo " "

echo "Setting Horses.... "
echo " "
sshpass -p $password ssh -o StrictHostKeyChecking=no -f $username@$HorseHostName "cd ~/projects/AfternoonRacesRemote/ ; bash clientSideHorses_com.sh $registryHostName $registryPortNum" &
sleep 5
echo " "

echo "Setting Spectators.... "
echo " "
sshpass -p $password ssh -o StrictHostKeyChecking=no -f $username@$SpectatorHostName "cd ~/projects/AfternoonRacesRemote/ ; bash clientSideSpectators_com.sh $registryHostName $registryPortNum" &
sleep 5
echo " "

echo "Getting Log"
echo " "
sshpass -p $password ssh -o StrictHostKeyChecking=no -f $username@$GeneralRepositoryHostName "cat projects/AfternoonRacesRemote/Log.txt" > Log.txt
sleep 2
echo " "

echo "Killing Registry"
sleep 2
echo " "

cd AfternoonRacesRemote/
bash clean_class.sh
bash local_clean_classes.sh

read -rsn1
