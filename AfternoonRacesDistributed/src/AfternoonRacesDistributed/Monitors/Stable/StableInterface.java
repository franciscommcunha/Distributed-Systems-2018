package AfternoonRacesDistributed.Monitors.Stable;

import AfternoonRacesDistributed.Messages.StableMessage.StableMessage;
import AfternoonRacesDistributed.Messages.StableMessage.StableMessageException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Interface for the stable monitor
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class StableInterface{
    
    private Stable stable;
    private boolean status = true;

    /**
     * 
     * @param stable Stable reference
     */
    public StableInterface(Stable stable) {
        this.stable = stable;
    }
    
    /**
     * Check if server alive or can end
     * @return boolean value to determine server lifecycle
     */
    public boolean getStatus() {
        return this.status;
    }
    
    /**
     * Set status of server
     * @param status 
     */
    public void setStatus(boolean status) {
        this.status = status;
    }
    
    /**
     * Function that executes the operation specified by each received message
     *
     * @param input_msg Message containing the request
     * @return StableMessage reply to input_msg message request
     * @throws StableMessageException if the message contains an invalid request
     */
    public StableMessage processRequest(StableMessage input_msg) throws StableMessageException{

        StableMessage output_msg = null;

        /*
         * Processes the received message, input_msg, and generate response
         */
        switch (input_msg.getMessageType()) {       
            case StableMessage.PROCEED_TO_STABLE:
                System.out.println("PROCEED_TO_STABLE");
                stable.proceedToStable(input_msg.getHorseId(), input_msg.getRaceNumber());
                output_msg = new StableMessage(StableMessage.OK);
                break;
            case StableMessage.SUMMON_HORSES_TO_PADDOCK:
                System.out.println("SUMMON_HORSES_TO_PADDOCK");
                stable.summonHorsesToPaddock();
                output_msg = new StableMessage(StableMessage.OK);
                break;                
            case StableMessage.END:           
                System.out.println("END");
                this.status = false;
                output_msg = new StableMessage(StableMessage.END);
                break;                              
            default:
                throw new StableMessageException("Received invalid message type!", input_msg);
        }

        return output_msg;
    }
    
}
     
