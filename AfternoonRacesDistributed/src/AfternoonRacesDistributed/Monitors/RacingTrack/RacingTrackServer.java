//package AfternoonRacesDistributed.Monitors.RacingTrack;

import static AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelPorts.NAME_RACING_TRACK;
import static AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelPorts.PORT_RACING_TRACK;
import AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelServer;
import AfternoonRacesDistributed.Monitors.RacingTrack.*;

/**
 * Server running the racing track monitor
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class RacingTrackServer {
    
    public static void main(String[] args){
        
        RacingTrack racingTrack;
        RacingTrackInterface racingTrackInterface;
        CommunicationChannelServer socketCommunication, socketListening;
        RacingTrackClientProxy clientProxy;
        
        // generate server listening channel
        socketListening = new CommunicationChannelServer(PORT_RACING_TRACK);
        socketListening.start();
        
        racingTrack = new RacingTrack();
        racingTrackInterface = new RacingTrackInterface(racingTrack);
        
        System.out.println("RacingTrack server online and listening!");

        /* process requests until END message type is received and processed by the server */
        while (true) {
            try{
                socketCommunication = socketListening.accept();
                clientProxy = new RacingTrackClientProxy(socketCommunication, racingTrackInterface);
                
                /* handle exception thrown by proxy thread upon END message processing */
                Thread.UncaughtExceptionHandler h = (Thread th, Throwable ex) -> {
                    System.out.println("RacingTrack server ended");
                    System.exit(0);
                };
                
                clientProxy.setUncaughtExceptionHandler(h);
                clientProxy.start(); 
            }
            catch(Exception ex){
                break;
            }
        }
        
        System.out.println("RacingTrack server ended");
        
    }
    
}