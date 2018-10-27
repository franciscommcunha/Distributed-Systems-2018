package AfternoonRacesDistributed.Messages.StableMessage;

/**
 * Message exceptions referent to the StableMessage class
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class StableMessageException extends RuntimeException{
    
    /**
     * Message with the exception
     */
    private StableMessage msg;

    /**
     * Instantiation of the stable message exception
     *
     * @param errorMessage text with the error message
     * @param msg message with the exception
     */
    public StableMessageException(String errorMessage, StableMessage msg) {
        super(errorMessage);
        this.msg = msg;
    }

    /**
     * Obtaining the stable message
     *
     * @return message with error
     */
    public StableMessage getMessageValue() {
        return (msg);
    }
    
}
