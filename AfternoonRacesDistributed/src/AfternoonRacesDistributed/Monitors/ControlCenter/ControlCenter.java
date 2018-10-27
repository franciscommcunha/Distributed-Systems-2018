package AfternoonRacesDistributed.Monitors.ControlCenter;

import AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelClient;
import static AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelPorts.NAME_GENERAL_REPOSITORY;
import static AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelPorts.PORT_GENERAL_REPOSITORY;
import AfternoonRacesDistributed.Entities.Broker.BrokerEnum.BrokerStates;
import AfternoonRacesDistributed.Entities.Broker.BrokerInterfaces.IBroker_ControlCenter;
import AfternoonRacesDistributed.Entities.Spectators.SpectatorsInterfaces.ISpectator_ControlCenter;
import AfternoonRacesDistributed.Messages.RepositoryMessage.RepositoryMessage;

import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Passive class that represents the Control Center where the broker monitors the race and the watching stand 
 * where the spectators stay while watching the race
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */

public class ControlCenter implements IBroker_ControlCenter, ISpectator_ControlCenter {
	
    private final ReentrantLock rl;

    private final Condition broker_condition;
    private final Condition spectator_condition;
    private CommunicationChannelClient cc_repository;

    private int spec_watching_race = 0;

    private boolean results_reported = false;

    private boolean are_there_winners = false;

    private boolean winners_reported = false;
    
    public ControlCenter()
    {
        rl = new ReentrantLock(true);
        broker_condition = rl.newCondition();
        spectator_condition = rl.newCondition();
        this.cc_repository = new CommunicationChannelClient(NAME_GENERAL_REPOSITORY, PORT_GENERAL_REPOSITORY);
    }
    
    /**
     * Sends REMOVE_HORSE_RESULT message type to repository
     */
    private int[] removeHorseResult() {
        RepositoryMessage response;
        while(!cc_repository.open()){
            try{
                Thread.sleep(1000);
            }catch(Exception exc){ }
        }
        cc_repository.writeObject(new RepositoryMessage(RepositoryMessage.REMOVE_HORSE_RESULT));
        response = (RepositoryMessage) cc_repository.readObject();
        cc_repository.close();
        return response.getArray();
    }
    
    /**
     * Sends GET_HORSE_RESULT_SIZE message type to repository
     */
    private int getHorseResultSize() {
        RepositoryMessage response;
        while(!cc_repository.open()){
            try{
                Thread.sleep(1000);
            }catch(Exception exc){ }
        }
        cc_repository.writeObject(new RepositoryMessage(RepositoryMessage.GET_HORSE_RESULT_SIZE));
        response = (RepositoryMessage) cc_repository.readObject();
        cc_repository.close();
        return response.getId();
    }
    
    /**
     * Sends PEEK_HORSE_RESULT message type to repository
     */
    private int[] peekHorseResult() {
        RepositoryMessage response;
        while(!cc_repository.open()){
            try{
                Thread.sleep(1000);
            }catch(Exception exc){ }
        }
        cc_repository.writeObject(new RepositoryMessage(RepositoryMessage.PEEK_HORSE_RESULT));
        response = (RepositoryMessage) cc_repository.readObject();
        cc_repository.close();
        System.out.println("response.getArray()" + response.getArray());
        return response.getArray();
    }
    
    /**
     * Sends ADD_HORSE_WINNER message type to repository
     */
    private void addHorseWinner(int head) {
        RepositoryMessage response;
        while(!cc_repository.open()){
            try{
                Thread.sleep(1000);
            }catch(Exception exc){ }
        }
        cc_repository.writeObject(new RepositoryMessage(RepositoryMessage.ADD_HORSE_WINNER, head));
        response = (RepositoryMessage) cc_repository.readObject();
        cc_repository.close();
    }
    
    /**
     * Sends GET_BET_INFO_COPY message type to repository
     */
    private Map<Integer,int[]> getBetInfoReferenceCopy() {
        RepositoryMessage response;
        while(!cc_repository.open()){
            try{
                Thread.sleep(1000);
            }catch(Exception exc){ }
        }
        cc_repository.writeObject(new RepositoryMessage(RepositoryMessage.GET_BET_INFO_COPY));
        response = (RepositoryMessage) cc_repository.readObject();
        cc_repository.close();
        return response.getBetInfoReferenceCopy();
    }
    
    /**
     * Sends HORSE_IS_WINNER message type to repository
     */
    private boolean horseIsWinner(int horse_id) {
        RepositoryMessage response;
        while(!cc_repository.open()){
            try{
                Thread.sleep(1000);
            }catch(Exception exc){ }
        }
        cc_repository.writeObject(new RepositoryMessage(RepositoryMessage.HORSE_IS_WINNER, horse_id));
        response = (RepositoryMessage) cc_repository.readObject();
        cc_repository.close();
        System.out.println("horseIsWinner return : " + response.getBoolean());
        return response.getBoolean();
    }
    
    /**
     * Sends ADD_WINNING_SPECTATOR message type to repository
     */
    private void addWinningSpectator(int spec_id) {
        RepositoryMessage response;
        while(!cc_repository.open()){
            try{
                Thread.sleep(1000);
            }catch(Exception exc){ }
        }
        cc_repository.writeObject(new RepositoryMessage(RepositoryMessage.ADD_WINNING_SPECTATOR, spec_id));
        response = (RepositoryMessage) cc_repository.readObject();
        cc_repository.close();
    }
    
    /**
     * Sends SPECTATOR_IS_WINNER message type to repository
     */
    private boolean spectatorIsWinner(int spec_id) {
        RepositoryMessage response;
        while(!cc_repository.open()){
            try{
                Thread.sleep(1000);
            }catch(Exception exc){ }
        }
        cc_repository.writeObject(new RepositoryMessage(RepositoryMessage.SPECTATOR_IS_WINNER, spec_id));
        response = (RepositoryMessage) cc_repository.readObject();
        cc_repository.close();
        return response.getBoolean();
    }
    
    @Override
    public void goWatchTheRace(int spec_id) 
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
        } 
        finally {
            rl.unlock();
        }
    }

    @Override
    public void reportResults()
    {
        // broker report the results of the race and wakes up spectators blocked  
    	
        rl.lock();

        try {
            //System.out.println("reportResults()");
            int horse_result_size =  getHorseResultSize();
            System.out.println("Horse_result_size : "+horse_result_size);
            for(int i=0 ; i< horse_result_size ; i++) {
                int[] head = removeHorseResult();
                System.out.println("head[0]: " + head[0] + ", head[1] " + head[1]);
                addHorseWinner(head[0]);
                System.out.println("addHorseWinner("+head[0]+")");
                if(getHorseResultSize()>0){
                    int a = peekHorseResult()[1];
                    System.out.println("A: "+a);
                    if(head[1] != a) { // if diferent then no more winners
                        break;
                    }
                }
            }
            //System.out.println("REPORT HORSE WINNER(S) : " +  general_repository.horseWinnerToString());
            results_reported = true;
            spectator_condition.signalAll(); // wakes up spectators blocked  at goWatchTheRace
            
        } finally {
            rl.unlock();
        }
        
    }

    @Override
    public boolean areThereAnyWinners() {		
    	
        // broker checks if there are any bets on the winning horse(s)
    	
        rl.lock();
        boolean has_winners = false;
        Map<Integer,int[]> bet_info = getBetInfoReferenceCopy();
        System.out.println(bet_info);
        //System.out.println("areThereAnyWinners()");
        try {
            for(Map.Entry<Integer,int[]> entry: bet_info.entrySet()){
                System.out.println("ENTER FOR");
                System.out.println("horseIsWinner("+entry.getValue()[1]+")");
                if(horseIsWinner(entry.getValue()[1])){ 
                    System.out.println("ENTER IF");
                    addWinningSpectator(entry.getKey());
                    System.out.println("SPECTATOR WINNER(S) : " + entry.getKey());
                    has_winners = true;
                }
            }
            winners_reported = true; 
            spectator_condition.signalAll();
            System.out.println(has_winners);
            return has_winners;
        }finally{
            rl.unlock();
        }
        
    }

    @Override
    public boolean haveIwon(int spec_id)
    {	
        // spectator checks if they have won in order to claim the prize of bet
    	
        rl.lock();
        boolean has_won = false;
        try {

            if(spectatorIsWinner(spec_id)){
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
        
        //return false;
    }
    
    @Override
    public void entertainTheGuests()
    {
        rl.lock();
        try {
            //System.out.println("ENTERTAING_THE_GUESTS");
            //general_repository.setBrokerState(BrokerStates.PLAYING_HOST_AT_THE_BAR);
            //general_repository.writeStatB(BrokerStates.PLAYING_HOST_AT_THE_BAR);
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