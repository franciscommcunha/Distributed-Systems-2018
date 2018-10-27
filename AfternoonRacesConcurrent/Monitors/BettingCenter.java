package AfternoonRacesConcurrent.Monitors;

import AfternoonRacesConcurrent.Entities.Broker.BrokerInterfaces.IBroker_BettingCenter;
import AfternoonRacesConcurrent.Entities.Spectators.SpectatorsInterfaces.ISpectator_BettingCenter;
import static AfternoonRacesConcurrent.Constants.Constants.*;
import static AfternoonRacesConcurrent.Entities.Spectators.SpectatorsEnum.SpectatorStates.*;
import static AfternoonRacesConcurrent.Entities.Broker.BrokerEnum.BrokerStates.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BettingCenter implements IBroker_BettingCenter, ISpectator_BettingCenter {
	
    private final ReentrantLock rl;
    private final Condition broker_condition, spectator_condition;

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
    }

    // broker waken up by placeABet of a spectator and blocks again until next placeABet 
    // transition only occurs after all spectators have placed a bet 
    public void acceptTheBets(GeneralRepository general_repository)
    {
        rl.lock();
        try {
            
            try {
                System.out.println("acceptTheBets() ");
                while(accepted_bets < NUM_SPECTATORS) {
                    while(fifo_bet.size()==0) { // while no new bets in queue
                        broker_condition.await(); // broker is blocked
                    }
                    accepted_bets++;
                    int id = fifo_bet.remove(); // remove spec id from queue (bet already accepted)
                    bets_state[id] = true;
                    spectator_condition.signal(); 
                } 
                general_repository.filterZeroAmountBets(); // filter any bets of amount 0 (because spectators always bet so they can bet 0)
            }catch(Exception ex){ }
            
            accepted_bets = 0; // reset
            
        } finally {
            rl.unlock();
        }
    }

    // spectator places a bet. Each bet consists of the spectator id, the id of the horse
    // hes going to bet for and the money he is betting
    // woken up by the broker when the betting is done
    public void placeABet(int spec_id, int horse_id, int money,  GeneralRepository general_repository)
    {	
        rl.lock();
        try {
            System.out.println("SPECTATOR "+spec_id+" placeABet horse:"+ horse_id + ", money: " + money);
            try {
                while(!bets_state[spec_id]) {
                    int[] info = { spec_id, horse_id, (int)money };
                    general_repository.setBetInfo(spec_id, info);
                    
                    fifo_bet.add(spec_id); // add spectator id to fifo and signal broker
   
                    broker_condition.signal(); // wake up the broker 
                    spectator_condition.await();				
                }
            }catch(Exception ex) { }

            broker_money += money; // update broker money
            
            bets_state[spec_id] = false; // reset bets_state
            general_repository.setSpectatorState(PLACING_A_BET, spec_id);
           
        } 
        finally {
            rl.unlock();
        }

    }

    // Broker honor the bets to the spectators who have bet on the winner horse
    @Override
    public void honourTheBets(GeneralRepository general_repository) {
        rl.lock();
        try {
        System.out.println(">>>>>>>>>> honourTheBets() " + honored_bets + " " + general_repository.getWinningSpectatorsSize());
        
        // amount each winner gets
        win_amount = broker_money / general_repository.getWinningSpectatorsSize();

        try {
            while(honored_bets < general_repository.getWinningSpectatorsSize()) {      
                broker_condition.await(); // blocks waiting for new bets
                fifo_collect_gains.remove();    // accept a bet
                spectator_condition.signal(); // wake up spectator  
            }
        }catch(Exception ex) { }
        System.out.println(">>>>>>>>>> BROKER - ALL BETS HONORED, WIN AMOUNT : " + win_amount);

        // clear variables / data structures after all bets honoured
        fifo_collect_gains.removeAll(fifo_collect_gains); // reset goCollectGains FIFO
        fifo_bet.removeAll(fifo_bet); // reset placeABet FIFO
        general_repository.clearBetInfo(); // reset bet_info
        general_repository.clearHorseWinners(); // reset horse_winners ArrayList
        general_repository.clearWinningSpectators(); // reset winning_spectators 
        honored_bets = 0; // reset
        win_amount = 0; // reset
        broker_money = 0; // reset
            
        }finally{
            rl.unlock();
        }
    }

    // only winning spectators call goCollectTheGains()
    public double goCollectTheGains(int spec_id,  GeneralRepository general_repository)
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

}
