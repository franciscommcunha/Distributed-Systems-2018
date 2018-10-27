//package AfternoonRacesDistributed.Monitors.Paddock;

import static AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelPorts.NAME_PADDOCK;
import static AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelPorts.PORT_PADDOCK;
import AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelServer;

import AfternoonRacesDistributed.Monitors.Paddock.*;

/**
 * Server running the Paddock monitor
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class PaddockServer {
    
    public static void main(String[] args){
        
        Paddock paddock;
        PaddockInterface paddockInterface;
        CommunicationChannelServer socketCommunication, socketListening;
        PaddockClientProxy clientProxy;
        
        // generate server listening channel
        socketListening = new CommunicationChannelServer(PORT_PADDOCK);
        socketListening.start();
        
        paddock = new Paddock();
        paddockInterface = new PaddockInterface(paddock);
        
        System.out.println("Paddock server online and listening!");
     
        /* process requests until END message type is received and processed by the server */
        while (true) {
            try{
                socketCommunication = socketListening.accept();
                clientProxy = new PaddockClientProxy(socketCommunication, paddockInterface);

                /* handle exception thrown by proxy thread upon END message processing */
                Thread.UncaughtExceptionHandler h = (Thread th, Throwable ex) -> {
                    System.out.println("Paddock server ended!");
                    System.exit(0);
                };

                clientProxy.setUncaughtExceptionHandler(h);
                clientProxy.start();
            
            }catch(Exception ex){ 
                break;
            }
  
        }
        
        System.out.println("Paddock server ended");
    }
    
}
