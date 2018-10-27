// package AfternoonRacesDistributed.Monitors.ControlCenter;

import AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelServer;
import static AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelPorts.NAME_CONTROL_CENTER;
import static AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelPorts.PORT_CONTROL_CENTER;
import AfternoonRacesDistributed.Monitors.ControlCenter.*;

/**
 * Server running the ControlCenter monitor
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class ControlCenterServer {
    
    public static void main(String[] args) {
        
        ControlCenter center = new ControlCenter();
        ControlCenterInterface centerInterface = new ControlCenterInterface(center);
        CommunicationChannelServer socketCommunication, socketListening;
        ControlCenterClientProxy clientProxy;
        
        // generate server listening channel 
        socketListening = new CommunicationChannelServer(PORT_CONTROL_CENTER);
        socketListening.start();
        
        System.out.println("ControlCenter server online and listening!");
           
        /* process requests until END message type is received and processed by the server */
        while (true) {
            try{
                socketCommunication = socketListening.accept();
                clientProxy = new ControlCenterClientProxy(socketCommunication, centerInterface);
                
                /* handle exception thrown by proxy thread upon END message processing */
                Thread.UncaughtExceptionHandler h = (Thread th, Throwable ex) -> {
                    System.out.println("Control Center server ended!");
                    System.exit(0);
                };
                
                clientProxy.setUncaughtExceptionHandler(h);
                clientProxy.start(); 
            }
            catch(Exception ex){
                break;
            }
        }

        System.out.println("Control Center server ended");
    }
    
}