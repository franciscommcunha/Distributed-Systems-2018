package AfternoonRacesConcurrent.Entities.Spectators;

import java.util.concurrent.ThreadLocalRandom;

import static AfternoonRacesConcurrent.Constants.Constants.*;
import AfternoonRacesConcurrent.Monitors.GeneralRepository;
import AfternoonRacesConcurrent.Entities.Spectators.SpectatorsInterfaces.*;
import static AfternoonRacesConcurrent.Entities.Spectators.SpectatorsEnum.SpectatorStates.*;

public class Spectator extends Thread{
	
	private final int spec_id;
	private int money;
	
	GeneralRepository general_repository;
	 
	private final ISpectator_BettingCenter spectator_betting_center;
	private final ISpectator_ControlCenter spectator_control_center;
	private final ISpectator_Paddock spectator_paddock;
	//private final ISpectator_RacingTrack spectator_racing_track;
		
	public Spectator(int spec_id, int money, ISpectator_BettingCenter spectator_betting_center, ISpectator_ControlCenter spectator_control_center, ISpectator_Paddock spectator_paddock, GeneralRepository rep)
	{
		this.spec_id = spec_id;
		this.money = money; 
		this.spectator_betting_center = spectator_betting_center;
		this.spectator_control_center = spectator_control_center;
		this.spectator_paddock = spectator_paddock;
		this.general_repository = rep;
	}
	
	@Override
	public void run()
	{	
		
        boolean celebrating = false;
        int race_number = 0;
         
        System.out.println(general_repository.getSpectatorState(this.spec_id)+", SPECTATOR " + this.spec_id);
        
        general_repository.initLog(); // prints the legend and the format of the logger
        general_repository.writeSpecSt(general_repository.getSpectatorState(this.spec_id));
        general_repository.makeLogger();
        
        /* spectator main lifecycle */
        while(!celebrating) {
            switch(general_repository.getSpectatorState(this.spec_id)) {

                case OUTSIDE_HIPPIC_CENTER :
                    spectator_paddock.waitForNextRace(this.spec_id,general_repository); // spectators are waiting for a race to start
                    general_repository.makeLogger();
                    break;

                case WAITING_FOR_A_RACE_TO_START :
                    spectator_paddock.goCheckHorses(spec_id,general_repository); // spectators go watching the horses after all oF them are at the paddock
                    general_repository.makeLogger();
                    break;

                case APPRAISING_THE_HORSES : 
                    int bet_money = ThreadLocalRandom.current().nextInt(0, money);
                    this.money = money - bet_money;
                    spectator_betting_center.placeABet(this.spec_id, ThreadLocalRandom.current().nextInt(0, NUM_HORSES), bet_money,general_repository);  // spectators make a bet
                    general_repository.setSpectatorState(PLACING_A_BET, spec_id);
                    general_repository.writeSpecSt(PLACING_A_BET);
                    general_repository.makeLogger();
                    break;

                case PLACING_A_BET:
                    spectator_control_center.goWatchTheRace(this.spec_id,general_repository); // spectators go watch the race
                    general_repository.setSpectatorState(WATCHING_A_RACE, this.spec_id);
                    general_repository.writeSpecSt(WATCHING_A_RACE);
                    general_repository.makeLogger();
                    break;

                case WATCHING_A_RACE:
                    race_number++;
                    System.out.println("-------> " + race_number + " " + NUM_RACES );
                    
                    if(spectator_control_center.haveIwon(this.spec_id, general_repository)) { // spectator know if they have won after the broker have reported the results
                        this.money += spectator_betting_center.goCollectTheGains(this.spec_id, general_repository);
                        general_repository.setSpectatorState(COLLECTING_THE_GAINS, this.spec_id);
                        general_repository.writeSpecSt(COLLECTING_THE_GAINS);
                        general_repository.makeLogger();
                        
                    }
                    else if(race_number != NUM_RACES) {
                        spectator_paddock.waitForNextRace(this.spec_id, general_repository);
                        general_repository.makeLogger();
                    }
                    else{
                        spectator_control_center.relaxABit(spec_id);
                        general_repository.setSpectatorState(CELEBRATING, this.spec_id);
                        general_repository.writeSpecSt(CELEBRATING);
                        general_repository.makeLogger();
                    }
                    break;

                case COLLECTING_THE_GAINS:
                    if(race_number!=NUM_RACES) {
                        spectator_paddock.waitForNextRace(this.spec_id, general_repository);
                        general_repository.makeLogger();
                        
                    }
                    else {
                        spectator_control_center.relaxABit(this.spec_id);
                        general_repository.setSpectatorState(CELEBRATING, this.spec_id);
                        general_repository.writeSpecSt(CELEBRATING);
                        general_repository.makeLogger();
                    }
                    break;

                case CELEBRATING:
                    celebrating = true;
                    break;

                default:
                    break;
            }

        }
        System.out.println("------ SPECTATOR "+ this.spec_id +" LIFECYCLE ENDED -------");
       
	}
}