package AfternoonRacesDistributed.Monitors.Stable;

import AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelServer;
import AfternoonRacesDistributed.Messages.StableMessage.StableMessage;
import AfternoonRacesDistributed.Messages.StableMessage.StableMessageException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Proxy for clients communicating with the stable server. Each proxy is constituted by a 
 * communication channel in order to read objects received by the server and an interface
 * for message processing. After message is processed it is then written into the socket output 
 * stream.
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class StableClientProxy extends Thread{
        
    /**
     * Communication channel
     */
    private CommunicationChannelServer sc;
    
    /**
     * Interface to stable
     */
    private StableInterface stable_interface;
    
    /**
     * Stable interface definition
     * 
     * @param sc Communication channel
     * @param stable_interface Stable interface
     */
    public StableClientProxy(CommunicationChannelServer sc, StableInterface stable_interface) {
        this.sc = sc;
        this.stable_interface = stable_interface;
    }
    
    @Override
    public void run(){
        StableMessage input_msg;
        StableMessage output_msg = null;
        
        input_msg = (StableMessage) sc.readObject();
        
        try{
           output_msg = stable_interface.processRequest(input_msg);
        }catch(StableMessageException e){
            System.out.println("Failed to process request for " + input_msg);
            System.out.println(e.getMessageValue().toString());
            System.exit(1);
        }
        if(!this.stable_interface.getStatus()){
            sc.writeObject(output_msg);
            sc.close();
            throw new StableMessageException("END message received",output_msg);
        }else{
            sc.writeObject(output_msg);
            sc.close();   
        }
    }
    
}