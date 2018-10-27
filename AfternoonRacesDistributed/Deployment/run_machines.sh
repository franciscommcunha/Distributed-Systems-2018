#!/bin/bash

scripts/run_01_repository.sh &
scripts/run_02_stable.sh &
scripts/run_03_paddock.sh &
scripts/run_04_betting_center.sh &
scripts/run_05_racing_track.sh &
scripts/run_06_control_center.sh &

scripts/run_broker.sh &
scripts/run_horses.sh &
scripts/run_spectators.sh &
read -rsn1