//package AfternoonRacesDistributed.Monitors.BettingCenter;

import static AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelPorts.NAME_BETTING_CENTER;
import static AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelPorts.PORT_BETTING_CENTER;
import AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelServer;

import AfternoonRacesDistributed.Monitors.BettingCenter.*;

/**
 * Server running the BettingCenter monitor
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class BettingCenterServer {
    
    public static void main(String[] args){
        
        BettingCenter bettingCenter = new BettingCenter();
        BettingCenterInterface bettingCenterInterface = new BettingCenterInterface(bettingCenter);
        CommunicationChannelServer socketCommunication, socketListening;
        BettingCenterClientProxy clientProxy;
        
        socketListening = new CommunicationChannelServer(PORT_BETTING_CENTER);
        socketListening.start();
        
        System.out.println("Betting center server online and listening!");
        
        boolean end = false;
        
        /* process requests until END message type is received and processed by the server */
        while (!end) {
            try{
                socketCommunication = socketListening.accept();
                clientProxy = new BettingCenterClientProxy(socketCommunication, bettingCenterInterface);
                
                /* handle exception thrown by proxy thread upon END message processing */
                Thread.UncaughtExceptionHandler h = (Thread th, Throwable ex) -> {
                    System.out.println("Betting center server ended!");
                    System.exit(0);
                };
                
                clientProxy.setUncaughtExceptionHandler(h);
                clientProxy.start(); 
                
            }
            catch(Exception ex){
                if(!bettingCenterInterface.getStatus()){
                    end = true;
                }
            }
        }
        
        System.out.println("Betting center server ended");
        
    }
    
}
