package AfternoonRacesConcurrent.Monitors;

import AfternoonRacesConcurrent.Constants.Constants;
import AfternoonRacesConcurrent.Entities.Broker.BrokerEnum.BrokerStates;
import AfternoonRacesConcurrent.Entities.Broker.BrokerInterfaces.IBroker_ControlCenter;
import AfternoonRacesConcurrent.Entities.Spectators.SpectatorsInterfaces.ISpectator_ControlCenter;

import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ControlCenter implements IBroker_ControlCenter, ISpectator_ControlCenter {
	
	private final ReentrantLock rl;

    private final Condition broker_condition;
    private final Condition spectator_condition;

    private int spec_watching_race = 0;

    private boolean results_reported = false;

    private boolean are_there_winners = false;

    private boolean winners_reported = false;
    
    public ControlCenter()
    {
        rl = new ReentrantLock(true);
        broker_condition = rl.newCondition();
        spectator_condition = rl.newCondition();
    }

    @Override
    public void goWatchTheRace(int spec_id, GeneralRepository general_repository) 
    {	
    		// spectators go watch the race and they block until the broker report the results of the race
    		
        rl.lock();

        try {
            spec_watching_race++;
           
            try {
                while(!results_reported && !winners_reported) { // spectators block until broker reports results of the race (winner horses/spectators)
                    spectator_condition.await();
                }
            }catch(Exception ex) { }
        
           
        } finally {
            rl.unlock();
        }
    }

    @Override
   
    public void reportResults(GeneralRepository general_repository)
    {
    		// broker report the results of the race and wakes up spectators blocked  
    	
        rl.lock();
        try {
            System.out.println("reportResults()");
            int horse_result_size =  general_repository.getHorseResultSize();
            for(int i=0 ; i< horse_result_size ; i++) {
                int[] head = general_repository.removeHorseResult();
                general_repository.addHorseWinner(head[0]);
                if(general_repository.getHorseResultSize()>0){
                    if(head[1] != general_repository.peekHorseResult()[1]) { // if diferent then no more winners
                        break;
                    }
                }
            }

            System.out.println("REPORT HORSE WINNER(S) : " +  general_repository.horseWinnerToString());
           
            results_reported = true;
            spectator_condition.signalAll(); // wakes up spectators blocked  at goWatchTheRace
            
        } finally {
            rl.unlock();
        }

    }

    @Override
    public boolean areThereAnyWinners(GeneralRepository general_repository) {		
    	
        // broker checks if there are any bets on the winning horse(s)
    	
        rl.lock();
        boolean has_winners = false;
        Map<Integer,int[]> bet_info = general_repository.getBetInfoReferenceCopy();
        System.out.println("areThereAnyWinners()");
        try {
            for(Map.Entry<Integer,int[]> entry: bet_info.entrySet()){
                if(general_repository.horseIsWinner(entry.getValue()[1])){ 
                    general_repository.addWinningSpectator(entry.getKey());
                    System.out.println("SPECTATOR WINNER(S) : " + entry.getKey());
                    has_winners = true;
                }
            }
            winners_reported = true; 
            spectator_condition.signalAll();
            return has_winners;
        }finally{
            rl.unlock();
        }
    }

    @Override
    public boolean haveIwon(int spec_id, GeneralRepository general_repository)
    {	
    		// spectator checks if they have won in order to claim the prize of bet
    	
        rl.lock();
        boolean has_won = false;
        try {

            if(general_repository.spectatorIsWinner(spec_id)){
                has_won = true;
               
            }
            spec_watching_race--; // decreases the number of spectators watching the race
            if(spec_watching_race == 0){
                results_reported = false; // reset
                winners_reported = false; // reset
            }
            
        } finally {
            rl.unlock();
        }
        return has_won;
    }

    public void entertainTheGuests(GeneralRepository general_repository)
    {
            rl.lock();
            try {
                //System.out.println("ENTERTAING_THE_GUESTS");
            		general_repository.setBrokerState(BrokerStates.PLAYING_HOST_AT_THE_BAR);
            		general_repository.writeStatB(BrokerStates.PLAYING_HOST_AT_THE_BAR);
            } finally {
                    rl.unlock();
            }
    }


    @Override
    public void relaxABit(int spec_id)
    {
            rl.lock();
            try {
                //System.out.println("CELEBRATING " + spec_id);
            } finally {
                    rl.unlock();
            }
    }

		
}