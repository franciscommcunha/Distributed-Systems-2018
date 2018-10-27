SD2018 - Afternoon Races Problem Distributed
############################################

> How it works
	run_machines.sh will invoke individual scripts from the "scripts" folder
	and connect to each remote machine using Plink. After establishing a connection
	each remote machine will start their own "run.sh" script which will compile and 
	execute the program of their respective server. The open terminal will display all
	of the remote machines outputs. After the program execution  the logger file Log.txt 
	will be displayed in the terminal by the use of Cat command. Any key press will close 
	the terminal.

> How to deploy
	- Execute the bash script run_machines.sh located in the "Deployment" folder
> Dependencies
	- Putty
	- Plink