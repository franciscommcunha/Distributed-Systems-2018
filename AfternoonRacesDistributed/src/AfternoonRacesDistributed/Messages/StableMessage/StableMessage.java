package AfternoonRacesDistributed.Messages.StableMessage;

import java.io.Serializable;

/**
 * Message types the stable monitor interface can process
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class StableMessage implements Serializable{
    
    /**
     * Message to retrieve the number of horses at the stable
     */
    public static final int GET_STABLE_HORSES = 1;
    
    /**
     * Message of proceedToStable
     */
    public static final int PROCEED_TO_STABLE = 2;
    
    /**
     * Message of summonHorsesToPaddock
     */
    public static final int SUMMON_HORSES_TO_PADDOCK = 3;
    
    /**
     * Message to signal the client that his request has been processed with success
     */
    public static final int OK = 9;
    
    /**
     * Message to terminate server activity
     */
    public static final int END = 0;
    
    /**
     * Integar value identifying the type of the stable message
     */
    private int msg_type = -1;
    
    /**
     * Id of the horse
     */
    private int horse_id;
    
    /**
     * Number of the race
     */
    private int race_number;
    
    /**
     * Constructor with the message type
     * @param msg_type type of message received
     */
    public StableMessage(int msg_type) {
        this.msg_type = msg_type;
    }
    
    /**
     * Constructor with the message type, id of the horse and number of the race
     * @param msg_type type of the message
     * @param horse_id id of the horse
     * @param race_number number of the race
     */
    public StableMessage(int msg_type, int horse_id, int race_number) {
        this.msg_type = msg_type;
        this.horse_id = horse_id;
        this.race_number = race_number;
    }
    
    /**
     * Getter method returning the message type
     * @return integer value representing the message type
     */
    public int getMessageType(){
        return this.msg_type;  
    }
    
    /**
     * Retrieves the second argument of the StableMessage corresponding to the horse id
     * @return horse_id id of the horse
     */
    public int getHorseId(){
        return this.horse_id;
    }

    /**
     * Retrieves the third argument of the StableMessage corresponding to the number of the race
     * @return horse_id id of the horse
     */
    public int getRaceNumber(){
        return this.race_number;
    }
        
}
