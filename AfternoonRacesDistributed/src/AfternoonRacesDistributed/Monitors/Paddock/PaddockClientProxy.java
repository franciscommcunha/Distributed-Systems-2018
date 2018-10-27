package AfternoonRacesDistributed.Monitors.Paddock;

import AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelServer;
import AfternoonRacesDistributed.Messages.PaddockMessage.PaddockMessage;
import AfternoonRacesDistributed.Messages.PaddockMessage.PaddockMessageException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Proxy for clients communicating with the paddock server
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class PaddockClientProxy extends Thread{
    
    /*
     * Communication channel
     */
    private CommunicationChannelServer sc;
    
    /*
     * Interface to Paddock
     */
    private PaddockInterface paddockInterface;
    
    /*
     * Paddock client proxy constructor
     * 
     * @param sc Communication channel
     * @param paddockInterface Paddock interface
     */
    public PaddockClientProxy(CommunicationChannelServer sc, PaddockInterface paddockInterface) {
        this.sc = sc;
        this.paddockInterface = paddockInterface;
    }
    
    @Override
    public void run() {
        PaddockMessage inputMsg;
        PaddockMessage outputMsg = null;
        
        inputMsg = (PaddockMessage) sc.readObject();
        
        try {
            outputMsg = paddockInterface.processRequest(inputMsg);
        }
        catch (PaddockMessageException exc) {
            System.out.println("Failed to process request : " + inputMsg);
            System.exit(1);
        }
        if(!this.paddockInterface.getStatus()){
            sc.writeObject(outputMsg);
            sc.close();
            throw new PaddockMessageException("END message received",outputMsg);
        }else{
            sc.writeObject(outputMsg);
            sc.close();   
        }
        
    }
    
}
