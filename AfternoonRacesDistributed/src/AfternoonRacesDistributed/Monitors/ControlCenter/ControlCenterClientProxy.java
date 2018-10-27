package AfternoonRacesDistributed.Monitors.ControlCenter;

import AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelServer;
import AfternoonRacesDistributed.Messages.ControlCenterMessage.ControlCenterMessage;
import AfternoonRacesDistributed.Messages.ControlCenterMessage.ControlCenterMessageException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Proxy for clients communicating with the ControlCenter server
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class ControlCenterClientProxy extends Thread{
    
    /**
     * Communication channel
     */
    private CommunicationChannelServer sc;
    
    /**
     * Interface to control center
     */
    private ControlCenterInterface centerInterface; 
    
    /**
     * ControlCenter interface instantiation
     * 
     * @param sc Communication channel
     * @param centerInterface ControlCenter interface
     */
    public ControlCenterClientProxy(CommunicationChannelServer sc, ControlCenterInterface centerInterface) {
        this.sc = sc;
        this.centerInterface = centerInterface;
    }
    
    @Override
    public void run(){
        ControlCenterMessage inputMsg;
        ControlCenterMessage outputMsg = null; 
        
        inputMsg = (ControlCenterMessage) sc.readObject();
        
        try{
           outputMsg = centerInterface.processRequest(inputMsg); 
        }
        catch(ControlCenterMessageException e){
            System.out.println("Thread " + getName() + ": " + e.getMessage() + "!");
            System.out.println(e.getMessageVal().toString());
            System.out.println("Failed to process request : " + inputMsg);
            System.exit(1);
        }
        if(!this.centerInterface.getStatus()){
            sc.writeObject(outputMsg);
            sc.close();
            throw new ControlCenterMessageException("END message received",outputMsg);
        }else{
            sc.writeObject(outputMsg);
            sc.close();   
        }
    }
   
}