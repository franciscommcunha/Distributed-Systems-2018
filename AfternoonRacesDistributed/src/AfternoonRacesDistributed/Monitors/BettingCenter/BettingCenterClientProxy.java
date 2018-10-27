package AfternoonRacesDistributed.Monitors.BettingCenter;

import AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelServer;
import AfternoonRacesDistributed.Messages.BettingCenterMessage.BettingCenterMessage;
import AfternoonRacesDistributed.Messages.BettingCenterMessage.BettingCenterMessageException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Proxy for clients communicating with the BettingCenter server
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class BettingCenterClientProxy extends Thread{
            
    /**
     * Communication channel
     */
    private CommunicationChannelServer sc;
    
    /**
     * Interface to Betting Center
     */
    private BettingCenterInterface bettingCenterInterface;
    
    /**
     * BettingCenter interface instantiation
     * 
     * @param sc Communication channel
     * @param bettingCenterInterface BettingCenter interface
     */
    public BettingCenterClientProxy(CommunicationChannelServer sc, BettingCenterInterface bettingCenterInterface) {
        this.sc = sc;
        this.bettingCenterInterface = bettingCenterInterface;

    }
    
    /**
     * Life cycle
     */
    @Override
    public void run(){
        BettingCenterMessage inputMsg;
        BettingCenterMessage outputMsg = null; 
        
        inputMsg = (BettingCenterMessage) sc.readObject();
        
        try{
           outputMsg = bettingCenterInterface.processRequest(inputMsg); 
        }
        catch(BettingCenterMessageException e){
            System.out.println("Thread " + getName() + ": " + e.getMessage() + "!");
            System.out.println(e.getMessageVal().toString());
            System.out.println("Failed to process request : " + inputMsg);
            System.exit(1);
        }
        if(!this.bettingCenterInterface.getStatus()){
            sc.writeObject(outputMsg);
            sc.close();
            throw new BettingCenterMessageException("END message received",outputMsg);
        }else{
            sc.writeObject(outputMsg);
            sc.close();   
        }
        
    }
 
}