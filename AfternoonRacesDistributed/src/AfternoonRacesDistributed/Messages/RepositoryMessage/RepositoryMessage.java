package AfternoonRacesDistributed.Messages.RepositoryMessage;

import AfternoonRacesDistributed.Entities.Broker.BrokerEnum.BrokerStates;
import AfternoonRacesDistributed.Entities.Horses.HorsesEnum.HorseStates;
import AfternoonRacesDistributed.Entities.Spectators.SpectatorsEnum.SpectatorStates;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * Message types the repository monitor interface can process
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */

public class RepositoryMessage implements Serializable{
    
    private int msg_type = -1;
    
    private boolean msg_state = false;
    
    /**
     * Message to retrieve the state of the broker
     */
    public static final int BROKER_STATE = 1;
    
    /**
     * Message to retrieve the state of the horse
     */
    public static final int HORSE_STATE = 2;
    
    /**
     * Message to retrieve the state of the spectator
     */
    public static final int SPECTATOR_STATE = 3;
    
    /**
     * Message to set the state of the broker
     */
    public static final int SET_BROKER_STATE = 4;
    
    /**
     * Message to set the state of the horse
     */
    public static final int SET_HORSE_STATE = 5;
    
    /**
     * Message to set the state of the spectator
     */
    public static final int SET_SPECTATOR_STATE = 6;
    
    /**
     * Message to retrieve if all horses have finished
     */
    public static final int GET_ALL_HORSES_FINISHED = 7;
    
    /**
     * Message to set the information of a bet
     */
    public static final int SET_BET_INFO = 20;
    
    /**
     * Message to get the information of a bet
     */
    public static final int GET_BET_INFO = 21;
    
    /**
     * Message to retrieve the size of the bet_info data structure
     */
    public static final int GET_BET_INFO_SIZE = 22; 
    
    /**
     * Message to clear the bet_info data structure
     */
    public static final int CLEAR_BET_INFO = 23; 
    
    /**
     * Message to filter zero value bets in bet_info data structure
     */
    public static final int FILTER_ZERO_AMOUNT_BET = 24;
    
    /**
     * Message to retrieve a reference copy of the bet_info data structure
     */
    public static final int GET_BET_INFO_COPY = 25;
    
    /**
     * Message to add a horse result
     */
    public static final int ADD_HORSE_RESULT = 26; 
    
    /**
     * Message to retrive the results of the horses
     */
    public static final int GET_HORSE_RESULT_SIZE = 27; 
    
    /**
     * Message to remove and retrieve a horse result
     */
    public static final int REMOVE_HORSE_RESULT = 28; 
    
    /**
     * Message to retrieve the top of the horse_result queue
     */
    public static final int PEEK_HORSE_RESULT = 29; 
    
    /**
     * Message to check if a horse won
     */
    public static final int HORSE_IS_WINNER = 30; 
    
     /**
     * Message to add a winning horse
     */
    public static final int ADD_HORSE_WINNER = 31; 
    
    /**
     * Message to clear winning horses
     */
    public static final int CLEAR_HORSE_WINNER = 32; 
    
    /**
     * Horse winner toString()
     */
    public static final int HORSE_WINNER_TOSTRING = 33; 
    
    /**
     * Message to add a winning horse
     */
    public static final int ADD_WINNING_SPECTATOR = 34; 
    
    /**
     * Message to retrieve the size of the winning_spectator data structure
     */
    public static final int GET_WINNING_SPECTATOR_SIZE = 35; 
    
    /**
     * Message to check if spectator won
     */
    public static final int SPECTATOR_IS_WINNER = 36;
    
    /**
     * Message to clear the winning_spectators data structure
     */
    public static final int CLEAR_WINNING_SPECTATOR = 37;
    
    /**
     * Message to increment the race iteration
     */
    public static final int INC_RACE_ITERATION = 38;
    
    /**
     * Message to initiate the log 
     */
    public static final int INIT_LOG = 50;
    
    /**
     * Message write broker state on logger
     */
    public static final int WRITE_STAT_B = 51;
    
    /**
     * Message write spectator state on logger
     */
    public static final int WRITE_SPEC_ST = 52;
    
    /**
     * Message write horse state on logger
     */
    public static final int WRITE_HORSE_ST = 53;
    
    /**
     * Message to start writting the logger
     */
    public static final int MAKE_LOGGER = 54;
    
    /**
     * Close the writter and finish the logger
     */
    public static final int CLOSE_WRITTER = 55;
    
    /**
     * Message to signal the client that his request has been processed with success
     */
    public static final int OK = 100;
    
    /**
     * Message to terminate server activity
     */
    public static final int END = 0;
    
    private BrokerStates broker_state;
    
    private HorseStates horse_state;
    
    private SpectatorStates spectator_state;
    
    private int id;
    
    
    /**
    * Queue containing the results of the race (horse id and last iteration)
    */
    private Queue<int[]> horse_results = new LinkedList<>();
    
    /**
     * ArrayList containing the id(s) of the horse winner(s) of the current race
     */
    private ArrayList<Integer> arr_list = new ArrayList<Integer>();
    
    
    /**
     * Hash map storing the information of a bet made by a spectator
     * Key -> Spectator Id
     * Value -> { Horse Id, Bet Amount }
     */
    private Map<Integer, int[]> bet_info = new HashMap<>();
    
    private int[] int_array;
    
    private boolean isWinner;
    
    /**
     * Constructor with the message type
     * @param msg_type type of message received
     */
    public RepositoryMessage(int msg_type) {
        this.msg_type = msg_type;
    }
    
     public RepositoryMessage(int msg_type, Map<Integer, int[]> bet_info) {
        this.msg_type = msg_type;
        this.bet_info = bet_info;
    }
    
    public RepositoryMessage(int msg_type, boolean msg_state){
        this.msg_type = msg_type;
        this.msg_state = msg_state;
    }

    /* response msg type for broker */
    public RepositoryMessage(int msg_type, BrokerStates state) {
        this.msg_type = msg_type;
        this.broker_state = state;
    }
    
    /* response msg type for horse */
    public RepositoryMessage(int msg_type, HorseStates state) {
        this.msg_type = msg_type;
        this.horse_state = state;
    }
        
    /* response msg type for spectator */
    public RepositoryMessage(int msg_type, SpectatorStates state) {
        this.msg_type = msg_type;
        this.spectator_state = state;
    }
    
    /* request msg type */
    public RepositoryMessage(int msg_type, int id) {
        this.msg_type = msg_type;
        this.id = id;
    }
    
    
    /* set state msg type */
    /* NULL POINTER EXCEPION NO IF
    public RepositoryMessage(int msg_type, Object state, int id){
        this.msg_type = msg_type;
        this.id = id;
        if(state instanceof HorseStates){
            this.horse_state = (HorseStates) state;
            System.out.println("HORSE STATE MESSAGE!!!");
        }else{
            this.spectator_state = (SpectatorStates) state;
            System.out.println("SPECTATOR STATE MESSAGE!!!");
        }
    }
    */
    
    /* set state horse msg type */
    public RepositoryMessage(int msg_type, HorseStates state, int id){
        this.msg_type = msg_type;
        this.id = id;
        this.horse_state = state;
    }
    
    /* set state spectator type */
    public RepositoryMessage(int msg_type, SpectatorStates state, int id){
        this.msg_type = msg_type;
        this.id = id;
        this.spectator_state = state;
    }
    
    /* used for SET BET INFO message */
    public RepositoryMessage (int msg_type, int id, int[] info){
        this.msg_type = msg_type;
        this.id = id;  
        this.int_array = info;
    }
    
    /* used for ADD_HORSE_RESULT message */
    public RepositoryMessage (int msg_type, int[] result){
        this.msg_type = msg_type;
        this.int_array = result;
    }
    
    /**
     * Getter method returning the message type
     * @return integer value representing the message type
     */
    public int getMessageType(){
        return this.msg_type;  
    }
    
    public BrokerStates getBrokerState(){
        return this.broker_state;
    }
    
    public HorseStates getHorseState(){
        return this.horse_state;
    }
        
    public SpectatorStates getSpectatorState(){
        return this.spectator_state;
    }
    
    public int getId(){
        return this.id;
    }
    
    /**
     * Adds to the queue the result
     */
    public void addHorseResult(int[] result){
       horse_results.add(result);
    }
    
    /**
     * Returns integer array component of the message
     */
    public int[] getArray(){
       return this.int_array; 
    }
    
    /**
     * Gets the size of the result queue 
     */
    public int getHorseResultSize() {
        return this.horse_results.size();
    }
    
    /**
     * Returns the peek of the queue
     */
    public int[] peekHorseResult(){
        return horse_results.peek(); 
    }   
    
    /**
     * 
     * @return boolean value
     */
    public boolean getBoolean(){
        return this.msg_state;
    }
    
    /**
     * @param horse_id
     */ 
    public void addHorseWinner(int horse_id){
        arr_list.add(horse_id);
    }
    
    /**
     * Removes all the elements of the horse_winners ArrayList so its ready to use on the following races
     */
    public void clearHorseWinners(){
       arr_list.clear();
    }
    
    /**
     * 
     * @return Returns a copy of bet_info. Easiest way to iterate to iterate through the map outside the repository
     */
    public Map<Integer,int[]> getBetInfoReferenceCopy(){
        return bet_info;   
    }
    
    /**
     * 
     * @return 
     */
    public boolean spectatorIsWinner(int spec_id){
       boolean b =  arr_list.contains(spec_id);
       return b;
    }
    
}
