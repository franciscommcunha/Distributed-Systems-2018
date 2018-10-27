package AfternoonRacesDistributed.Monitors.ControlCenter;

import AfternoonRacesDistributed.Messages.ControlCenterMessage.ControlCenterMessage;
import AfternoonRacesDistributed.Messages.ControlCenterMessage.ControlCenterMessageException;

/**
 * Interface for the ControlCenter monitor
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class ControlCenterInterface {
    
    private ControlCenter controlCenter;
    private boolean status = true;

    /**
     * 
     * @param controlCenter ControlCenter reference
     */
    public ControlCenterInterface(ControlCenter controlCenter) {
        this.controlCenter = controlCenter;
    }
    
    /**
     * Check if server alive or can end
     * @return boolean value to determine server lifecycle
     */
    public boolean getStatus() {
        return this.status;
    }
    
    /**
     * Function that executes the operation specified by each received message
     *
     * @param inputMsg Message containing the request
     * @return ControlCenterMessage reply
     * @throws ControlCenterMessageException if the message contains an invalid request
     */
    public ControlCenterMessage processRequest(ControlCenterMessage inputMsg) throws ControlCenterMessageException  {
       
        ControlCenterMessage outputMsg = null;

        /*
         * Process the received message and generate response
         */
        switch (inputMsg.getMessageType()) {
            
            case ControlCenterMessage.GO_WATCH_THE_RACE:
                controlCenter.goWatchTheRace(inputMsg.getId());
                outputMsg = new ControlCenterMessage(ControlCenterMessage.OK);
                break;
                
            case ControlCenterMessage.REPORT_RESULTS:
                controlCenter.reportResults();
                outputMsg = new ControlCenterMessage(ControlCenterMessage.OK);
                break;
                
            case ControlCenterMessage.ARE_THERE_ANY_WINNERS:
                boolean haveWinners = controlCenter.areThereAnyWinners();
                outputMsg = new ControlCenterMessage(ControlCenterMessage.ARE_THERE_ANY_WINNERS, haveWinners);
                break;
                
            case ControlCenterMessage.HAVE_I_WON:
                boolean won = controlCenter.haveIwon(inputMsg.getId());
                outputMsg = new ControlCenterMessage(ControlCenterMessage.HAVE_I_WON, won);
                break;
                
            case ControlCenterMessage.ENTERTAIN_THE_GUESTS:
                controlCenter.entertainTheGuests();
                outputMsg = new ControlCenterMessage(ControlCenterMessage.OK);
                break;
                
            case ControlCenterMessage.RELAX_A_BIT:
                controlCenter.relaxABit(inputMsg.getId());
                outputMsg = new ControlCenterMessage(ControlCenterMessage.OK);
                break;    
            
            case ControlCenterMessage.END:
                System.out.println("END");
                this.status = false;
                outputMsg = new ControlCenterMessage(ControlCenterMessage.END);
                break;
               
            default:
                throw new ControlCenterMessageException("Received invalid message type!", inputMsg);
        }

        return outputMsg;
    }
    
}
