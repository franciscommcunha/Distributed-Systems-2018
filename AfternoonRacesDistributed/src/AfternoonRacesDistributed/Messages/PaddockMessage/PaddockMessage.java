package AfternoonRacesDistributed.Messages.PaddockMessage;

import java.io.Serializable;

/**
 * Message types the paddock monitor interface can process
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class PaddockMessage implements Serializable{
        
    private int msgType = -1;
    
    private boolean msgState = false;
        
    /*
     * Message of proceedToPaddock
     */
    public static final int PROCEED_TO_PADDOCK = 1;
    
    /*
     * Message of summonHorsesToPaddock
     */
    public static final int SUMMON_HORSES_TO_PADDOCK = 2;
    
    /*
     * Message of waitForNextRace
     */
    public static final int WAIT_FOR_NEXT_RACE = 3;
    
    /*
     * Message of goCheckHorses
     */
    public static final int GO_CHECK_HORSES = 4;
    
    /*
     * Message of proceedToStartLine
     */
    public static final int PROCEED_TO_START_LINE = 5;
    
    /*
     * Message to signal the client that his request has been processed with success
     */
    public static final int OK = 9;
    
    /*
     * Message to terminate server activity
     */
    public static final int END = 0;
    
    private int horse_id;
    private int spec_id;
    
    /**
     * Constructor with the message type
     * @param msg_type type of message received
     */
    public PaddockMessage(int msgType) {
        this.msgType = msgType;
    }
    
    public PaddockMessage(int msgType, boolean msgState){
        this.msgType = msgType;
        this.msgState = msgState;
    }
    
    public PaddockMessage(int msgType,int horse_id){
        this.msgType = msgType;
        this.horse_id = horse_id;
    }
    
    /*
    public PaddockMessage(int msgType,int spec_id){
        this.msgType = msgType;
        this.spec_id = spec_id;
    }
    */
    
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
     * Getter method returning the horse id
     * @return int value belonging to that horse
     */
    public int getHorseId(){
        return this.horse_id;
    }
    
    /**
     * Getter method returning the spec id
     * @return int value belonging to that spectator
     */
    public int getSpecId(){
        return this.spec_id;
    }
        
}
