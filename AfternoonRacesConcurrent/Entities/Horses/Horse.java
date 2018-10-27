package AfternoonRacesConcurrent.Entities.Horses;

import AfternoonRacesConcurrent.Monitors.GeneralRepository;
import AfternoonRacesConcurrent.Entities.Horses.HorsesInterfaces.*;
import static AfternoonRacesConcurrent.Constants.Constants.*;
import static AfternoonRacesConcurrent.Entities.Horses.HorsesEnum.HorseStates.*;

import java.util.concurrent.ThreadLocalRandom;

public class Horse extends Thread{
	
	GeneralRepository general_repository;
	
	private final IHorse_Paddock horse_paddock;
	private final IHorse_RacingTrack horse_racing_track;
	private final IHorse_Stable horse_stable;
	
	private int horse_id;
	private int agility;
		
	public Horse(int horse_id, int agility ,IHorse_Paddock horse_paddock, IHorse_RacingTrack horse_racing_track, IHorse_Stable horse_stable, GeneralRepository rep) 
	{
		this.horse_id = horse_id;
		this.agility = agility;
		this.horse_paddock = horse_paddock;
		this.horse_racing_track = horse_racing_track;
		this.horse_stable = horse_stable;
		this.general_repository = rep;
	}
	
	@Override
	public void run()
	{	
		
		int race_number = 0;
        boolean event_ended = false;
		
        System.out.println(general_repository.getHorseState(this.horse_id)+", HORSE " + this.horse_id);
        
		general_repository.initLog();
		general_repository.writeHorseSt(general_repository.getHorseState(this.horse_id));
		general_repository.makeLogger();
				
		 /* horse/jockey pair main lifecycle */
        while(!event_ended) {
            switch(general_repository.getHorseState(this.horse_id)) {

                case AT_THE_FIELDS :
                    horse_stable.proceedToStable(this.horse_id, general_repository); // horse proceeds to stable at the start of the race waking up the broker
                    general_repository.makeLogger();             
                    break;

                case AT_THE_STABLE : 
       
                    if(race_number == NUM_RACES){ 
                        event_ended = true;
                        general_repository.makeLogger();
                        break;
                    }
                    horse_paddock.proceedToPaddock(this.horse_id, general_repository); // horse proceeds to stable at the start of the race waking up the broker
                    general_repository.makeLogger();
                    break;

                case AT_THE_PADDOCK : 
                    horse_paddock.proceedToStartLine(this.horse_id, general_repository); // horse proceeds to start line after all the spectators have checked them
                    general_repository.setHorseState(AT_THE_START_LINE, horse_id);
                    general_repository.writeHorseSt(AT_THE_START_LINE);
                    break;

                case AT_THE_START_LINE : 

                    horse_racing_track.makeAMove(this.horse_id, ThreadLocalRandom.current().nextInt(1, this.agility+1), general_repository); // horses start the race
                    general_repository.setHorseState(RUNNING, this.horse_id);
                    general_repository.writeHorseSt(RUNNING);
                    general_repository.makeLogger();
                    break;

                case RUNNING:
                    
                    if(!horse_racing_track.hasFinishLineBeenCrossed(this.horse_id, general_repository)){   // horses moving while finish line has not been crossed                     
                        horse_racing_track.makeAMove(this.horse_id, ThreadLocalRandom.current().nextInt(1, this.agility+1), general_repository);
                        general_repository.makeLogger();
                    }
                    else {
                        general_repository.setHorseState(AT_THE_FINISH_LINE, this.horse_id);	
                        general_repository.writeHorseSt(AT_THE_FINISH_LINE);
                        general_repository.makeLogger();
                    }
                    
                    break;

                case AT_THE_FINISH_LINE : 
                   
                    race_number++;
                    horse_stable.proceedToStable(this.horse_id, general_repository); // horse proceeds to stable at the start of the race waking up the broker
                    general_repository.makeLogger();
                    break;

                default :
                    break;
            }
        }
        
      System.out.println("------ HORSE "+ this.horse_id +" LIFECYCLE ENDED -------");
      general_repository.closeWriter();

	}
}
