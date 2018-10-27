package AfternoonRacesRemote.Monitors.BettingCenter;

import AfternoonRacesRemote.Entities.Broker.BrokerInterfaces.IBroker_BettingCenter;
import AfternoonRacesRemote.Entities.Spectators.SpectatorsInterfaces.ISpectator_BettingCenter;
import static AfternoonRacesRemote.Constants.Constants.*;
import AfternoonRacesRemote.Registry.RegistryConfig;
import AfternoonRacesRemote.interfaces.BettingCenterInterface;
import AfternoonRacesRemote.interfaces.GeneralRepositoryInterface;
import AfternoonRacesRemote.interfaces.Register;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

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


public class BettingCenter implements IBroker_BettingCenter, ISpectator_BettingCenter, BettingCenterInterface {
    
    String rmiRegHostName;
    int rmiRegPortNumb;
    
    private final ReentrantLock rl;
    private final Condition broker_condition, spectator_condition;
    
    private int broker_money = 0; // total money broker collects from spectator bets

    private int accepted_bets = 0; // number of accepted bets by the broker

    private int win_amount = 0;

    private int honored_bets = 0;
    
    private final GeneralRepositoryInterface generalRepositoryInterface;
    
    // data structure containing the ids of the spectators waiting for their bets to be accepted
    private Queue<Integer> fifo_bet = new LinkedList<Integer>();
    private Queue<Integer> fifo_collect_gains = new LinkedList<Integer>();

    private boolean[] bets_state = new boolean[NUM_SPECTATORS]; // state of the bets for each spectator

    public BettingCenter(GeneralRepositoryInterface gri, String rmiRegHostName, int rmiRegPortNumb)
    {
        rl = new ReentrantLock(true);		
        broker_condition = rl.newCondition();
        spectator_condition = rl.newCondition(); 
        
        this.generalRepositoryInterface = gri;
        this.rmiRegHostName = rmiRegHostName;
        this.rmiRegPortNumb = rmiRegPortNumb;
    }
    
    private void filterZeroAmountBets() {
        try {
            generalRepositoryInterface.filterZeroAmountBets();
        }
        catch(RemoteException e) {
            System.err.println("Excepção na invocação remota de método filterZeroAmountBets()" + e.getMessage() + "!");
            System.exit(1);
        }
    }
    
    private void setBetInfo(int spec_id, int[] info) {
        try {
            generalRepositoryInterface.setBetInfo(spec_id, info);
        }
        catch(RemoteException e) {
            System.err.println("Excepção na invocação remota de método setBetInfo()" + e.getMessage() + "!");
            System.exit(1);
        }
    }
    
    private int getWinningSpectatorsSize() { 
        int w = 0;
        try {
          w = generalRepositoryInterface.getWinningSpectatorsSize();
        }
        catch(RemoteException e) {
            System.err.println("Excepção na invocação remota de método getWinningSpectatorsSize()" + e.getMessage() + "!");
            System.exit(1);
        }
        
        return w;
    }

    private void clearBetInfo() { 
        try {
            generalRepositoryInterface.clearBetInfo();
        }
        catch(RemoteException e) {
            System.err.println("Excepção na invocação remota de método clearBetInfo()()" + e.getMessage() + "!");
            System.exit(1);
        }
    }

    private void clearHorseWinners() {
        try {
            generalRepositoryInterface.clearHorseWinners();
        }
        catch(RemoteException e) {
            System.err.println("Excepção na invocação remota de método clearHorseWinners()" + e.getMessage() + "!");
            System.exit(1);
        }
    }

    private void clearWinningSpectators() {
        try {
            generalRepositoryInterface.clearWinningSpectators();
        }
        catch(RemoteException e) {
            System.err.println("Excepção na invocação remota de método clearWinningSpectators()" + e.getMessage() + "!");
            System.exit(1);
        }
    }
    
    /**
     * broker waken up by placeABet of a spectator and blocks again until next placeABet 
    // transition only occurs after all spectators have placed a bet 
     */
    @Override
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
                filterZeroAmountBets(); // filter any bets of amount 0 (because spectators always bet so they can bet 0)
            }catch(Exception ex){ }
            
            accepted_bets = 0; // reset
            
        } finally {
            rl.unlock();
        }
    }

    // spectator places a bet. Each bet consists of the spectator id, the id of the horse
    // hes going to bet for and the money he is betting
    // woken up by the broker when the betting is done
    @Override
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
    @Override
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
    
    @Override
    public void signalShutdown() {
        Register reg = null;
        Registry registry = null;
        
        /*
        String rmiRegHostName;
        int rmiRegPortNumb; 
        rmiRegHostName = "localhost";
        rmiRegPortNumb = 1099;
        */
        
        try {
            registry = LocateRegistry.getRegistry(rmiRegHostName, rmiRegPortNumb);
        } 
        catch (RemoteException ex) {
            System.out.println("Erro ao localizar o registo");
            System.exit(1);
        }
        
        String nameEntryBase = RegistryConfig.RMI_REGISTER_NAME;
        String nameEntryObject = RegistryConfig.RMI_REGISTRY_BETTINGCENTER_NAME;
        
        try {
            reg = (Register) registry.lookup(nameEntryBase);
        } catch (RemoteException e) {
            System.out.println("RegisterRemoteObject lookup exception: " + e.getMessage());
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("RegisterRemoteObject not bound exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        
        // Unregister ourself
        try {
            reg.unbind(nameEntryObject);
        } catch (RemoteException e) {
            System.out.println("Betting Center registration exception: " + e.getMessage());
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("Betting Center not bound exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        // Unexport; this will also remove us from the RMI runtime
        try {
            UnicastRemoteObject.unexportObject(this, true);
        } catch (NoSuchObjectException ex) {
            System.exit(1);
        }

        System.out.println("Betting Center closed.");
    }
}