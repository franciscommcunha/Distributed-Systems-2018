package AfternoonRacesDistributed.Monitors.Paddock;

import AfternoonRacesDistributed.Messages.PaddockMessage.PaddockMessage;
import AfternoonRacesDistributed.Messages.PaddockMessage.PaddockMessageException;

/**
 * Interface for the Paddock monitor
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class PaddockInterface {
    
    private Paddock paddock;
    private boolean status = true;
            
    /** 
     * @param paddock Paddock reference
     */
    public PaddockInterface(Paddock paddock) {
        this.paddock = paddock;
    }
    
    /**
     * Check if server alive or can end
     * @return boolean value to determine server lifecycle
     */
    public boolean getStatus() {
        return this.status;
    }
    
    /**
     * Method that executes the operation specified by each received message
     *
     * @param inputMsg Message containing the request
     * @return PaddockMessage reply
     * @throws PaddockMessageException if the message contains an invalid request
     */
    public PaddockMessage processRequest(PaddockMessage inputMsg) throws PaddockMessageException{
        
        PaddockMessage outputMsg = null;

        /*
         * Process the received message and generate response
         */
        switch (inputMsg.getMessageType()) {
            
            case PaddockMessage.SUMMON_HORSES_TO_PADDOCK:
                paddock.summonHorsesToPaddock();
                outputMsg = new PaddockMessage(PaddockMessage.OK);
                break;
            
            case PaddockMessage.PROCEED_TO_PADDOCK:
                paddock.proceedToPaddock(inputMsg.getHorseId());
                outputMsg = new PaddockMessage(PaddockMessage.OK);
                break;
                
            case PaddockMessage.WAIT_FOR_NEXT_RACE:
                paddock.waitForNextRace(inputMsg.getSpecId());
                outputMsg = new PaddockMessage(PaddockMessage.OK);
                break;
             
            case PaddockMessage.GO_CHECK_HORSES:
                paddock.goCheckHorses(inputMsg.getSpecId());
                outputMsg = new PaddockMessage(PaddockMessage.OK);
                break;
                
            case PaddockMessage.PROCEED_TO_START_LINE:
                paddock.proceedToStartLine(inputMsg.getHorseId());
                outputMsg = new PaddockMessage(PaddockMessage.OK);
                break;
                
            case PaddockMessage.END:
                System.out.println("END");
                this.status = false;
                outputMsg = new PaddockMessage(PaddockMessage.END);
                break;
               
            default:
                throw new PaddockMessageException("Received invalid message type!", inputMsg);
        }
        return outputMsg;
    }
}