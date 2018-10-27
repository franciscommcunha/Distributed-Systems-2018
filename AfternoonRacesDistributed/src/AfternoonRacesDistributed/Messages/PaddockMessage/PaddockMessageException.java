package AfternoonRacesDistributed.Messages.PaddockMessage;

/**
 * Message exceptions referent to the PaddockMessage class
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class PaddockMessageException extends RuntimeException{
    
    /**
     * Message with the exception
     */
    private PaddockMessage msg;

    /**
     * Instantiation of the Paddock message exception
     *
     * @param errorMessage text with the error message
     * @param msg message with the exception
     */
    public PaddockMessageException(String errorMessage, PaddockMessage msg) {
        super(errorMessage);
        this.msg = msg;
    }

    /**
     * Obtaining the Paddock message
     *
     * @return message with error
     */
    public PaddockMessage getMessageVal() {
        return (msg);
    }
    
}
