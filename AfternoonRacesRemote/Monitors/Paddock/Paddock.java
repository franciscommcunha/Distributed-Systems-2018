package AfternoonRacesRemote.Monitors.Paddock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import AfternoonRacesRemote.Entities.Broker.BrokerInterfaces.IBroker_Paddock;
import AfternoonRacesRemote.Entities.Horses.HorsesInterfaces.IHorse_Paddock;
import AfternoonRacesRemote.Entities.Spectators.SpectatorsInterfaces.ISpectator_Paddock;
import static AfternoonRacesRemote.Constants.Constants.*;
import AfternoonRacesRemote.Registry.RegistryConfig;
import AfternoonRacesRemote.interfaces.PaddockInterface;
import AfternoonRacesRemote.interfaces.Register;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Passive class that represents the Paddock that contains the horses until they are evaluated by the spectators 
 * that are going to watch the race.
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */

public class Paddock implements IHorse_Paddock, ISpectator_Paddock, IBroker_Paddock, PaddockInterface {
	
    private final ReentrantLock rl;

    private final Condition horse_condition;
    private final Condition spectator_condition;
    private final Condition broker_condition;
	
    private int paddock_horses = 0; // number of horses at the paddock
    private int paddock_spectators = 0; // // number of spectators at the paddock

    private boolean horses_at_paddock= false; // reset done at proceedToStartLine
    private boolean spectators_at_paddock = false; // reset done at goCheckHorses
    
    private boolean horses_at_start_line = false; // reset done at goCheckHorses
    private boolean last_horse_leaving_paddock = false; // reset done at proceedToStartLine

    private boolean go_broker = false; // reset done at goCheckHorses
	
    String rmiRegHostName;
    int rmiRegPortNumb;
    
    public Paddock(String rmiRegHostName, int rmiRegPortNumb)
    {
        rl = new ReentrantLock(true);
        horse_condition = rl.newCondition();
        spectator_condition = rl.newCondition();
        broker_condition = rl.newCondition();
        this.rmiRegHostName = rmiRegHostName;
        this.rmiRegPortNumb = rmiRegPortNumb;
    }
	
    @Override
    public void summonHorsesToPaddock() {
		
        // broker sends the horses to the paddock and blocks until they do not reach there

        // OPENING_THE_EVENT
        rl.lock();
        try {
            System.out.println("summonHorsesToPaddock() - PADDOCK");
            try {
                while(!go_broker) { // broker is blocked until all the horses are at the paddock
                    broker_condition.await();
                }
            } catch (Exception e) {}

            //general_repository.setBrokerState(ANNOUNCING_NEXT_RACE);
            //general_repository.writeStatB(ANNOUNCING_NEXT_RACE);

            go_broker = false;
        } 
        finally {
            rl.unlock();
        }
        // ANNOUCING_NEXT_RACE
    }

	@Override
    public void proceedToPaddock(int horse_id) 
    {
 
        // AT_THE_STABLE

        rl.lock();

        try {	
            //general_repository.writeHorseSt(AT_THE_PADDOCK);

            paddock_horses++; // increases the number of horses at the paddock
            //System.out.println("AT_THE_PADDOCK, HORSE " + horse_id);
            if(paddock_horses == NUM_HORSES) { // checks if all horses are at the paddock
                horses_at_paddock = true;// signal -> waitForNextRace()
                spectator_condition.signalAll(); // wakes up spectators blocked at waitForNextRace
            }				
            try {
                while(! spectators_at_paddock) { // waits until spectators reaches the paddock -> true on waitForNextRace
                    horse_condition.await(); // horses will be awake at goCheckHorses
                }

                //general_repository.setHorseState(AT_THE_PADDOCK, horse_id);
                //general_repository.writeHorseSt(AT_THE_PADDOCK);

            }catch(Exception e) {}
            horses_at_start_line = false;
        }         
        finally {
            rl.unlock(); 
        }
        // AT_THE_PADDOCK	
    }

    @Override
    public void waitForNextRace(int spec_id) 
    {
        // the speectators are blocked until all the horses are at the paddock
        // when horses are at the paddock, they can go check them
	
        // OUTSIDE_HIPPIC_CENTER
        //System.out.println(horses_at_paddock);
        rl.lock();
        try {
            //System.out.println("> waitingForNextRace SPECTATOR " + spec_id);
            try {
                while (!horses_at_paddock) { // waits until all horses are at the paddock -> true em proceedToPaddock
                    spectator_condition.await();	// will be awaken at proceedToPaddock
                }
            } catch (Exception e) {}
            spectators_at_paddock = true; // signal -> proceedToPaddock()

             //general_repository.setSpectatorState(WAITING_FOR_A_RACE_TO_START, spec_id);
             //general_repository.writeSpecSt(WAITING_FOR_A_RACE_TO_START);

            horse_condition.signalAll(); // unblock horses blocked at proceedToPaddock
        } 
        finally { 
            rl.unlock();
        }
        // WAITING_FOR_A_RACE_TO_START
    }	
		
    @Override
    public void goCheckHorses(int spec_id) {
		
		// the spectators go check the horses, so they can make a decision in which horse they will bet
		// after all the spectators are at the paddock,  the broker can procced to accept bets and horses can go to the star line
		
		// WAITING_FOR_NEXT_RACE
            rl.lock();
            try {
                paddock_spectators++; // increases the number of spectators at the paddock
                //System.out.println("> goCheckHorses, SPECTATOR " + spec_id);

                //general_repository.writeSpecSt(APPRAISING_THE_HORSES);

                if (paddock_spectators == NUM_SPECTATORS) {
                    go_broker = true; // changes the value of the flag, broker can now unblock
                    horses_at_start_line = true; // changes the value of the flag, horses can unblock and go to the star line
                    broker_condition.signal();
                    horse_condition.signalAll();
                }
                try {
                    while(! last_horse_leaving_paddock) {
                        spectator_condition.await();
                    }
                } catch (Exception e) {}

                paddock_spectators--; // decreases the number of spectators at the paddock

                if(paddock_spectators == 0) {
                    spectators_at_paddock = false;
                    last_horse_leaving_paddock = false;
                }

                //general_repository.setSpectatorState(APPRAISING_THE_HORSES, spec_id);

                //System.out.println("APRAISING_THE_HORSES, SPECTATOR " + spec_id);	
            } 
            finally {
                rl.unlock();
            }
            // APRAISING_THE_HORSES
	}
	
	@Override
    public void proceedToStartLine(int horse_id) {
		
            // horses are blocked until all the spectators dont reach the paddock
            // after they reach there, they can go to the start line
        
            // AT_THE_PADDOCK

            rl.lock();
            try {
                paddock_horses--; // decreases the number of horses at the paddock

                //general_repository.writeHorseSt(AT_THE_PADDOCK);

                //System.out.println("AT_THE_START_LINE, HORSE " + horse_id);

                if (paddock_horses == 0 ) { // horses go to the start line and wake up spectators to go betting 
                    spectator_condition.signalAll(); // wakes up spectators blocked at goCheckHorses 
                }

                try {
                    while(! horses_at_start_line) { // horses are blocked waiting for spectators
                       horse_condition.await();
                    }
                } catch (Exception e) {}

                if(paddock_horses == 0){ 
                    last_horse_leaving_paddock = true; // changes the value of the flach
                    horses_at_paddock = false; // reset 
                }

                //general_repository.setHorseState(AT_THE_START_LINE, horse_id);
                //general_repository.writeHorseSt(AT_THE_START_LINE);

                spectator_condition.signalAll(); // wakes up spectators blocked at goCheckHorses 
            } 
            finally {
                    rl.unlock();
            }

            // AT_THE_START_LINE
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
        String nameEntryObject = RegistryConfig.RMI_REGISTRY_PADDOCK_NAME;
        
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
            System.out.println("Paddock registration exception: " + e.getMessage());
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("Paddock not bound exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        // Unexport; this will also remove us from the RMI runtime
        try {
            UnicastRemoteObject.unexportObject(this, true);
        } catch (NoSuchObjectException ex) {
            System.exit(1);
        }

        System.out.println("Paddock closed.");
    }
}
