package AfternoonRacesDistributed.Messages.RacingTrackMessage;

/**
 * Message exceptions referent to the RacingTrackmessage class
 * 
 * @author João Amaral
 * @author franciscommcunha
 */
public class RacingTrackMessageException extends RuntimeException{
    
    /*
     * Message with the exception
     */
    private RacingTrackMessage msg;

    /**
     * Instantiation of the racing track exception message
     *
     * @param errorMessage text with the error message
     * @param msg message with the exception
     */
    public RacingTrackMessageException(String errorMessage, RacingTrackMessage msg) {
        super(errorMessage);
        this.msg = msg;
    }

    /**
     * Obtaining the racing track message
     *
     * @return message with error
     */
    public RacingTrackMessage getMessageVal() {
        return (msg);
    }
    
}
