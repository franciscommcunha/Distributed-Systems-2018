package AfternoonRacesDistributed.Messages.RepositoryMessage;

/**
 * Message exceptions referent to the RepositoryMessage class
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class RepositoryMessageException extends RuntimeException{
    
    /**
     * Message with the exception
     */
    private RepositoryMessage msg;

    /**
     * Instantiation of the repository message exception
     *
     * @param errorMessage text with the error message
     * @param msg message with the exception
     */
    public RepositoryMessageException(String errorMessage, RepositoryMessage msg) {
        super(errorMessage);
        this.msg = msg;
    }

    /**
     * Obtaining the repository message
     *
     * @return message with error
     */
    public RepositoryMessage getMessageValue() {
        return (msg);
    }
    
}