package AfternoonRacesRemote.Monitors.Stable;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static AfternoonRacesRemote.Constants.Constants.*; 
import AfternoonRacesRemote.Entities.Horses.HorsesInterfaces.IHorse_Stable;
import AfternoonRacesRemote.Entities.Broker.BrokerInterfaces.IBroker_Stable;
import AfternoonRacesRemote.Registry.RegistryConfig;
import AfternoonRacesRemote.interfaces.Register;
import AfternoonRacesRemote.interfaces.StableInterface;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Passive class that represents the Stable where horses stay until they are called to the Paddock by the Broker.
 * Broker can only release the horses from the stable until all of them are in it
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */

public class Stable implements IBroker_Stable, IHorse_Stable, StableInterface {
	
    private final ReentrantLock rl;

    private final Condition broker_condition;
    private final Condition horse_condition;

    private int stable_horses;

    private boolean go_to_paddock = false;
    
    String rmiRegHostName;
    int rmiRegPortNumb;
    
    /**
     * Create a new instance of the stable
     * Initializes the ReentrantLock and Broker and Horse block conditions
     * @param rmiRegHostName
     * @param rmiRegPortNumb
     */
    public Stable(String rmiRegHostName, int rmiRegPortNumb){
        rl = new ReentrantLock(true);
        broker_condition = rl.newCondition();
        horse_condition = rl.newCondition();
        this.rmiRegHostName = rmiRegHostName;
        this.rmiRegPortNumb = rmiRegPortNumb;
    }

    /**
     * Horse proceeds to stable
     * @param horse_id Id of the horse
     * @param race_number Number of the race
     */
    @Override
    public void proceedToStable(int horse_id, int race_number) {	
        /*
         * When everything starts, the horses are assumed to be AT_THE_FIELDS, so the broker must send them to the stable.
         * After that, broker is waken up in oder to summonHorsesToPaddock
        */

        rl.lock();

        try {
            if(race_number == NUM_RACES){
                //general_repository.setHorseState(AT_THE_STABLE, horse_id);
                //general_repository.writeHorseSt(AT_THE_STABLE);
                return;
            }

            stable_horses++;

            if(stable_horses == NUM_HORSES) broker_condition.signal(); // signal broker in case all horses have reached the stable 

            while(!go_to_paddock) { // while horses are not at the paddock
                try {
                    horse_condition.await();
                }catch(Exception ex) { }
            }
            stable_horses--;	// reset 
            if(stable_horses == 0) // avoid deadlock
                go_to_paddock = false;

        } finally {
            rl.unlock();
        }

    }

    /**
     * Broker waits for all horses to be at the stable before summoning them to paddock
     */
    @Override
    public void summonHorsesToPaddock() {
        /*
         * broker next state is ANOUNCING_NEXT_RACE and he will be blocked until the last spectator reaches the paddock
         */

        rl.lock();            
        try {

            try {
                while(stable_horses < NUM_HORSES) {
                    broker_condition.await(); // broker waits until all horses are in the stable
                }
            }catch(Exception e) { }
            go_to_paddock = true;
            horse_condition.signalAll(); // wake up all horses to send them to the paddock => proceedToPaddock

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
        String nameEntryObject = RegistryConfig.RMI_REGISTRY_STABLE_NAME;
        
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
            System.out.println("Stable registration exception: " + e.getMessage());
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("Stable not bound exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        // Unexport; this will also remove us from the RMI runtime
        try {
            UnicastRemoteObject.unexportObject(this, true);
        } catch (NoSuchObjectException ex) {
            System.exit(1);
        }

        System.out.println("Stable closed.");
    }
}
