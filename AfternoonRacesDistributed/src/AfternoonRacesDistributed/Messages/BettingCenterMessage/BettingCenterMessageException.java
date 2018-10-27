package AfternoonRacesDistributed.Messages.BettingCenterMessage;

/**
 * Message exceptions referent to the BettingCenterMessage class
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class BettingCenterMessageException extends RuntimeException{
    
    /**
     * Message with the exception
     */
    private BettingCenterMessage msg;

    /**
     * Instantiation of the BettingCenter message exception
     *
     * @param errorMessage text with the error message
     * @param msg message with the exception
     */
    public BettingCenterMessageException(String errorMessage, BettingCenterMessage msg) {
        super(errorMessage);
        this.msg = msg;
    }

    /**
     * Obtaining the BettingCenter message
     *
     * @return message with error
     */
    public BettingCenterMessage getMessageVal() {
        return (msg);
    } 
}