//package AfternoonRacesDistributed.Entities.Horses;

import AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelClient;
import static AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelPorts.*;
import static AfternoonRacesDistributed.Constants.Constants.HORSE_MAX_AGILITY;
import static AfternoonRacesDistributed.Constants.Constants.HORSE_MIN_AGILITY;
import static AfternoonRacesDistributed.Constants.Constants.NUM_HORSES;
import AfternoonRacesDistributed.Entities.Horses.HorsesInterfaces.IHorse_Paddock;
import AfternoonRacesDistributed.Entities.Horses.HorsesInterfaces.IHorse_RacingTrack;
import AfternoonRacesDistributed.Entities.Horses.HorsesInterfaces.IHorse_Stable;
import AfternoonRacesDistributed.Monitors.GeneralRepository.GeneralRepository;
import java.util.concurrent.ThreadLocalRandom;

import AfternoonRacesDistributed.Entities.Horses.*;

/**
 * Server implementation of the Horses entities 
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class HorseServer {
    
    public static void main(String[] args){
        
        Horse[] horses = new Horse[NUM_HORSES];
       
        // horses instantiation and lauch
        for(int i=0 ; i < NUM_HORSES ; i++) {
            horses[i] = new Horse(i,ThreadLocalRandom.current().nextInt(HORSE_MIN_AGILITY, HORSE_MAX_AGILITY+1));
            horses[i].start();
        }
        
        // wait for finish of threads
        for(int i=0 ; i < NUM_HORSES ; i++) {
            try{
                horses[i].join();
            }catch(InterruptedException ex){
                System.out.println("Horse " + i + " execution has finished!");
            }
        }
        
    }
}
