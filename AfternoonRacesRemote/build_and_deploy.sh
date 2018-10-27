#!/usr/bin/env bash

echo "compiling source code..."
javac -cp Constants/*.java 
javac -cp .. interfaces/*.java 
javac -cp .. Registry/*.java 
javac -cp .. Monitors/GeneralRepository/*.java 
javac -cp .. Monitors/Stable/*.java 
javac -cp .. Monitors/Paddock/*.java 
javac -cp .. Monitors/RacingTrack/*.java 
javac -cp .. Monitors/BettingCenter/*.java 
javac -cp .. Monitors/ControlCenter/*.java 
javac -cp .. Entities/Broker/*.java 
javac -cp .. Entities/Broker/BrokerEnum/*.java
javac -cp .. Entities/Broker/BrokerInterfaces/*.java
javac -cp .. Entities/Horses/*.java
javac -cp .. Entities/Horses/HorsesEnum/*.java
javac -cp .. Entities/Horses/HorsesInterfaces/*.java
javac -cp .. Entities/Spectators/*.java
javac -cp .. Entities/Spectators/SpectatorsEnum/*.java
javac -cp .. Entities/Spectators/SpectatorsInterfaces/*.java

echo "Copying Interfaces .class files ..."
cp interfaces/*.class deploy/interfaces/

echo "Copying Registry .class files ..."
cp Registry/*.class deploy/Registry/

echo "Copying Monitors .class files ... "
cp Monitors/GeneralRepository/*.class deploy/Monitors/GeneralRepository/
cp Monitors/Stable/*.class deploy/Monitors/Stable/
cp Monitors/Paddock/*.class deploy/Monitors/Paddock/
cp Monitors/RacingTrack/*.class deploy/Monitors/RacingTrack/
cp Monitors/BettingCenter/*.class deploy/Monitors/BettingCenter/
cp Monitors/ControlCenter/*.class deploy/Monitors/ControlCenter/

echo "Copying Entities .class files ..."
cp Entities/Broker/*.class deploy/Entities/Broker/
cp Entities/Horses/*.class deploy/Entities/Horses/
cp Entities/Spectators/*.class deploy/Entities/Spectators/

echo "Copying Entities Interfaces .class files ..."
cp Entities/Broker/BrokerInterfaces/*.class deploy/Entities/Broker/BrokerInterfaces/
cp Entities/Horses/HorsesInterfaces/*.class deploy/Entities/Horses/HorsesInterfaces/
cp Entities/Spectators/SpectatorsInterfaces/*.class deploy/Entities/Spectators/SpectatorsInterfaces/

echo "Copying Entities Enumerates .class files ..."
cp Entities/Broker/BrokerEnum/*.class deploy/Entities/Broker/BrokerEnum/
cp Entities/Horses/HorsesEnum/*.class deploy/Entities/Horses/HorsesEnum/
cp Entities/Spectators/SpectatorsEnum/*.class deploy/Entities/Spectators/SpectatorsEnum/

echo "Copying Constants .class files ..."
cp Constants/*.class deploy/Constants/

echo "done build and deploy!!!"