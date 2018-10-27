package AfternoonRacesDistributed.Entities.Broker;

import AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelClient;
import AfternoonRacesDistributed.Monitors.GeneralRepository.GeneralRepository;
import AfternoonRacesDistributed.Entities.Broker.BrokerInterfaces.*;
import static AfternoonRacesDistributed.Entities.Broker.BrokerEnum.BrokerStates.*;
import static AfternoonRacesDistributed.Constants.Constants.*;
import AfternoonRacesDistributed.Entities.Broker.BrokerEnum.BrokerStates;
import AfternoonRacesDistributed.Messages.BettingCenterMessage.BettingCenterMessage;
import AfternoonRacesDistributed.Messages.RacingTrackMessage.RacingTrackMessage;
import AfternoonRacesDistributed.Messages.ControlCenterMessage.ControlCenterMessage;
import AfternoonRacesDistributed.Messages.PaddockMessage.PaddockMessage;
import AfternoonRacesDistributed.Messages.RepositoryMessage.RepositoryMessage;
import AfternoonRacesDistributed.Messages.StableMessage.StableMessage;

/**
 * Active class representing the Broker entity
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class Broker extends Thread {
	
    private CommunicationChannelClient cc_stable;
    private CommunicationChannelClient cc_paddock;
    private CommunicationChannelClient cc_betting_center;
    private CommunicationChannelClient cc_control_center;
    private CommunicationChannelClient cc_racing_track;
    private CommunicationChannelClient cc_repository;

    /**
     * Broker entity of the problem. Created with already initalized communications classes 
     * in order to communicate with the problem server monitors
     * 
     * @param cc_stable communication class for stable
     * @param cc_paddock communication class for paddock
     * @param cc_betting_center communication class for betting center
     * @param cc_control_center communication class for control center
     * @param cc_racing_track communication class for racing track
     * @param cc_repository  communication class for repository
     */
    public Broker(CommunicationChannelClient cc_stable, CommunicationChannelClient cc_paddock, CommunicationChannelClient cc_betting_center, 
                CommunicationChannelClient cc_control_center, CommunicationChannelClient cc_racing_track, CommunicationChannelClient cc_repository){
        this.cc_stable = cc_stable;
        this.cc_paddock = cc_paddock;
        this.cc_betting_center = cc_betting_center;
        this.cc_control_center = cc_control_center;
        this.cc_racing_track = cc_racing_track;
        this.cc_repository = cc_repository;
    }
        
    /**
     * Sends RepositoryMessage to General Repository server to update the broker state to state
     * @param state Next state of the broker
     */
    private void setBrokerState(BrokerStates state){            
        RepositoryMessage response;
        openComm(cc_repository, "Repository");
        cc_repository.writeObject(new RepositoryMessage(RepositoryMessage.SET_BROKER_STATE,state));
        response = (RepositoryMessage) cc_repository.readObject();
        cc_repository.close(); 
    }
        
    /**
     * Sends message to repository and retrieves the current state of the broker
     * @return The broker state contained in the message
    */
    private BrokerStates getBrokerState(){
        RepositoryMessage response;
        openComm(cc_repository, "Repository");
        cc_repository.writeObject(new RepositoryMessage(RepositoryMessage.BROKER_STATE));
        response = (RepositoryMessage) cc_repository.readObject(); 
        return response.getBrokerState();
    }
        
    /**
     * Sends INIT_LOG message to General Repository server and receives respective response
     */
    private void initLog() {
        RepositoryMessage response;
        openComm(cc_repository, "Repository");
        cc_repository.writeObject(new RepositoryMessage(RepositoryMessage.INIT_LOG));
        response = (RepositoryMessage) cc_repository.readObject(); 
        cc_repository.close();
    }
    
    /**
     * Sends MAKE_LOGGER message to General Repository server and receives respective response
     */
    private void makeLogger() {
        RepositoryMessage response;
        openComm(cc_repository, "Repository");
        cc_repository.writeObject(new RepositoryMessage(RepositoryMessage.MAKE_LOGGER));
        response = (RepositoryMessage) cc_repository.readObject(); 
        cc_repository.close(); 
    }
        
    /**
     * Sends WRITE_STAT_B and state of the broker message to General Repository server and receives respective response
      * @param state state of the broker
     */
    private void writeStatB(BrokerStates state) {
        RepositoryMessage response;
        openComm(cc_repository, "Repository");
        cc_repository.writeObject(new RepositoryMessage(RepositoryMessage.WRITE_STAT_B, state));
        response = (RepositoryMessage) cc_repository.readObject(); 
        cc_repository.close();
    }
    
    /**
     * Sends CLOSE_WRITTER message to General Repository server and receives respective response
     */
    private void closeWriter() {
        RepositoryMessage response;
        openComm(cc_repository, "Repository");
        cc_repository.writeObject(new RepositoryMessage(RepositoryMessage.CLOSE_WRITTER));
        response = (RepositoryMessage) cc_repository.readObject(); 
        cc_repository.close();
    }
      
    /**
     * Sends SUMMON_HORSES_TO_PADDOCK message to stable server and receives respective response
     */
    private void summonHorsesToPaddockAtStable(){    
        StableMessage response;
        openComm(cc_stable, "Stable");
        cc_stable.writeObject(new StableMessage(StableMessage.SUMMON_HORSES_TO_PADDOCK));
        response = (StableMessage) cc_stable.readObject();
        cc_stable.close();
    }
        
    /**
     * Sends SUMMON_HORSES_TO_PADDOCK message to paddock server and receives respective response
     */
    private void summonHorsesToPaddockAtPaddock(){
        PaddockMessage response;
        openComm(cc_paddock, "Paddock");
        cc_paddock.writeObject(new PaddockMessage(PaddockMessage.SUMMON_HORSES_TO_PADDOCK));
        response = (PaddockMessage) cc_paddock.readObject();
        cc_paddock.close();   
    }
        
    /**
     * Sends ACCEPT_THE_BETS message to betting center server and receives respective response
     */
    private void acceptTheBets() {
        BettingCenterMessage response;
        openComm(cc_betting_center, "Betting Center");
        cc_betting_center.writeObject(new BettingCenterMessage(BettingCenterMessage.ACCEPT_THE_BETS));
        response = (BettingCenterMessage) cc_betting_center.readObject();
        cc_betting_center.close(); 
    }

    /**
     * Sends START_THE_RACE message to Racing Track server and receives respective response
     */
    private void startTheRace() {
        RacingTrackMessage response;
        openComm(cc_racing_track, "Racing Track");
        cc_racing_track.writeObject(new RacingTrackMessage(RacingTrackMessage.START_THE_RACE));
        response = (RacingTrackMessage) cc_racing_track.readObject();
        cc_racing_track.close();
    }
        
    /**
     * Sends REPORT_RESULTS message to paddock server and receives respective response
     */
    private void reportResults() {
        ControlCenterMessage response;
        openComm(cc_control_center, "Control Center");
        cc_control_center.writeObject(new ControlCenterMessage(ControlCenterMessage.REPORT_RESULTS));
        response = (ControlCenterMessage) cc_control_center.readObject();
    }

    /**
     * Sends ARE_THERE_ANY_WINNERS message to paddock server and receives respective response
     */
    private boolean areThereAnyWinners() {
        ControlCenterMessage response;
        openComm(cc_control_center, "Control Center");
        cc_control_center.writeObject(new ControlCenterMessage(ControlCenterMessage.ARE_THERE_ANY_WINNERS));
        response = (ControlCenterMessage) cc_control_center.readObject();
        cc_control_center.close();
        return response.getBoolean();
    }
        
    /**
     * Sends HONOUR_THE_BETS message to betting center server and receives respective response
     */
    private void honourTheBets() { 
        BettingCenterMessage response;
        openComm(cc_betting_center, "Betting Center");
        cc_betting_center.writeObject(new BettingCenterMessage(BettingCenterMessage.HONOUR_THE_BETS));
        response = (BettingCenterMessage) cc_betting_center.readObject();
        cc_betting_center.close();
    }

    /**
     * Sends ENTERTAIN_THE_GUESTS message to control center server and receives respective response
     */
    private void entertainTheGuests() {
        ControlCenterMessage response;
        openComm(cc_control_center, "Control Center");
        cc_control_center.writeObject(new ControlCenterMessage(ControlCenterMessage.ENTERTAIN_THE_GUESTS));
        response = (ControlCenterMessage) cc_control_center.readObject();
        cc_control_center.close();
    }
    
    /**
     * Sends END message to terminate all server remote machines
     * before broker finished executing
     */
    private void endExecution(){

        openComm(cc_stable, "Stable");
        cc_stable.writeObject(new StableMessage(StableMessage.END));
        cc_stable.readObject();
        cc_stable.close();
        
        openComm(cc_paddock, "Paddock");
        cc_paddock.writeObject(new PaddockMessage(PaddockMessage.END));
        cc_paddock.readObject();
        cc_paddock.close();
        
        openComm(cc_betting_center, "Betting Center");
        cc_betting_center.writeObject(new BettingCenterMessage(BettingCenterMessage.END));
        cc_betting_center.readObject();
        cc_betting_center.close();
        
        openComm(cc_racing_track, "Racing Track");
        cc_racing_track.writeObject(new RacingTrackMessage(RacingTrackMessage.END));
        cc_racing_track.readObject();
        cc_racing_track.close();
        
        openComm(cc_control_center, "Control Center");
        cc_control_center.writeObject(new ControlCenterMessage(ControlCenterMessage.END));
        cc_control_center.readObject();
        cc_control_center.close();
        
        openComm(cc_repository, "General Repository");
        cc_repository.writeObject(new RepositoryMessage(RepositoryMessage.END));
        cc_repository.readObject();
        cc_repository.close();
         
    }
        
    /**
     * Generic method to open the communication to the server.
     * In case of failure keeps trying to initiate communication in 1 second intervals
     * 
     * @param cc the communication channel to open the connection
     * @param name Name of the monitor corresponding to the channel
     */
    private void openComm(CommunicationChannelClient cc, String name){
        while(!cc.open()){
            System.out.println(name + " not open, trying again...");
            try{
                Thread.sleep(1000);
            }catch(Exception ex){ }            
        }
    }
           
    @Override
    public void run(){	

        initLog();
        writeStatB(getBrokerState());

        System.out.println("BROKER, " + getBrokerState());

        boolean event_finished = false;
        int race_number = 0;

        // broker main lifecycle
        while(!event_finished) {
            switch(getBrokerState()) {

                case OPENING_THE_EVENT :
                    System.out.println("OPENING_THE_EVENT, ANNOUCING_NEXT_RACE");
                    summonHorsesToPaddockAtStable(); // broker sends horses to the paddock
                    summonHorsesToPaddockAtPaddock();  // method used to wake up the borker, so he can start accepting bets
                    setBrokerState(ANNOUNCING_NEXT_RACE);
                    writeStatB(ANNOUNCING_NEXT_RACE);
                    makeLogger();
                    break;

                case ANNOUNCING_NEXT_RACE :
                    System.out.println("BROKER, ANNOUCING_NEXT_RACE");
                    acceptTheBets(); // broker can start accepting the bets of all spectators
                    setBrokerState(WAITING_FOR_BETS);
                    writeStatB(WAITING_FOR_BETS);
                    makeLogger();
                    break;

                case WAITING_FOR_BETS :  
                    System.out.println("BROKER, WAITING_FOR_BETS");
                    startTheRace();// broker starts the race after all the bets are done
                    setBrokerState(SUPERVISING_THE_RACE);
                    writeStatB(SUPERVISING_THE_RACE);
                    makeLogger();

                case SUPERVISING_THE_RACE:
                    System.out.println("SUPERVISING_THE_RACE, BROKER");
                    race_number++;
                    reportResults(); // broker report the results to spectators
                    
                    if(areThereAnyWinners()) { // if some spectator has bet in the winner horse, the spectator is a winner
                        honourTheBets();
                        setBrokerState(SETTLING_ACCOUNTS);
                        writeStatB(SETTLING_ACCOUNTS);
                        makeLogger();
                    }
                    else if(race_number == NUM_RACES) {
                        //summonHorsesToPaddockAtStable();
                        entertainTheGuests();
                        setBrokerState(PLAYING_HOST_AT_THE_BAR);
                        writeStatB(PLAYING_HOST_AT_THE_BAR);
                        makeLogger();
                    }
                    else {
                        summonHorsesToPaddockAtStable(); // broker sends the horses to the paddock after the race has finished
                        summonHorsesToPaddockAtPaddock();
                        setBrokerState(ANNOUNCING_NEXT_RACE);
                        writeStatB(ANNOUNCING_NEXT_RACE);
                        makeLogger();
                    }
                    break;
                  
                case SETTLING_ACCOUNTS :
                    System.out.println("SETTLING_ACCOUNTS, BROKER");
                    if(race_number == NUM_RACES) {
                        //summonHorsesToPaddockAtStable(); // broker sends the horses to the paddock after the race has finished
                        entertainTheGuests();
                        setBrokerState(PLAYING_HOST_AT_THE_BAR);
                        writeStatB(PLAYING_HOST_AT_THE_BAR);
                        makeLogger();
                    }
                    else {
                        summonHorsesToPaddockAtStable(); 
                        summonHorsesToPaddockAtPaddock();
                        setBrokerState(ANNOUNCING_NEXT_RACE);
                        writeStatB(ANNOUNCING_NEXT_RACE);
                        makeLogger();
                    }
                    break;
                    
                case PLAYING_HOST_AT_THE_BAR :
                    System.out.println("PLAYING_HOST_AT_THE_BAR, BROKER");
                    setBrokerState(PLAYING_HOST_AT_THE_BAR);
                    writeStatB(PLAYING_HOST_AT_THE_BAR);
                    makeLogger();
                    endExecution();
                    event_finished = true;
                    break;
                
                default :
                        break;
            }
        }
        //closeWriter();
    }
}