package AfternoonRacesDistributed.Monitors.GeneralRepository;

import AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelServer;
import AfternoonRacesDistributed.Messages.RepositoryMessage.RepositoryMessage;
import AfternoonRacesDistributed.Messages.RepositoryMessage.RepositoryMessageException;

/**
 * Proxy for clients communicating with the repository server. Each proxy is constituted by a 
 * communication channel in order to read objects received by the server and an interface
 * for message processing. After message is processed it is then written into the socket output 
 * stream.
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class GeneralRepositoryClientProxy extends Thread{
    
    /**
     * Communication channel
     */
    private CommunicationChannelServer sc;
    
    /**
     * Interface to repository
     */
    private GeneralRepositoryInterface rep_interface;
    
    /**
     * 
     * @param sc Communication channel
     * @param rep_interface repository interface
     */
    public GeneralRepositoryClientProxy(CommunicationChannelServer sc, GeneralRepositoryInterface rep_interface) {
        this.sc = sc;
        this.rep_interface = rep_interface;
    }
    
    @Override
    public void run(){
        RepositoryMessage input_msg = null;
        RepositoryMessage output_msg = null; 
        
        input_msg = (RepositoryMessage) sc.readObject();
        
        try{
           output_msg = rep_interface.processRequest(input_msg); 
        }
        catch(Exception e){
            System.out.println("Thread " + getName() + ": " + e.getMessage() + "!");
            System.out.println("Failed to process request : " + input_msg);
            System.exit(1);
        }
         if(!this.rep_interface.getStatus()){
            sc.writeObject(output_msg);
            sc.close();
            throw new RepositoryMessageException("END message received",output_msg);
        }else{
            sc.writeObject(output_msg);
            sc.close();   
        }
    }
}