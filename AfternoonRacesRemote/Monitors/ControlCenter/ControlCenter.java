package AfternoonRacesRemote.Monitors.ControlCenter;

import AfternoonRacesRemote.Entities.Broker.BrokerEnum.BrokerStates;
import AfternoonRacesRemote.Entities.Broker.BrokerInterfaces.IBroker_ControlCenter;
import AfternoonRacesRemote.Entities.Spectators.SpectatorsInterfaces.ISpectator_ControlCenter;
import AfternoonRacesRemote.Registry.RegistryConfig;
import AfternoonRacesRemote.interfaces.ControlCenterInterface;
import AfternoonRacesRemote.interfaces.GeneralRepositoryInterface;
import AfternoonRacesRemote.interfaces.Register;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

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

public class ControlCenter implements IBroker_ControlCenter, ISpectator_ControlCenter, ControlCenterInterface {
	
    private final ReentrantLock rl;

    private final Condition broker_condition;
    private final Condition spectator_condition;
    private int spec_watching_race = 0;

    private boolean results_reported = false;

    private boolean are_there_winners = false;

    private boolean winners_reported = false;
    
    private final GeneralRepositoryInterface generalRepositoryInterface;
    
    String rmiRegHostName;
    int rmiRegPortNumb;
    
    public ControlCenter(GeneralRepositoryInterface gri, String rmiRegHostName, int rmiRegPortNumb)
    {
        rl = new ReentrantLock(true);
        broker_condition = rl.newCondition();
        spectator_condition = rl.newCondition();
        
        this.generalRepositoryInterface = gri;
        this.rmiRegHostName = rmiRegHostName;
        this.rmiRegPortNumb = rmiRegPortNumb;
    }
    
    private int getHorseResultSize() {
        int h = 0;
        
        try {
            h = generalRepositoryInterface.getHorseResultSize();
        }
        catch (RemoteException e) {
            System.err.println("Excepção na invocação remota de método getHorseResultSize()" + e.getMessage() + "!");
            System.exit(1);
        }
        
        return h;
    }

    private int[] removeHorseResult() {
        int[] r = null;
        
        try {
           r = generalRepositoryInterface.removeHorseResult();
        }
        catch (RemoteException e) {
            System.err.println("Excepção na invocação remota de método removeHorseResult()" + e.getMessage() + "!");
            System.exit(1);
        }
        
        return r;
    }

    private void addHorseWinner(int horse_id) {
        try {
            generalRepositoryInterface.addHorseWinner(horse_id);
        }
        catch (RemoteException e) {
            System.err.println("Excepção na invocação remota de método addHorseWinner()" + e.getMessage() + "!");
            System.exit(1);
        }
    }

    private int[] peekHorseResult() {
        int[] r = null;
        
        try {
            r = generalRepositoryInterface.peekHorseResult();
        }
        catch (RemoteException e) {
            System.err.println("Excepção na invocação remota de método peekHorseResult()" + e.getMessage() + "!");
            System.exit(1);
        }
        
        return r;
    }

    private Map<Integer, int[]> getBetInfoReferenceCopy() {
        
        Map<Integer,int[]> bet_info = null;
        
        try {
            bet_info = generalRepositoryInterface.getBetInfoReferenceCopy();
        }
        catch (RemoteException e) {
            System.err.println("Excepção na invocação remota de método getBetInfoReferenceCopy()" + e.getMessage() + "!");
            System.exit(1);
        }
        
        return bet_info;
    }

    private boolean horseIsWinner(int horse_id) {
        boolean h = false;
        
        try {
            h = generalRepositoryInterface.horseIsWinner(horse_id);
        }
        catch (RemoteException e) {
            System.err.println("Excepção na invocação remota de método horseIsWinner()" + e.getMessage() + "!");
            System.exit(1);
        }
        
        return h;
    }

    private void addWinningSpectator(int spec_id) {
        try {
            generalRepositoryInterface.addWinningSpectator(spec_id);
        }
        catch (RemoteException e) {
            System.err.println("Excepção na invocação remota de método addWinningSpectator()" + e.getMessage() + "!");
            System.exit(1);
        }
    }

    private boolean spectatorIsWinner(int spec_id) { 
        boolean s = false;
        
        try {
            s = generalRepositoryInterface.spectatorIsWinner(spec_id);
        }
        catch (RemoteException e) {
            System.err.println("Excepção na invocação remota de método spectatorIsWinner()" + e.getMessage() + "!");
            System.exit(1);
        }
        
        return s;
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
        String nameEntryObject = RegistryConfig.RMI_REGISTRY_CONTROLCENTER_NAME;
        
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
            System.out.println("Control Center registration exception: " + e.getMessage());
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("Control Center not bound exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        // Unexport; this will also remove us from the RMI runtime
        try {
            UnicastRemoteObject.unexportObject(this, true);
        } catch (NoSuchObjectException ex) {
            System.exit(1);
        }

        System.out.println("Control Center closed.");
    }

}
