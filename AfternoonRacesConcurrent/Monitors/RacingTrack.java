package AfternoonRacesConcurrent.Monitors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import AfternoonRacesConcurrent.Constants.Constants;
import AfternoonRacesConcurrent.Entities.Broker.BrokerInterfaces.IBroker_RacingTrack;
import AfternoonRacesConcurrent.Entities.Horses.HorsesInterfaces.IHorse_RacingTrack;
import static AfternoonRacesConcurrent.Constants.Constants.*;

public class RacingTrack implements IBroker_RacingTrack, IHorse_RacingTrack{
    
    private final ReentrantLock rl;
    private final Condition broker_condition;
    private final Condition horse_condition;
    private final Condition horses_can_move;

    GeneralRepository general_repository;
    
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

    public RacingTrack()
    {
            rl = new ReentrantLock();
            broker_condition = rl.newCondition();
            horse_condition = rl.newCondition();
	
            horses_can_move = rl.newCondition();
            
            this. general_repository =  general_repository;
            this.length = RACING_TRACK_LENGTH;
    }

    @Override
    public void startTheRace(GeneralRepository general_repository) 
    {
    		// broker starts the race and blocks until the race has finished
    	
        rl.lock();
        try {
            System.out.println("START THE RACE : ");
            race_started = true;
            horse_condition.signal(); // signal a horse to start running
            try {
                while(!race_finished) { // broker is blokecd until the end of the race
                    broker_condition.await();
                }
            }catch(Exception ex) { }
            race_started = false; // reset
            race_finished = false; // reset
            general_repository.incRaceIteration();
            System.out.println("RACE FINISHED");
        } finally {
            rl.unlock();
        }
    }	

    @Override
    public void makeAMove(int horse_id, int move_unit, GeneralRepository general_repository) 
    {
    		// horses start running until the finished line has been crossed. The horse makes a move, blocks and wakes the next horse so he can run
    	
        rl.lock();
        try {

            try {
                while(!race_started) {
                    System.out.println("HORSE WAIT RACE START "+horse_id);
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
            rl.unlock();
        }	
    }

    @Override
    public boolean hasFinishLineBeenCrossed(int horse_id, GeneralRepository general_repository) 
    {
    		// horses make a move until they cross over the finishing line
    	
        rl.lock();
        boolean has_finished = false;
        try {
            if(positions.get(horse_id) > length) { // crossed finish line

                //System.out.println("CROSSED FINISH LINE "+horse_id);
                
                finished_horses++;
                
                int[] result = { horse_id, race_iteration};
                general_repository.addHorseResult(result); // add the result of the horse to the queue

                has_finished = true; // chenges the value of the flag
                
                positions.set(horse_id, 0); // reset 
                
                if(finished_horses==NUM_HORSES) {   // check if all horses finished
                    System.out.println("ALL HORSES FINISHED");
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
}
