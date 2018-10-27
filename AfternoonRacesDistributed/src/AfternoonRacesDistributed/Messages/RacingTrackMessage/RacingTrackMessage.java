/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AfternoonRacesDistributed.Messages.RacingTrackMessage;

import java.io.Serializable;

/**
 * @author João Amaral
 * @author franciscommcunha
 */
public class RacingTrackMessage implements Serializable {
    
    private int msgType = -1;
    
    private boolean msgState = false;
    
    /*
     * Message of startTheRace
     */
    public static final int START_THE_RACE = 1;
    
    /*
     * Message of makeAMove
     */
    public static final int MAKE_A_MOVE = 2;
    
    /*
     * Message of hasFinishLineBeenCrossed
     */
    public static final int HAS_FINISHED_LINE_BEEN_CROSSED = 3;
    
    /*
     * Message to signal the client that his request has been processed with success
     */
    public static final int OK = 9;
    
    /*
     * Message to terminate server activity
     */
    public static final int END = 0;
    
    private int id;
    
    private int move_unit;
    
    private boolean hasFinished;
    
    public RacingTrackMessage(int msgType) {
        this.msgType = msgType;
    }
    
    public RacingTrackMessage(int msgType, boolean msgState) {
        this.msgType = msgType;
        this.msgState = msgState;
    }
    
    public RacingTrackMessage(int msgType, int horse_id) {
        this.msgType = msgType;
        this.id = horse_id;
    }

    public RacingTrackMessage(int msgType, int horse_id, int move_unit) {
        this.msgType = msgType;
        this.id = horse_id;
        this.move_unit = move_unit;
    }
    
    public RacingTrackMessage(int msgType, int horse_id, boolean hasFinished) {
        this.msgType = msgType;
        this.id = horse_id;
        this.hasFinished = hasFinished;
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
     * Getter method returning the id of the horse
     * @return int value signaling the id of the horse
     */
    public int getId(){
        return this.id;
    }
    
    /**
     * Getter method returning the move unit the horse ran
     * @return int value signaling move unit the horse ran
     */
    public int getMoveUnit(){
        return this.move_unit;
    }
    
    public boolean getHasFinished() {
        return hasFinished;
    }
}
