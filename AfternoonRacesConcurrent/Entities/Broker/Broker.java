package AfternoonRacesConcurrent.Entities.Broker;

import AfternoonRacesConcurrent.Monitors.GeneralRepository;
import AfternoonRacesConcurrent.Entities.Broker.BrokerInterfaces.*;
import static AfternoonRacesConcurrent.Entities.Broker.BrokerEnum.BrokerStates.*;

import AfternoonRacesConcurrent.Entities.Broker.BrokerEnum.BrokerStates;

import static AfternoonRacesConcurrent.Constants.Constants.*;

public class Broker extends Thread {
	
	GeneralRepository general_repository;
	
	private final IBroker_BettingCenter broker_betting_center;
	private final IBroker_ControlCenter broker_control_center;
	private final IBroker_RacingTrack broker_racing_track;
	private final IBroker_Stable broker_stable;
	private final IBroker_Paddock broker_paddock;
		
	public Broker(IBroker_BettingCenter broker_betting_center, IBroker_ControlCenter broker_control_center, IBroker_RacingTrack broker_racing_track, IBroker_Stable broker_stable, IBroker_Paddock broker_paddock, GeneralRepository rep)
	{
		this.broker_betting_center = broker_betting_center;
		this.broker_control_center = broker_control_center;
		this.broker_racing_track = broker_racing_track;
		this.broker_stable = broker_stable;
		this.broker_paddock = broker_paddock;
		this.general_repository = rep;
	}
	
	@Override
	public void run()
	{	
		general_repository.initLog();
		general_repository.writeStatB(general_repository.getBrokerState());

		System.out.println(general_repository.getBrokerState()+", BROKER");

        boolean event_finished = false;
        int race_number = 0;
        
     // broker main lifecycle
        while(!event_finished) {
            switch(general_repository.getBrokerState()) {
            
                case OPENING_THE_EVENT :
                	
                    broker_stable.summonHorsesToPaddock(general_repository); // broker sends horses to the paddock
                    general_repository.setBrokerState(ANNOUNCING_NEXT_RACE);
                    general_repository.writeStatB(ANNOUNCING_NEXT_RACE);
                    broker_paddock.summonHorsesToPaddock(general_repository); // method used to wake up the borker, so he can start accepting bets
                    break;

                case ANNOUNCING_NEXT_RACE : 
                		broker_betting_center.acceptTheBets(general_repository);	// broker can start accepting the bets of all spectators
                		general_repository.setBrokerState(WAITING_FOR_BETS);
                		general_repository.writeStatB(WAITING_FOR_BETS);
                		general_repository.makeLogger();
                    break;

                case WAITING_FOR_BETS :  
                    
                    broker_racing_track.startTheRace( general_repository); // broker starts the race after all the bets are done
                    general_repository.setBrokerState(SUPERVISING_THE_RACE);
                    general_repository.writeStatB(SUPERVISING_THE_RACE);
                    general_repository.makeLogger();
                    break;

                case SUPERVISING_THE_RACE:
                   race_number++;
                   
                    broker_control_center.reportResults(general_repository); // broker report the results to spectators
                       
                    if(broker_control_center.areThereAnyWinners(general_repository)) { // if some spectator has bet in the winner horse, the spectator is a winner
                    	
                        broker_betting_center.honourTheBets(general_repository);
                        general_repository.setBrokerState(SETTLING_ACCOUNTS);
                        general_repository.writeStatB(SETTLING_ACCOUNTS);   
                        general_repository.makeLogger();
                    }
                    else if(race_number == NUM_RACES) {	
                        broker_stable.summonHorsesToPaddock(general_repository); 
                        broker_paddock.summonHorsesToPaddock(general_repository);
                        broker_control_center.entertainTheGuests(general_repository);
                        general_repository.setBrokerState(PLAYING_HOST_AT_THE_BAR);
                        general_repository.writeStatB(PLAYING_HOST_AT_THE_BAR);
                        general_repository.makeLogger();
                   }
                    else {
                        broker_stable.summonHorsesToPaddock( general_repository); // broker sends the horses to the paddock after the race has finished
                        broker_paddock.summonHorsesToPaddock( general_repository);
                    }
                    break;

                case SETTLING_ACCOUNTS :

                        if(race_number == NUM_RACES) {
                            broker_stable.summonHorsesToPaddock( general_repository); // broker sends the horses to the paddock after the race has finished
                            broker_control_center.entertainTheGuests(general_repository);
                            general_repository.setBrokerState(PLAYING_HOST_AT_THE_BAR);
                            general_repository.writeStatB(PLAYING_HOST_AT_THE_BAR);
                            general_repository.makeLogger();
                            
                        }
                        else {
                            broker_stable.summonHorsesToPaddock(general_repository); 
                            broker_paddock.summonHorsesToPaddock(general_repository);
                            general_repository.makeLogger();
                        }
                        break;

                case PLAYING_HOST_AT_THE_BAR :
                        event_finished = true;
                        general_repository.setBrokerState(PLAYING_HOST_AT_THE_BAR);
                        general_repository.writeStatB(PLAYING_HOST_AT_THE_BAR);
                        general_repository.makeLogger();
                        break;

                default :
                        break;
            }
        }
        
        System.out.println("------ BROKER LIFECYCLE ENDED -------");
        
	}
}
