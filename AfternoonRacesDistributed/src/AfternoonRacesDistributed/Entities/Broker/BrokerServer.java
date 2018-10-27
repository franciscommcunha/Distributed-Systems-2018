// package AfternoonRacesDistributed.Entities.Broker;

import AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelClient;
import static AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelPorts.*;

import AfternoonRacesDistributed.Entities.Broker.*;

/**
 * Server implementation of the Broker entitiy. Creates communication channels for every monitor 
 * it communicates to and starts the Broker thread. Stops when thread execution finishes
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class BrokerServer {
    
    public static void main(String[] args){
        
        CommunicationChannelClient cc_stable = new CommunicationChannelClient(NAME_STABLE, PORT_STABLE);
        CommunicationChannelClient cc_paddock =  new CommunicationChannelClient(NAME_PADDOCK, PORT_PADDOCK);
        CommunicationChannelClient cc_betting_center = new CommunicationChannelClient(NAME_BETTING_CENTER, PORT_BETTING_CENTER);
        CommunicationChannelClient cc_control_center = new CommunicationChannelClient(NAME_CONTROL_CENTER, PORT_CONTROL_CENTER);
        CommunicationChannelClient cc_racing_track = new CommunicationChannelClient(NAME_RACING_TRACK, PORT_RACING_TRACK);
        CommunicationChannelClient cc_repository = new CommunicationChannelClient(NAME_GENERAL_REPOSITORY, PORT_GENERAL_REPOSITORY);
   
        Broker broker = new Broker(cc_stable,cc_paddock, cc_betting_center, cc_control_center, cc_racing_track, cc_repository);
        
        broker.start();
       
        try {
            broker.join(); // wait until end of broker thread
        } catch (InterruptedException ex) { }
        
        System.out.println("Broker ended!");
        
    } 
    
}
