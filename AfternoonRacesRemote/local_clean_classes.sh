#!/usr/bin/env bash

echo "Removing Interfaces .class files ..."
rm interfaces/*.class
rm deploy/interfaces/*.class

echo "removing tar.gz files" 
rm -rf ../deploy.tar.gz

echo "Removing Registry .class files ..."
rm Registry/*.class
rm deploy/Registry/*.class

echo "Removing Monitors .class files ... "
rm Monitors/GeneralRepository/*.class
rm deploy/Monitors/GeneralRepository/*.class
rm Monitors/Stable/*.class
rm deploy/Monitors/Stable/*.class
rm Monitors/Paddock/*.class
rm deploy/Monitors/Paddock/*.class
rm Monitors/RacingTrack/*.class
rm deploy/Monitors/RacingTrack/*.class
rm Monitors/BettingCenter/*.class
rm deploy/Monitors/BettingCenter/*.class
rm Monitors/ControlCenter/*.class
rm deploy/Monitors/ControlCenter/*.class


echo "Removing Entities .class files ..."
rm Entities/Broker/*.class
rm deploy/Entities/Broker/*.class
rm Entities/Horses/*.class
rm deploy/Entities/Horses/*.class
rm Entities/Spectators/*.class
rm deploy/Entities/Spectators/*.class

echo "Removing Entities Interfaces .class files ..."
rm Entities/Broker/BrokerInterfaces/*.class
rm deploy/Entities/Broker/BrokerInterfaces/*.class
rm Entities/Horses/HorsesInterfaces/*.class
rm deploy/Entities/Horses/HorsesInterfaces/*.class
rm Entities/Spectators/SpectatorsInterfaces/*.class
rm deploy/Entities/Spectators/SpectatorsInterfaces/*.class

echo "Removing Entities Enumerates .class files ..."
rm Entities/Broker/BrokerEnum/*.class
rm deploy/Entities/Broker/BrokerEnum/*.class
rm Entities/Horses/HorsesEnum/*.class
rm deploy/Entities/Horses/HorsesEnum/*.class
rm Entities/Spectators/SpectatorsEnum/*.class
rm deploy/Entities/Spectators/SpectatorsEnum/*.class

echo "Removing Constants .class files ..."
rm Constants/*.class
rm deploy/Constants/*.class


echo "Done ..."