package AfternoonRacesDistributed.Messages.ControlCenterMessage;
import java.io.Serializable;

/**
 * Message types the stable monitor interface can process
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class ControlCenterMessage implements Serializable {
    
    private int msgType = -1;
    
    private boolean msgState = false;
    
    /**
     * Message of goWatchTheRace
     */
    public static final int GO_WATCH_THE_RACE = 1;
    
    /**
     * Message of reportResults
     */
    public static final int REPORT_RESULTS = 2;
    
    /**
     * Message of areThereAnyWinners
     */
    public static final int ARE_THERE_ANY_WINNERS = 3;
    
    /**
     * Message of haveIwon
     */
    public static final int HAVE_I_WON = 4;
    
    /**
     * Message of entertainTheGuests
     */
    public static final int ENTERTAIN_THE_GUESTS = 5;
    
    /**
     * Message of relaxABit
     */
    public static final int RELAX_A_BIT = 6;
    
    /**
     * Message to signal the client that his request has been processed with success
     */
    public static final int OK = 9;
    
    /**
     * Message to terminate server activity
     */
    public static final int END = 0;
    
    private int id;
        
    private boolean haveWon;
    
    private boolean bool;
    
     /**
     * Constructor with the message type
     * @param msgType type of message received
     */
    public ControlCenterMessage(int msgType) {
        this.msgType = msgType;
    }
    
    /**
     * Constructor with the message type
     * @param msgType type of message received
     * @param id representing the spectatator id
     */
    public ControlCenterMessage(int msgType, int id) {
        this.msgType = msgType;
        this.id = id;
    }
    
    /**
     * Constructor with the message type
     * @param msgType type of message received
     * @param bool representing boolean value 
     */
    public ControlCenterMessage(int msgType, boolean bool) {
        this.msgType = msgType;
        this.bool = bool;
    }
    
    /**
     * Getter method returning the message type
     * @return integer value representing the message type
     */
    public int getMessageType(){
        return this.msgType;  
    }
    
    /**
     * Getter method returning the state of the message
     * @return boolean value signaling the state of the message
     */
    public boolean getMessageState(){
        return this.msgState;
    }
    
    /**
     * Getter method returning the integer  id
     * @return integer value representing the id
     */
    public int getId() {
        return this.id;
    }
    
    public boolean getBoolean() {
        return this.bool;
    }
  
}