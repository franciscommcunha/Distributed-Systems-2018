package AfternoonRacesConcurrent.Monitors;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static AfternoonRacesConcurrent.Constants.Constants.*; 
import static AfternoonRacesConcurrent.Entities.Horses.HorsesEnum.HorseStates.*;
import AfternoonRacesConcurrent.Entities.Horses.HorsesInterfaces.IHorse_Stable;
import AfternoonRacesConcurrent.Entities.Broker.BrokerInterfaces.IBroker_Stable;
import AfternoonRacesConcurrent.Monitors.GeneralRepository;


public class Stable implements IBroker_Stable, IHorse_Stable {
	
	private final ReentrantLock rl;
	
	private final Condition broker_condition;
	private final Condition horse_condition;
	
	private int stable_horses;
		
	public Stable()
	{
		rl = new ReentrantLock(true);
		broker_condition = rl.newCondition();
		horse_condition = rl.newCondition();
	}
		
	@Override
	public void proceedToStable(int horse_id, GeneralRepository general_repository) 
	{	
		/*
		 * When everything starts, the horses are assumed to be AT_THE_FIELDS, so the broker must send them to the stable.
		 * After that, broker is waken up in oder to summonHorsesToPaddock
		*/
		
        // AT_THE_FIELDS
        rl.lock();
       
        try {
            stable_horses++;
            
            if(stable_horses == NUM_HORSES) broker_condition.signal(); // signal broker in case all horses have reached the stable 
            
            while(!general_repository.getGoToPaddock()) { // while horses are not at the paddock
                try {
                    horse_condition.await();
                }catch(Exception ex) { }
            }
            stable_horses--;	// reset 
            if(stable_horses == 0) // avoid deadlock
                general_repository.setGoToPaddock(false);

            general_repository.setHorseState(AT_THE_STABLE, horse_id);
            general_repository.writeHorseSt(AT_THE_STABLE);
            
        } finally {
            rl.unlock();
        }
        // AT_THE_STABLE
	}

	@Override
	public void summonHorsesToPaddock(GeneralRepository general_repository) 
	{
		/*
		 * broker next state is ANOUNCING_NEXT_RACE and he will be blocked until the last spectator reaches the paddock
		 */
		
        // OPENING_THE_EVENT
        rl.lock();
        System.out.println("summonHorsesToPaddock() - STABLE");
        try {
            //System.out.println("ANNOUNCING_NEXT_RACE, BROKER");
            try {
                while(stable_horses < NUM_HORSES) {
                    broker_condition.await(); // broker waits until all horses are in the stable
                }
            }catch(Exception e) { }
            general_repository.setGoToPaddock(true);
            horse_condition.signalAll(); // wake up all horses to send them to the paddock => proceedToPaddock
            
        } finally {
            rl.unlock();
        }
       // OPENING_THE_EVENT					
	}
}