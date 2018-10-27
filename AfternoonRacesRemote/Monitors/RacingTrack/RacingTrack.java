package AfternoonRacesRemote.Monitors.RacingTrack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import AfternoonRacesRemote.Entities.Broker.BrokerInterfaces.IBroker_RacingTrack;
import AfternoonRacesRemote.Entities.Horses.HorsesInterfaces.IHorse_RacingTrack;
import static AfternoonRacesRemote.Constants.Constants.*;
import AfternoonRacesRemote.Registry.RegistryConfig;
import AfternoonRacesRemote.interfaces.GeneralRepositoryInterface;
import AfternoonRacesRemote.interfaces.RacingTrackInterface;
import AfternoonRacesRemote.interfaces.Register;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Passive class that represents the Racing Track where horses wait until the broker starts the race after which
 * they run alternatively until the finish line
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */

public class RacingTrack implements IBroker_RacingTrack, IHorse_RacingTrack, RacingTrackInterface {
    
    private final ReentrantLock rl;
    private final Condition broker_condition;
    private final Condition horse_condition;
    private final Condition horses_can_move;
    
    private final int length;
    
    private static boolean race_finished = false; // flag indicating if the race has finished
    private boolean race_started = false; // flag indicating if the race has started

    public volatile static boolean all_horses_finished = false;

    private int race_iteration = 0;
    private int move_iteration = 0;	
    private int finished_horses = 0;
    
    private ArrayList<Integer> positions = new ArrayList<Integer>(Collections.nCopies(NUM_HORSES,0));

    // FIFO used to establish the order of the running horses
    private Queue<Integer> FIFO_horses = new LinkedList<Integer>();
    
    private final GeneralRepositoryInterface generalRepositoryInterface;
    String rmiRegHostName;
    int rmiRegPortNumb;
    
    public RacingTrack(GeneralRepositoryInterface generalRepositoryInterface, String rmiRegHostName, int rmiRegPortNumb)
    {
        rl = new ReentrantLock();
        broker_condition = rl.newCondition();
        horse_condition = rl.newCondition();
        horses_can_move = rl.newCondition();
        this.length = RACING_TRACK_LENGTH;
        this.generalRepositoryInterface = generalRepositoryInterface;
        this.rmiRegHostName = rmiRegHostName;
        this.rmiRegPortNumb = rmiRegPortNumb;
    }
    
    @Override
    public void startTheRace() 
    {
    	// broker starts the race and blocks until the race has finished
    	
        rl.lock();
        try {
            //System.out.println("START THE RACE : ");
            race_started = true;
            horse_condition.signal(); // signal a horse to start running
            try {
                while(!race_finished) { // broker is blokecd until the end of the race
                    broker_condition.await();
                }
            }catch(Exception ex) { }
            race_started = false; // reset
            race_finished = false; // reset
            incRaceIteration();
        } 
        finally {
            rl.unlock();
        }
    }	

    @Override
    public void makeAMove(int horse_id, int move_unit) 
    {
    	// horses start running until the finished line has been crossed. The horse makes a move, blocks and wakes the next horse so he can run
    	
        rl.lock();
        System.out.println("BEGINNING MAKE A MOVE, HORSE: " +horse_id);
        try {

            try {
                while(!race_started) {
                    //System.out.println("HORSE WAIT RACE START "+horse_id);
                    horse_condition.await(); // horses at the start line blocked until broker starts the race
                    horse_condition.signal(); 
                }
            }catch(Exception ex){ }
            
            //System.out.println("makeAMove() horse: " + horse_id + " move_iteration: " + move_iteration + " race_iteration " + race_iteration );
            
            try {

                if(move_iteration == (NUM_HORSES-finished_horses)){
                    race_iteration++;
                    move_iteration = 0; // reset   

                    for(int i = 0; i<FIFO_horses.size();i++)
                    {
                    	FIFO_horses.remove(); 
                    } // empty fifo
                    
                    horses_can_move.signal();
                }
                FIFO_horses.add(horse_id); // add the horse to the fifo, so it can run
                
                move_iteration++;
                
                positions.set(horse_id, positions.get(horse_id) + move_unit);  // new position of the horse
                
                while(FIFO_horses.contains(horse_id) && FIFO_horses.size() != NUM_HORSES-finished_horses){
                    horses_can_move.signal();
                    horses_can_move.await();
                }
                
                FIFO_horses.remove();
                
            }catch(Exception ex){ }
        } finally {
            System.out.println("END MAKE A MOVE, HORSE: " +horse_id);
            rl.unlock();
        }	
    }

    @Override
    public boolean hasFinishLineBeenCrossed(int horse_id) 
    {
        // horses make a move until they cross over the finishing line
    	
        rl.lock();
        boolean has_finished = false;
        try {
            if(positions.get(horse_id) > length) { // crossed finish line

                //System.out.println("CROSSED FINISH LINE "+horse_id);
                
                finished_horses++;
                
                int[] result = { horse_id, race_iteration}; 
                
                System.out.println("addHorseResult(" + result +")");
                //general_repository.addHorseResult(result); // add the result of the horse to the queue

                has_finished = true; // chenges the value of the flag
                
                positions.set(horse_id, 0); // reset 
                
                if(finished_horses==NUM_HORSES) {   // check if all horses finished
                    //System.out.println("ALL HORSES FINISHED");
                    race_finished = true;// changes the value of the flag
                    FIFO_horses.clear(); // reset
                    move_iteration = 0; // reset
                    race_iteration = 0; // reset 
                    finished_horses = 0; // reset
                    broker_condition.signal(); // signal the broker all horses finished
                }
            }
           
            horses_can_move.signal();
        } finally {
            rl.unlock();	
        }
        return has_finished;
    }
    
    @Override
    public void signalShutdown() {
        Register reg = null;
        Registry registry = null;
        
        /*
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
        String nameEntryObject = RegistryConfig.RMI_REGISTRY_RACINGTRACK_NAME;
        
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
            System.out.println("Racing Track registration exception: " + e.getMessage());
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("Racing Track not bound exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        // Unexport; this will also remove us from the RMI runtime
        try {
            UnicastRemoteObject.unexportObject(this, true);
        } catch (NoSuchObjectException ex) {
            System.exit(1);
        }

        System.out.println("Racing Track closed.");
    }

    private void incRaceIteration() {
        try {
            generalRepositoryInterface.incRaceIteration();
        }
        catch(RemoteException e) {
            System.err.println("Excepção na invocação remota de método incRaceIteration()" + e.getMessage() + "!");
            System.exit(1);
        }
    }
}
