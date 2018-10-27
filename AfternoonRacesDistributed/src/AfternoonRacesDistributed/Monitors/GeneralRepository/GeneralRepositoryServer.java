//package AfternoonRacesDistributed.Monitors.GeneralRepository;

import static AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelPorts.PORT_GENERAL_REPOSITORY;
import AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelServer;
import AfternoonRacesDistributed.Monitors.GeneralRepository.*;

/**
 * Server class for general repository
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class GeneralRepositoryServer {
        
    public static void main(String[] args){
        
        GeneralRepository general_repository;
        GeneralRepositoryInterface rep_interface;
        CommunicationChannelServer socket_communication, socket_listening;
        GeneralRepositoryClientProxy client_proxy;

        // generate server listening channel 
        socket_listening = new CommunicationChannelServer(PORT_GENERAL_REPOSITORY);
        socket_listening.start();
        
        general_repository = new GeneralRepository();
        rep_interface = new GeneralRepositoryInterface(general_repository);
        
        System.out.println("Repository server online and listening!");
      
        /* process requests until END message type is received and processed by the server */
        while (true) {
            try{
                socket_communication = socket_listening.accept();
                client_proxy = new GeneralRepositoryClientProxy(socket_communication, rep_interface);
                
                /* handle exception thrown by proxy thread upon END message processing */
                Thread.UncaughtExceptionHandler h = (Thread th, Throwable ex) -> {
                    System.out.println("Repository server ended!");
                    System.exit(0);
                };
                
                client_proxy.setUncaughtExceptionHandler(h);
                client_proxy.start(); 
                
            }catch(Exception ex){
                break;
            }
        }

        System.out.println("Repository server ended");
    }
}
