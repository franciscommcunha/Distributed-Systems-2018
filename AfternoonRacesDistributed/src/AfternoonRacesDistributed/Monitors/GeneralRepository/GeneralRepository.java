package AfternoonRacesDistributed.Monitors.GeneralRepository;

import static AfternoonRacesDistributed.Constants.Constants.*;
import AfternoonRacesDistributed.Entities.Broker.BrokerEnum.BrokerStates;
import static AfternoonRacesDistributed.Entities.Broker.BrokerEnum.BrokerStates.*;
import AfternoonRacesDistributed.Entities.Spectators.SpectatorsEnum.SpectatorStates;
import static AfternoonRacesDistributed.Entities.Spectators.SpectatorsEnum.SpectatorStates.*;
import static AfternoonRacesDistributed.Entities.Horses.HorsesEnum.HorseStates.*;
import AfternoonRacesDistributed.Entities.Horses.HorsesEnum.HorseStates;

import java.util.concurrent.locks.ReentrantLock;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;


/**
 * Passive class that stores the variables and data structures that are shared by the entities or monitors.
 * Such information is then accessed by the entities by calling of Get type methods and altered by Set type methods,
 * all of which happens in mutual exclusion by the use of a lock 
 * 
 * @author João Amaral, nª mec. 76460
 * @author Francisco Cunha, nª mec 76759
 **/
public class GeneralRepository {
	
    /**
    * ReentrantLock instance used to establish mutual exlusion when acessing repository information by the threads
    */
	private final ReentrantLock rl;
	
    /**
    * String with then name of the log file
    */
	private final String file_name = "Log.txt";
	
    /**
     * PrintWriter used to write to log file
     */
	public PrintWriter writer;

	String s = "";
	String hs = "";
	String b = "";
	
	/**
	* Enumerate representing the broker state 
	*/
	private BrokerStates broker_state;
	
	/**
	* Enumerate representing the horses state
	*/
	private HorseStates[] horse_state;
	
	/**
	* Enumerate representing the spectators state;
	*/
	private SpectatorStates[] spectator_state;
	    
    /**
    * Boolean flag signaling the horses at the stable they can move to paddock
    */
    private boolean go_to_paddock;
    
    /**
    * Boolean flag signaling spectators that the race has finished
    */  
    private boolean race_finished;
    
    /**
     * Boolean flag signaling broker of the end of the race
     */
    private boolean all_horses_finished;
        
    /**
    * Retrieve the state of the broker 
    * @return current state of the broker
    */
    public BrokerStates getBrokerState(){
        rl.lock();
        BrokerStates bs;
        try{
            bs = this.broker_state;
        }finally{
            rl.unlock();
        }    
        return bs;
    }
    
    /**
    * Set the state of the broker 
    * @param state of the broker
    */
    public void setBrokerState(BrokerStates state){
        rl.lock();
        try{
            this.broker_state = state;
        }finally{
            rl.unlock();
        }        
    }
    
    /**
    * Retrieve the state the horse identified by the given id
    * @param id of the horse
    * @return current state of the horse
    */
    public HorseStates getHorseState(int id){
        rl.lock();
        try{
            return this.horse_state[id];
        }finally{
            rl.unlock();
        }        
    }
    
    /**
    * Sets the state the horse identified by the given id
    * @param state of the horse
    * @param id of the horse 
    */
    public void setHorseState(HorseStates state, int id){
        rl.lock();
        try{
            this.horse_state[id] = state;
            System.out.println(this.horse_state[id] + " HORSE " + id);
        }finally{
            rl.unlock();
        }        
    }
    
   /** 
    * Retrieve the state the spectator identified by the given id
    * @param id of the spectator
    * @return current state of the spectator
    */
    public SpectatorStates getSpectatorState(int id){
        rl.lock();
        SpectatorStates ss;
        try{
            ss =  this.spectator_state[id];
        }finally{
            rl.unlock();
        }   
        return ss;
    }
    
    /** 
    * Sets the state the spectator identified by the given id
    * @param state of the spectator
    * @param id of the spectator 
    */
    public void setSpectatorState(SpectatorStates state, int id){
        rl.lock();
        try{
            this.spectator_state[id] = state;
            System.out.println(spectator_state[id]+ " SPECTATOR " + id);
        }finally{
            rl.unlock();
        }        
    }   
    
    /**
    * Set the value of go_to_paddock variable
    * @param value of go_to_paddock variable
    */
    public void setGoToPaddock(boolean value){
        rl.lock();
        try{
            this.go_to_paddock = value;
        }finally{
            rl.unlock();
        }
    }
    
    /**
    * Retrieve the value of go_to_paddock boolean variable
    * @return value of the variable go_to_paddock
    */
    public boolean getGoToPaddock(){
        rl.lock();
        boolean b;
        try{
            b = this.go_to_paddock;
        }finally{
            rl.unlock();
        }
        return b;
    }
    
   /**
    * Set the value of race_finished variable
    * @param value to set race_finished variable
    */
    public void setRaceFinished(boolean value){
        rl.lock();
        try{
            this.race_finished = value;
        }finally{
            rl.unlock();
        }
    }
    
    /**
    * Retrieve the value of race_finished variable
    * @return value of the variable race_finished
    */
    public boolean getRaceFinished(){
        rl.lock();
        boolean b;
        try{
            b = this.race_finished;
        }finally{
            rl.unlock();
        }
        return b;
    }
        
    /**
     * 
     * @return value of boolean variable all_horses_finished
     */
    public boolean getAllHorsesFinished(){
        rl.lock();
        boolean b;
        try{    
            b =  all_horses_finished;
        }finally{
            rl.unlock();
        }
        return b;
    }
    
    /**
     * Hash map storing the information of a bet made by a spectator
     * Key -> Spectator Id
     * Value -> { Horse Id, Bet Amount }
     */
    private Map<Integer, int[]> bet_info = new HashMap<>();
    
    /**
     * @param spec_id id of the spectator
     * @param info array containing the horse id and the bet amout
     */
    public void setBetInfo(int spec_id, int[] info){
        rl.lock();
        try{
            bet_info.put(spec_id, info);
        }finally{
            rl.unlock();
        }
    }
    
     /**
     * @param spec_id id of the spectator
     * @return array containing the horse id and the bet amout
     */
    public int[] getBetInfo(int spec_id){
        rl.lock();
        try{
            return bet_info.get(spec_id);
        }finally{
            rl.unlock();
        }
    }
    
     /**
     * @return size of the bet_info map. Corresponds to the number of bets made
     */
    public int getBetInfoSize(){
    		
        rl.lock();
    		int size;
        try{
            size = bet_info.size();
        }
        finally{
            rl.unlock();
        }
        return size;
    }
    
    /**
     * Clear the Map bet_info after each finished race and bets honoured
     */
    public void clearBetInfo(){
        rl.lock();
        try{
            bet_info.clear();
        }finally{
            rl.unlock();
        }
    }
    
    /**
     * Filter any bets made by spectators of amout equal to 0 (zero)
     */
    public void filterZeroAmountBets(){
        rl.lock();
        try{
            Iterator<Map.Entry<Integer,int[]>> it = bet_info.entrySet().iterator();
            while(it.hasNext()){
                int[] info = it.next().getValue();
                if(info[1] == 0){
                    it.remove();
                }
            }
        }finally{
            rl.unlock();
        }
    }
    
    /**
     * 
     * @return Returns a copy of bet_info. Easiest way to iterate to iterate through the map outside the repository
     */
    public Map<Integer,int[]> getBetInfoReferenceCopy(){
        rl.lock();
        try{
            return bet_info;
        }finally{
            rl.unlock();
        }     
    }    
    
    /**
     * Queue containing the results of the race (horse id and last iteration)
     */
    private Queue<int[]> horse_results = new LinkedList<>();
    
    /**
    * Add the results of the race
    */
    public void addHorseResult(int[] result){
        rl.lock();
        try{
            horse_results.add(result);
        }finally{
            rl.unlock();
        }
    }
    
    /**
    * Retrives the size of the queue which contains the results of the race
    */
    public int getHorseResultSize(){
        rl.lock();
        int size;
        try{
            size = horse_results.size();
        }finally{
            rl.unlock();
        }    
        return size;
    }
    
    /**
     * Removes from the queue the result
     */
    public int[] removeHorseResult(){
        rl.lock();
        try{
            return horse_results.remove();
        }finally{
            rl.unlock();
        }    
    }
    
    /**
     * Returns the peek of the queue
     */
    public int[] peekHorseResult(){
        rl.lock();
        try{
            return horse_results.peek();
        }finally{
            rl.unlock();
        }    
    }
    
    /**
     * ArrayList containing the id(s) of the horse winner(s) of the current race
     */
    private ArrayList<Integer> horse_winners = new ArrayList<Integer>();
    
    /**
     * 
     * @param horse_id
     * @return returns true/false wether the given horse id is present in horse_winners
     */
    public boolean horseIsWinner(int horse_id){
        rl.lock();
        boolean b;
        try{
            b = horse_winners.contains(horse_id); 
        }finally{
            rl.unlock();
        }
        return b;
    }
    
    /**
     * 
     * @param horse_id
     */ 
    public void addHorseWinner(int horse_id){
        rl.lock();
        try{
            horse_winners.add(horse_id);
        }finally{
            rl.unlock();
        }
    }
    
    /**
     * Removes all the elements of the horse_winners ArrayList so its ready to use on the following races
     */
    public void clearHorseWinners(){
        rl.lock();
        try{
            horse_winners.clear();
        }
        finally{
            rl.unlock();
        }  
    }
    
    /**
     * 
     * @return Returns the string representation of the horse_winners ArrayList
     */
    public String horseWinnerToString(){
        rl.lock();
        String st;
        try{
            st =  horse_winners.toString();
        }finally{
            rl.unlock();
        }    
        return st;
    }
        
    /**
     * ArrayList containing the id(s) of the winning spectator(s)
     */
    private ArrayList<Integer> winning_spectators = new ArrayList<>();

    /**
     * 
     * @param spec_id id of the spectator
     */
    public void addWinningSpectator(int spec_id){
        rl.lock();
        try{
            winning_spectators.add(spec_id);
        }finally{
            rl.unlock();
        }
    }
    
    /**
     * 
     * @return size of winning_spectators ArrayList
     */
    public int getWinningSpectatorsSize(){
        rl.lock();
        int size;
        try{
            size =  winning_spectators.size();
        }finally{
            rl.unlock();
        }
        return size;
    }
 
    /**
     * 
     * @return 
     */
    public boolean spectatorIsWinner(int spec_id){
        rl.lock();
        boolean b;
        try{
            b =  winning_spectators.contains(spec_id);
        }finally{
            rl.unlock();
        }
        return b;
    }
    
    /**
     * Removes all the elements of the winning_spectators ArrayList so its ready to use on the following races
     */
    public void clearWinningSpectators(){
        rl.lock();
        try{
            winning_spectators.clear();
        }finally{
            rl.unlock();
        }
    }
    
    /***************************************************************************************************/
    
    private int race_iteration = 0;
    
    // gets the value of race iterations
    public int getRaceIteration() {
        rl.lock();
        int r;
        try {
        		r = race_iteration;
        }
        finally {
        		rl.unlock();
        }
        return r;
    }
        
    // increments the number of race iterations
    public void incRaceIteration() {
        rl.lock();
        try {
            race_iteration++;
        }finally {
            rl.unlock();        	
        }
    }
        
    // decrements the number of race iterations
    public void decRaceIteration() {
        rl.lock();
        try {
            race_iteration--;
        }finally {
            rl.unlock();        	
        }
    }
     
    // reses the value of race iteration to the default
    public void resetRaceIteration() {
        rl.lock();
        try {
        		race_iteration = 0;
        }finally {
            rl.unlock();        	
        }
    }
    
    /**
     * All variable information is in the initial state.
     * The changes will only occur by the use of set functions.
     * Values will only be obtained by the use of get functions
     */
	public GeneralRepository() {
		rl = new ReentrantLock(true);
		
        /* Initialize actors states  */ 
        horse_state = new HorseStates[NUM_HORSES];
        spectator_state = new SpectatorStates[NUM_SPECTATORS];
                
        broker_state = OPENING_THE_EVENT;
        
        for(int i=0; i < horse_state.length ; i++) horse_state[i] = AT_THE_FIELDS;
        
        for(int i=0; i < spectator_state.length ; i++) spectator_state[i] =  OUTSIDE_HIPPIC_CENTER;
 
        /* Initialize variables */
        go_to_paddock = false;
        race_finished = false;  
	}

	
    /******** LOGGER ********/
    
    String h = "         AFTERNOON AT THE RACE TRACK - " + "Description of the internal state of the problem\n";
    String h1 = "MAN/BRK           SPECTATOR/BETTER              "                            + "HORSE/JOCKEY PAIR AT RACE RN";
    String h2 = "  Stat  St0  Am0 St1  Am1 St2  Am2 St3  Am3 RN "                            +     "St0 Len0 St1 Len1 St2 Len2 St3 Len3";
    String h3 = "                                        "
                + "RACE RN Status";
    String h4 = " RN Dist BS0  BA0 BS1  BA1 BS2  BA2 BS3  BA3  Od0 "+ "N0 PS0 SD0 Od1 N1 PS1 Sd1 Od2 N2 Ps2 Sd2 Od3 N3 Ps3 St3";
    String h5 = "  ####  ### #### ### #### ### #### ### ####  # ###  ##  ###  ##  ###  ##  ###  ##";
    String h6 = "  #  ##   #  ####  #  ####  #  ####  #  #### #### ##  ##  # #### ##  ##  # #### ##  ##  # #### ##  ##  #";
    
    String h7 = "\n" + 
                    "Legend:\n" + 
                    "Stat - manager/broker state\n" + 
                    "St# - spectator/better state (# - 0 .. 3)\n" + 
                    "Am# - spectator/better amount of money she has presently (# - 0 .. 3)\n" + 
                    "RN - race number\n" + 
                    "St# - horse/jockey pair state in present race (# - 0 .. 3)\n" + 
                    "Len# - horse/jockey pair maximum moving length per iteration step in present race (# - 0 .. 3)\n" + 
                    "RN - race number\n" + 
                    "Dist - race track distance in present race\n" + 
                    "BS# - spectator/better bet selection in present race (# - 0 .. 3)\n" + 
                    "BA# - spectator/better bet amount in present race (# - 0 .. 3)\n" + 
                    "Od# - horse/jockey pair winning probability in present race (# - 0 .. 3)\n" + 
                    "N# - horse/jockey pair iteration step number in present race (# - 0 .. 3)\n" + 
                    "Ps# - horse/jockey pair track position in present race (# - 0 .. 3)\n" + 
                    "SD# - horse/jockey pair standing at the end of present race (# - 0 .. 3)\n" +
                    "\n" +
                    "OTE - OPENING_THE_EVENT state(broker)\n" + 
                    "ANR - ANNOUNCING_NEXT_RACE state(broker)\n" +
                    "WFB - WAITING_FOR_BETS state(broker)\n" +
                    "STR - SUPERVISING_THE_RACE state(broker)\n" +
                    "SA - SETTLING_ACCOUNTS state(broker)\n" +
                    "PHB - PLAYING_HOST_AT_THE_BAR state(broker)\n" +
                    "NDS - NOT DEFINED STATE state(broker /horse / spectator ) \n" +
                    "\n" +
                    "WRS - WAITING_FOR_A_RACE_TO_START state(spectator)\n" +
                    "ATH - APPRAISING_THE_HORSES state(spectator)\n" +
                    "PAB - PLACING_A_BET state(spectator)\n" +
                    "WAR - WATCHING_A_RACE state(spectator)\n" +
                    "CTG - COLLECTING_THE_GAINS state(spectator)\n" +
                    "CEL - CELEBRATING state(spectator)\n" +
                    "OHC - OUTSIDE_HIPPIC_CENTER state(broker)\n" +
                    "\n" +
                    "ATF - AT_THE_FIELDS state(horse)\\n" +
                    "ATS - AT_THE_STABLE state(horse)\n" +
                    "ATP - AT_THE_PADDOCK state(horse)\n" +
                    "ASL - AT_THE_START_LINE state(horse)\n" +
                    "RUN - RUNNING state(horse)\n" +
                    "AFL - AT_THE_FINISH_LINE state(horse)\n" + 
                    "\n";
	
    // writes the format of the log file and it's legend
    public void initLog() {
            rl.lock();
            try {
                    writer = new PrintWriter(new FileOutputStream(new File(file_name), true ));
                    writer.println(hs);
                    writer.println(h1);
                    writer.println(h2);
                    writer.println(h3);
                    writer.println(h4);
                    writer.println(h5);
                    writer.println(h6);
                    writer.println(h7);
            } 
            catch(Exception e) {
                    System.err.println("ERROR WRITING FILE");
            }
            rl.unlock();        
    } 
		
	// updates the string b with the abbreviation of the Broker state
	public void writeStatB(BrokerStates state) {
		rl.lock();
		
		b = ""; // resets the string b
		if(state.equals(OPENING_THE_EVENT)) {;
			b = "OTE";
		}
		else if(state.equals(ANNOUNCING_NEXT_RACE)) {
			b = "ANR";
		}
		else if(state.equals(WAITING_FOR_BETS)) {
			b = "WFB";
		}
		else if(state.equals(SUPERVISING_THE_RACE)) {
			b = "STR";
		}
		else if(state.equals(SETTLING_ACCOUNTS)) {
			b = "SA";
		}
		else if(state.equals(PLAYING_HOST_AT_THE_BAR)) {
			b = "PHB";
		}
		else {
			b = "NDS";
		}		
		
		rl.unlock();
	}
	
	// updates the string s with the abbreviation of the Spectator state
	public void writeSpecSt(SpectatorStates state) {
		rl.lock();
		
		s = ""; // resets the string s
		if(state.equals(WAITING_FOR_A_RACE_TO_START )) {
			s = "WRS";
		}
		else if(state.equals(APPRAISING_THE_HORSES )) {
			s = "ATH";
		}
		else if(state.equals(PLACING_A_BET )) {
			s = "PAB";
		}
		else if(state.equals(WATCHING_A_RACE )) {
			s= "WAR";
		}
		else if(state.equals(COLLECTING_THE_GAINS )) {
			s = "CTG";
		}
		else if(state.equals(CELEBRATING )) {
			s = "CEL";
		}
		else if(state.equals(OUTSIDE_HIPPIC_CENTER ))  {
			s = "OHC";
		}
		else {
			s = "NDS";
		}		
		
		rl.unlock();
	}
	
	// updates the string hs with the abbreviation of the Horse/Jockey state
    public void writeHorseSt(HorseStates state) {
            rl.lock();
            
            hs = ""; // resets the string hs
            if(state.equals(AT_THE_STABLE)) {
                    hs = "ATS";
            }
            else if(state.equals(AT_THE_PADDOCK)) {
                    hs = "ATP";
            }
            else if(state.equals(AT_THE_START_LINE)) {
                    hs = "ASL";
            }
            else if(state.equals(RUNNING)) {
                    hs = "RUN";
            }
            else if(state.equals(AT_THE_FINISH_LINE)) {
                    hs = "AFL";
            }
            else if(state.equals(AT_THE_FIELDS)){
                    hs = "ATF";
            }
            else {
                    hs = "NDS";
            }
            
            rl.unlock();
    }
	
	public void makeLogger() {
		
		String m = Integer.toString(SPECTATOR_INIT_MONEY); 
		String d = Integer.toString(RACING_TRACK_LENGTH);
		
		rl.lock();
		
		String nh1 = "MAN/BRK           SPECTATOR/BETTER              "	                    + "HORSE/JOCKEY PAIR AT RACE "+getRaceIteration();
		writer.println(nh1);
			
		String nh2 = "  Stat  St0  Am0 St1  Am1 St2  Am2 St3  Am3 RN "	                    +     "St0 Len0 St1 Len1 St2 Len2 St3 Len3\n"+
			         "  "+b+"   "+s+"  "+m+" "+s+"  "+m+" "+s+"  "+m+" "+s+"  "+m+" "+getRaceIteration()+"  "+hs+"  "+d+"  "+hs+"  "+d+"  "+hs+"  "+d+"  "+hs+"  "+d;
			
		writer.println(nh2);
			
		String nh3 = "                                        "
	                + "RACE "+getRaceIteration()+" Status";
		writer.println(nh3);
			
		String nh4 = " RN Dist BS0  BA0 BS1  BA1 BS2  BA2 BS3  BA3  Od0 "+ "N0 PS0 SD0 Od1 N1 PS1 Sd1 Od2 N2 Ps2 Sd2 Od3 N3 Ps3 St3\n" +
						"";
		writer.println(nh4);
		writer.println("\n");
		rl.unlock();
	}
	
	public void closeWriter() {
		rl.lock();
		writer.close();
		rl.unlock();
	}
}
