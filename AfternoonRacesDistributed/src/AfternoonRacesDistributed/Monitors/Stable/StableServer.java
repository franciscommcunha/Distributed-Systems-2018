//package AfternoonRacesDistributed.Monitors.Stable;

import static AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelPorts.PORT_STABLE;
import AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelServer;
import static AfternoonRacesDistributed.Constants.Constants.NUM_HORSES;
import static AfternoonRacesDistributed.Constants.Constants.NUM_RACES;

import AfternoonRacesDistributed.Monitors.Stable.*;

/**
 * Server running the stable monitor
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class StableServer {
    
    public static void main(String[] args){
        
        Stable stable;
        StableInterface stable_interface;
        CommunicationChannelServer socket_communication, socket_listening;
        StableClientProxy client_proxy;

        // generate server listening channel 
        socket_listening = new CommunicationChannelServer(PORT_STABLE);
        socket_listening.start();
        
        stable = new Stable();
        stable_interface = new StableInterface(stable);
        
        System.out.println("Stable server online and listening!");
        
        /* process requests until END message type is received and processed by the server */
        while (true) {
            try{
                socket_communication = socket_listening.accept();
                client_proxy = new StableClientProxy(socket_communication, stable_interface);
                
                /* handle exception thrown by proxy thread upon END message processing */
                Thread.UncaughtExceptionHandler h = (Thread th, Throwable ex) -> {
                    System.out.println("Stable server ended!");
                    System.exit(0);
                };
                
                client_proxy.setUncaughtExceptionHandler(h);
                client_proxy.start(); 
            }catch(Exception ex){
                break;
            }
        }
        System.out.println("Stable server ended");
    }
        
}
