package AfternoonRacesDistributed.Messages.ControlCenterMessage;

/**
 * Message exceptions referent to the ControlCenterMessage class
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class ControlCenterMessageException extends RuntimeException {
    
    /**
     * Message with the exception
     */
    private ControlCenterMessage msg;

    /**
     * Instantiation of the control center message exception
     *
     * @param errorMessage text with the error message
     * @param msg message with the exception
     */
    public ControlCenterMessageException(String errorMessage, ControlCenterMessage msg) {
        super(errorMessage);
        this.msg = msg;
    }

    /**
     * Obtaining the control center message
     *
     * @return message with error
     */
    public ControlCenterMessage getMessageVal() {
        return (msg);
    }
    
}
