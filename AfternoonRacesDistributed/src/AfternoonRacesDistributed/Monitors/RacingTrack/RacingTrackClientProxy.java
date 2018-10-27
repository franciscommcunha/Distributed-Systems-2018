package AfternoonRacesDistributed.Monitors.RacingTrack;

import AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelServer;
import AfternoonRacesDistributed.Messages.RacingTrackMessage.RacingTrackMessage;
import AfternoonRacesDistributed.Messages.RacingTrackMessage.RacingTrackMessageException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Proxy for clients communicating with the stable server
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class RacingTrackClientProxy extends Thread{
            
    /*
     * Communication channel
     */
    private CommunicationChannelServer sc;
    
    /*
     * Interface to stable
     */
    private RacingTrackInterface racingTrackInterface;
    
    /**
     * Racing track monitor proxy instantiation
     * 
     * @param sc Communication channel
     * @param racingTrackInterface Racing track interface
     */
    public RacingTrackClientProxy(CommunicationChannelServer sc, RacingTrackInterface racingTrackInterface) {
        this.sc = sc;
        this.racingTrackInterface = racingTrackInterface;
    }
    
    @Override
    public void run(){
        RacingTrackMessage inputMsg;
        RacingTrackMessage outputMsg = null; 
        
        inputMsg = (RacingTrackMessage) sc.readObject();
        
        try{
           outputMsg = racingTrackInterface.processRequest(inputMsg); 
        }
        catch(RacingTrackMessageException exc){
            System.out.println("Failed to process request : " + inputMsg);
            System.exit(1);
        }
        
        if(!this.racingTrackInterface.getStatus()){
            sc.writeObject(outputMsg);
            sc.close();
            throw new RacingTrackMessageException("END message received",outputMsg);
        }else{
            sc.writeObject(outputMsg);
            sc.close();   
        }
        
    }
    
}