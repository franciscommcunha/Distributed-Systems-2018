package AfternoonRacesDistributed.Monitors.BettingCenter;

import AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelClient;
import static AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelPorts.NAME_GENERAL_REPOSITORY;
import static AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelPorts.PORT_GENERAL_REPOSITORY;
import AfternoonRacesDistributed.Entities.Broker.BrokerInterfaces.IBroker_BettingCenter;
import AfternoonRacesDistributed.Entities.Spectators.SpectatorsInterfaces.ISpectator_BettingCenter;
import static AfternoonRacesDistributed.Constants.Constants.*;
import static AfternoonRacesDistributed.Entities.Spectators.SpectatorsEnum.SpectatorStates.*;
import AfternoonRacesDistributed.Messages.RepositoryMessage.RepositoryMessage;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Passive class that represents the Betting Center, where spectators place bets on participant horses and
 * the broker accepts such bets and, in the end, if there are winners, honours the bets. 
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */


public class BettingCenter implements IBroker_BettingCenter, ISpectator_BettingCenter {
	
    private final ReentrantLock rl;
    private final Condition broker_condition, spectator_condition;
    
    private CommunicationChannelClient cc_repository;

    private int broker_money = 0; // total money broker collects from spectator bets

    private int accepted_bets = 0; // number of accepted bets by the broker

    private int win_amount = 0;

    private int honored_bets = 0;

    // data structure containing the ids of the spectators waiting for their bets to be accepted
    private Queue<Integer> fifo_bet = new LinkedList<Integer>();
    private Queue<Integer> fifo_collect_gains = new LinkedList<Integer>();

    private boolean[] bets_state = new boolean[NUM_SPECTATORS]; // state of the bets for each spectator

    public BettingCenter()
    {
        rl = new ReentrantLock(true);		
        broker_condition = rl.newCondition();
        spectator_condition = rl.newCondition(); 
        
        this.cc_repository = new CommunicationChannelClient(NAME_GENERAL_REPOSITORY, PORT_GENERAL_REPOSITORY);
    }
    
    /**
     * Sends SET_BET_INFO message type to repository
     * @param spec_id id of the spectator
     * @param info array containing the horse id and the bet amout
     */
    public void setBetInfo(int spec_id, int[] info){
        RepositoryMessage response;
        while(!cc_repository.open()){
            try{
                Thread.sleep(1000);
            }catch(Exception exc){ }
        }
        cc_repository.writeObject(new RepositoryMessage(RepositoryMessage.SET_BET_INFO, spec_id, info));
        response = (RepositoryMessage) cc_repository.readObject();
        cc_repository.close();
    }

    // broker waken up by placeABet of a spectator and blocks again until next placeABet 
    // transition only occurs after all spectators have placed a bet 
    public void acceptTheBets()
    {
        rl.lock();
        try {
            
            try {
                //System.out.println("acceptTheBets() ");
                while(accepted_bets < NUM_SPECTATORS) {
                    while(fifo_bet.size()==0) { // while no new bets in queue
                        broker_condition.await(); // broker is blocked
                    }
                    accepted_bets++;
                    int id = fifo_bet.remove(); // remove spec id from queue (bet already accepted)
                    bets_state[id] = true;
                    spectator_condition.signal(); 
                } 
                //general_repository.filterZeroAmountBets(); // filter any bets of amount 0 (because spectators always bet so they can bet 0)
            }catch(Exception ex){ }
            
            accepted_bets = 0; // reset
            
        } finally {
            rl.unlock();
        }
    }

    // spectator places a bet. Each bet consists of the spectator id, the id of the horse
    // hes going to bet for and the money he is betting
    // woken up by the broker when the betting is done
    public void placeABet(int spec_id, int horse_id, int money)
    {	
        rl.lock();
        System.out.println("placeABet: SPEC: " + spec_id + " HORSE: " + horse_id + " MONEY: " + money);
        try {
            //System.out.println("SPECTATOR "+spec_id+" placeABet horse:"+ horse_id + ", money: " + money);
            try {
                while(!bets_state[spec_id]) {
                    int[] info = { spec_id, horse_id, (int)money };
                    setBetInfo(spec_id, info);
                    
                    fifo_bet.add(spec_id); // add spectator id to fifo and signal broker
   
                    broker_condition.signal(); // wake up the broker 
                    spectator_condition.await();				
                }
            }catch(Exception ex) { }

            broker_money += money; // update broker money
            
            bets_state[spec_id] = false; // reset bets_state
       
        } 
        finally {
            rl.unlock();
        }

    }

    // Broker honor the bets to the spectators who have bet on the winner horse
    @Override
    public void honourTheBets() {
        
        rl.lock();
        try {
        //System.out.println(">>>>>>>>>> honourTheBets() " + honored_bets + " " + general_repository.getWinningSpectatorsSize());
        System.out.println("honourTheBets()");
        // amount each winner gets
        int spec_win_size = getWinningSpectatorsSize();
        System.out.println("getWinningSpectatorSize = " + spec_win_size);
        win_amount = broker_money / spec_win_size;

        try {
            while(honored_bets < spec_win_size) {      
                broker_condition.await(); // blocks waiting for new bets
                fifo_collect_gains.remove();    // accept a bet
                spectator_condition.signal(); // wake up spectator  
            }
        }catch(Exception ex) { }
        //System.out.println(">>>>>>>>>> BROKER - ALL BETS HONORED, WIN AMOUNT : " + win_amount);

        // clear variables / data structures after all bets honoured
        fifo_collect_gains.removeAll(fifo_collect_gains); // reset goCollectGains FIFO
        fifo_bet.removeAll(fifo_bet); // reset placeABet FIFO
        clearBetInfo(); // reset bet_info
        clearHorseWinners(); // reset horse_winners ArrayList
        clearWinningSpectators(); // reset winning_spectators 
        honored_bets = 0; // reset
        win_amount = 0; // reset
        broker_money = 0; // reset
            
        }finally{
            rl.unlock();
        }
        
    }

    // only winning spectators call goCollectTheGains()
    public double goCollectTheGains(int spec_id)
    {	
        rl.lock();
        try {
            try {                
                fifo_collect_gains.add(spec_id); // add spectator to queue;
                honored_bets++;
                broker_condition.signal();
                while(!fifo_collect_gains.contains(spec_id)){ // while bet not accepted
                    spectator_condition.await();    
                }
            }catch(Exception ex){ }
            
            if(fifo_collect_gains.size() == 0){
                broker_condition.signal();
            }
            return win_amount;
        } finally {
            rl.unlock();
        }
    }
    
    public int getWinningSpectatorsSize(){
        RepositoryMessage response;
        while(!cc_repository.open()){
            System.out.println("Repository not open, trying again...");
            try{
                Thread.sleep(1000);
            }catch(Exception ex){ }              
        }
        cc_repository.writeObject(new RepositoryMessage(RepositoryMessage.GET_WINNING_SPECTATOR_SIZE));
        response = (RepositoryMessage) cc_repository.readObject(); 
        cc_repository.close();
        return response.getId();
    }
    
    public void clearBetInfo(){
        while(!cc_repository.open()){
            System.out.println("Repository not open, trying again...");
            try{
                Thread.sleep(1000);
            }catch(Exception ex){ }              
        }
        cc_repository.writeObject(new RepositoryMessage(RepositoryMessage.CLEAR_BET_INFO));
        cc_repository.readObject();
        cc_repository.close();
    }
    
    public void clearHorseWinners(){
        while(!cc_repository.open()){
            System.out.println("Repository not open, trying again...");
            try{
                Thread.sleep(1000);
            }catch(Exception ex){ }              
        }
        cc_repository.writeObject(new RepositoryMessage(RepositoryMessage.CLEAR_HORSE_WINNER));
        cc_repository.readObject();
        cc_repository.close();
    }
    
    public void clearWinningSpectators(){
        while(!cc_repository.open()){
            System.out.println("Repository not open, trying again...");
            try{
                Thread.sleep(1000);
            }catch(Exception ex){ }              
        }
        cc_repository.writeObject(new RepositoryMessage(RepositoryMessage.CLEAR_WINNING_SPECTATOR));
        cc_repository.readObject();
        cc_repository.close();
    }
    

}
