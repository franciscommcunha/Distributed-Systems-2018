package AfternoonRacesDistributed.Messages.BettingCenterMessage;
import java.io.Serializable;

/**
 * Message types the BettingCenter monitor interface can process
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class BettingCenterMessage implements Serializable {
    
    private int msgType = -1;
    
    private boolean msgState = false;
    
    /**
     * Message of acceptTheBets
     */
    public static final int ACCEPT_THE_BETS = 1;
    
    /**
     * Message of placeABet
     */
    public static final int PLACE_A_BET = 2;
    
    /**
     * Message of honourTheBets
     */
    public static final int HONOUR_THE_BETS = 3;
    
    /**
     * Message of goCollectTheGains
     */
    public static final int GO_COLLECT_THE_GAINS = 4;
    
    /**
     * Message to signal the client that his request has been processed with success
     */
    public static final int OK = 9;
    
    /**
     * Message to terminate server activity
     */
    public static final int END = 0;
    
    private int horse_id;
    private int spec_id;
    private int money;
    
    /**
     * Constructor with the message type
     * @param msgType type of message received
     */
    public BettingCenterMessage(int msgType) {
        this.msgType = msgType;
    }
    
    public BettingCenterMessage(int msgType, boolean msgState) {
        this.msgType = msgType;
        this.msgState = msgState;
    }
    
    public BettingCenterMessage(int msgType, int spec_id) {
        this.msgType = msgType;
        this.spec_id = spec_id;
    }
    
    public BettingCenterMessage(int msgType, int spec_id, int horse_id, int money) {
        this.msgType = msgType;
        this.spec_id = spec_id;
        this.horse_id = horse_id;
        this.money = money;
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
     * Getter method returning the spectator id 
     * @return int value signaling the spectator id 
     */
    public int getSpecId(){
        return this.spec_id;
    }
    
    /**
     * Getter method returning the horse id 
     * @return int value signaling the horse id 
     */
    public int getHorseId(){
        return this.horse_id;
    }
    
    public int getMoney() {
        return this.money;
    }
    
    public int getGains() {
        return this.money;
    }
}
