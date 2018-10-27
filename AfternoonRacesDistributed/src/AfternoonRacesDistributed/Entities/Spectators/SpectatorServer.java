//package AfternoonRacesDistributed.Entities.Spectators;

import AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelClient;
import static AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelPorts.*;
import static AfternoonRacesDistributed.Constants.Constants.NUM_SPECTATORS;
import static AfternoonRacesDistributed.Constants.Constants.SPECTATOR_INIT_MONEY;
import AfternoonRacesDistributed.Entities.Spectators.SpectatorsInterfaces.ISpectator_BettingCenter;
import AfternoonRacesDistributed.Entities.Spectators.SpectatorsInterfaces.ISpectator_ControlCenter;
import AfternoonRacesDistributed.Entities.Spectators.SpectatorsInterfaces.ISpectator_Paddock;
import AfternoonRacesDistributed.Monitors.GeneralRepository.GeneralRepository;

import AfternoonRacesDistributed.Entities.Spectators.*;

/**
 * Server implementation of the Spectators entities
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class SpectatorServer {
    
    public static void main(String[] args){
                
        Spectator[] spectators = new Spectator[NUM_SPECTATORS];
        
        // spectators instantiation and lauch
        for(int i=0 ; i < NUM_SPECTATORS ; i++) {
            spectators[i] = spectators[i] = new Spectator(i, SPECTATOR_INIT_MONEY);
            spectators[i].start();
        }
        
        // wait for finish of threads
        for(int i=0 ; i<NUM_SPECTATORS ; i++) {
            try{
                spectators[i].join();
            }catch(InterruptedException ex){
                System.out.println("Spectator " + i + " execution has finished!");
            }
        }
    }
    
}
