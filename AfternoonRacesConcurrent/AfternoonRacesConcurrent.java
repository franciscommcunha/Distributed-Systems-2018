package AfternoonRacesConcurrent;

import static AfternoonRacesConcurrent.Constants.Constants.*;

import AfternoonRacesConcurrent.Entities.Broker.*;
import AfternoonRacesConcurrent.Entities.Broker.BrokerInterfaces.*;
import AfternoonRacesConcurrent.Entities.Horses.*;
import AfternoonRacesConcurrent.Entities.Horses.HorsesInterfaces.*;
import AfternoonRacesConcurrent.Entities.Spectators.*;
import AfternoonRacesConcurrent.Entities.Spectators.SpectatorsInterfaces.*;
import AfternoonRacesConcurrent.Monitors.*;

import java.util.concurrent.ThreadLocalRandom;

public class AfternoonRacesConcurrent {
	
	public static void main(String[] args) {
	
    /* Information repository instantiation */
    GeneralRepository repository = new GeneralRepository(); 
        
    /* Monitors instantiation */ 
    BettingCenter bc = new BettingCenter(); 	
    Paddock p = new Paddock(); 					
    RacingTrack rt = new RacingTrack(); 		
    Stable s = new Stable(); 					
    ControlCenter cc = new	ControlCenter();	

    Horse horses[] = new Horse[NUM_HORSES];
    Spectator spectators[] = new Spectator[NUM_SPECTATORS];

    // horses instantiation
    for(int i=0 ; i < NUM_HORSES ; i++) {
        horses[i] = new Horse(i,ThreadLocalRandom.current().nextInt(HORSE_MIN_AGILITY, HORSE_MAX_AGILITY+1), (IHorse_Paddock) p, (IHorse_RacingTrack) rt, (IHorse_Stable) s, (GeneralRepository) repository);
    }
 
    // spectators instantiation
    for(int i=0;i<NUM_SPECTATORS;i++) {
        spectators[i] = new Spectator(i, SPECTATOR_INIT_MONEY, (ISpectator_BettingCenter) bc, (ISpectator_ControlCenter) cc, (ISpectator_Paddock) p, (GeneralRepository) repository);
    }
    
    // broker instantiation
    Broker broker = new Broker((IBroker_BettingCenter) bc, (IBroker_ControlCenter) cc, (IBroker_RacingTrack) rt, (IBroker_Stable) s, (IBroker_Paddock) p, (GeneralRepository) repository);

    // launching the threads
    broker.start();
    for(int i=0;i<horses.length;i++) horses[i].start();
    for(int i=0;i<spectators.length;i++) spectators[i].start();
    	
	}	
	/*
	public static final int k = NUM_RACES;	// number of races
	public static final int m = Constants.NUM_SPECTATORS;	// number of spectators
	public static final int n = Constants.NUM_SPECTATORS;	// number of horses
	
	public static final int spectator_initial_money = 100; // money amount each spectator starts the event with
		
	public static void main(String[] args) {
				
		GeneralRepository rep = new GeneralRepository(); // repository containing all the variables shared by the threads
		
		BettingCenter bc = new BettingCenter(); 	// monitor for betting center
		Paddock p = new Paddock(); 					// monitor for paddock
		RacingTrack rt = new RacingTrack(100); 		// monitor for racing track
		Stable s = new Stable(); 					// monitor for stable
		ControlCenter cc = new	ControlCenter();	// monitor for control center
		
		Horse horses[] = new Horse[n];
		Spectator spectators[] = new Spectator[m];
		
		// horses instantiation
		for(int i=0;i<n;i++) {
			horses[i] = new Horse(i,ThreadLocalRandom.current().nextInt(1, 6), (IHorse_Paddock) p, (IHorse_RacingTrack) rt, (IHorse_Stable) s, (GeneralRepository) rep);
		}
		
		// spectators instantiation
		for(int i=0;i<n;i++) {
			spectators[i] = new Spectator(i, spectator_initial_money, (ISpectator_BettingCenter) bc, (ISpectator_ControlCenter) cc, (ISpectator_Paddock) p, (GeneralRepository) rep);
		}
		
		Broker broker = new Broker((IBroker_BettingCenter) bc, (IBroker_ControlCenter) cc, (IBroker_RacingTrack) rt, (IBroker_Stable) s, (IBroker_Paddock) p, (GeneralRepository) rep);
		
		// launching the threads
		broker.start();
		for(int i=0;i<horses.length;i++) horses[i].start();
		for(int i=0;i<spectators.length;i++) spectators[i].start();
	}
	*/
}
