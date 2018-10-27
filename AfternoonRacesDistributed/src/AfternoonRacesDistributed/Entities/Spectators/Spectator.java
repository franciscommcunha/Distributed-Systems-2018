package AfternoonRacesDistributed.Entities.Spectators;

import AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelClient;
import static AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelPorts.*;
import java.util.concurrent.ThreadLocalRandom;

import static AfternoonRacesDistributed.Constants.Constants.*;
import AfternoonRacesDistributed.Entities.Horses.HorsesEnum.HorseStates;
import AfternoonRacesDistributed.Entities.Spectators.SpectatorsEnum.SpectatorStates;
import AfternoonRacesDistributed.Monitors.GeneralRepository.GeneralRepository;
import AfternoonRacesDistributed.Entities.Spectators.SpectatorsInterfaces.*;
import static AfternoonRacesDistributed.Entities.Spectators.SpectatorsEnum.SpectatorStates.*;
import AfternoonRacesDistributed.Messages.BettingCenterMessage.BettingCenterMessage;
import AfternoonRacesDistributed.Messages.PaddockMessage.PaddockMessage;
import AfternoonRacesDistributed.Messages.RepositoryMessage.RepositoryMessage;
import AfternoonRacesDistributed.Messages.ControlCenterMessage.ControlCenterMessage;
import AfternoonRacesDistributed.Monitors.ControlCenter.ControlCenter;

public class Spectator extends Thread{
	
    private final int spec_id;
    private int money;
	
    private CommunicationChannelClient cc_control_center;
    private CommunicationChannelClient cc_paddock;
    private CommunicationChannelClient cc_betting_center;
    private CommunicationChannelClient cc_repository;
	
    /**
     * Spectator entity of the problem. Initialized with the given id and money value
     * Initializes the communication channels used in order to communicate
     * with the problem server monitors
     * 
     * @param spec_id integer value of the id of the spectator
     * @param money integer value representing the money of the spectator
     */
    public Spectator(int spec_id, int money){
        this.spec_id = spec_id;
	this.money = money;         
        this.cc_control_center = new CommunicationChannelClient(NAME_CONTROL_CENTER, PORT_CONTROL_CENTER);
        this.cc_paddock = new CommunicationChannelClient(NAME_PADDOCK, PORT_PADDOCK);
        this.cc_betting_center = new CommunicationChannelClient(NAME_BETTING_CENTER, PORT_BETTING_CENTER);
        this.cc_repository = new CommunicationChannelClient(NAME_GENERAL_REPOSITORY, PORT_GENERAL_REPOSITORY);   
    }

     /**
     * Sends message to repository and retrieves the current state of the spectator
     * @return The spectator state contained in the message
     */
    private SpectatorStates getSpectatorState(){
        RepositoryMessage response;
        openComm(cc_repository, "Spectator " + this.spec_id + ": Repository");
        cc_repository.writeObject(new RepositoryMessage(RepositoryMessage.SPECTATOR_STATE, this.spec_id));
        response = (RepositoryMessage) cc_repository.readObject(); 
        cc_repository.close();
        return response.getSpectatorState(); 
    }

    /**
     * Sends RepositoryMessage to General Repository server to update the spectator internal state to state
     * @param state Next state of the spectator
     */
    private void setSpectatorState(SpectatorStates state){
        RepositoryMessage response;
        openComm(cc_repository, "Spectator " + this.spec_id + ": Repository");
        cc_repository.writeObject(new RepositoryMessage(RepositoryMessage.SET_SPECTATOR_STATE,state,this.spec_id));
        response = (RepositoryMessage) cc_repository.readObject();;
        cc_repository.close(); 
    }
    
    /**
    * Sends INIT_LOG message to General Repository server and receives respective response
    */
    private void initLog() {
        RepositoryMessage response;
        openComm(cc_repository, "Spectator " + this.spec_id + ": Repository");
        cc_repository.writeObject(new RepositoryMessage(RepositoryMessage.INIT_LOG));
        response = (RepositoryMessage) cc_repository.readObject(); 
        cc_repository.close();
    }
    
    /**
     * Sends MAKE_LOGGER message to General Repository server and receives respective response
     */
    private void makeLogger() {
        RepositoryMessage response;
        openComm(cc_repository, "Spectator " + this.spec_id + ": Repository");
        cc_repository.writeObject(new RepositoryMessage(RepositoryMessage.MAKE_LOGGER));
        response = (RepositoryMessage) cc_repository.readObject(); 
        cc_repository.close(); 
    }
    
    /**
     * Sends MAKE_LOGGER message to General Repository server and receives respective response
     * @param state state of the Spectator 
     */
    private void writeSpecSt(SpectatorStates state) {
        RepositoryMessage response;
        openComm(cc_repository, "Spectator " + this.spec_id + ": Repository");
        cc_repository.writeObject(new RepositoryMessage(RepositoryMessage.WRITE_SPEC_ST, state));
        response = (RepositoryMessage) cc_repository.readObject(); 
        cc_repository.close(); 
    }
    
    /**
     * Sends CLOSE_WRITTER message to General Repository server and receives respective response
     */
    private void closeWriter() {
        RepositoryMessage response;
        openComm(cc_repository, "Spectator " + this.spec_id + ": Repository");
        cc_repository.writeObject(new RepositoryMessage(RepositoryMessage.CLOSE_WRITTER));
        response = (RepositoryMessage) cc_repository.readObject(); 
        cc_repository.close();
    }
    
    
    /**
     * Sends WAIT_FOR_NEXT_RACE message to paddock server and receives respective response
     */
    private void waitForNextRace(){    
        PaddockMessage response;
        openComm(cc_paddock, "Spectator " + this.spec_id + ": Paddock");
        cc_paddock.writeObject(new PaddockMessage(PaddockMessage.WAIT_FOR_NEXT_RACE, this.spec_id));
        response = (PaddockMessage) cc_paddock.readObject();
        cc_paddock.close();
    }
    
     /**
     * Sends GO_CHECK_HORSES message to paddock server and receives respective response
     */
    private void goCheckHorses() {
        PaddockMessage response;
        openComm(cc_paddock, "Spectator " + this.spec_id + ": Paddock");
        cc_paddock.writeObject(new PaddockMessage(PaddockMessage.GO_CHECK_HORSES, this.spec_id));
        response = (PaddockMessage) cc_paddock.readObject();
        cc_paddock.close();    
    }
    
     /**
     * Sends PLACE_A_BET message to betting center server and receives respective response
     * @param horse_id id of the horse to bet on
     * @param money money amount of the bet
     */
    private void placeABet(int horse_id, int money) {
        BettingCenterMessage response;
        openComm(cc_betting_center, "Spectator " + this.spec_id + ": Betting Center");
        cc_betting_center.writeObject(new BettingCenterMessage(BettingCenterMessage.PLACE_A_BET, this.spec_id, horse_id, money));
        response = (BettingCenterMessage) cc_betting_center.readObject();
        cc_betting_center.close();    
    }
    
    /**
    * Sends GO_WATCH_THE_RACE message to betting center server and receives respective response 
    */
    private void goWatchTheRace() {
        ControlCenterMessage response;
        openComm(cc_control_center, "Spectator " + this.spec_id + ": Control Center");
        cc_control_center.writeObject(new ControlCenterMessage(ControlCenterMessage.GO_WATCH_THE_RACE, this.spec_id));
        response = (ControlCenterMessage) cc_control_center.readObject();
        cc_control_center.close();
    }
    
    /**
     * Sends HAVE_I_WON message to Control Center server and receives respective response
     */
    private boolean haveIWon() {
        ControlCenterMessage response;
        openComm(cc_control_center, "Spectator " + this.spec_id + ": Control Center");
        cc_control_center.writeObject(new ControlCenterMessage(ControlCenterMessage.HAVE_I_WON, this.spec_id));
        response = (ControlCenterMessage) cc_control_center.readObject();
        cc_control_center.close();
        return response.getBoolean();
    }
    
     /**
     * Sends GO_COLLECT_THE_GAINS message to stable server and receives respective response
     */
    private int goCollectTheGains() { 
        BettingCenterMessage response;
        openComm(cc_betting_center, "Spectator " + this.spec_id + ": Betting Center");
        cc_betting_center.writeObject(new BettingCenterMessage(BettingCenterMessage.GO_COLLECT_THE_GAINS, this.spec_id));
        response = (BettingCenterMessage) cc_betting_center.readObject();
        cc_betting_center.close();
        return response.getSpecId();
    }
    
     /**
     * Sends RELAX_A_BIT message to stable server and receives respective response
     */
    private void relaxABit() {
        ControlCenterMessage response;
        openComm(cc_control_center, "Spectator " + this.spec_id + ": Control Center");
        cc_control_center.writeObject(new ControlCenterMessage(ControlCenterMessage.RELAX_A_BIT, this.spec_id));
        response = (ControlCenterMessage) cc_control_center.readObject();
        cc_control_center.close();
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
		
        boolean celebrating = false;
        int race_number = 0;
         
        System.out.println(getSpectatorState()+", SPECTATOR " + this.spec_id);
        
        initLog(); // prints the legend and the format of the logger
        writeSpecSt(getSpectatorState());
        makeLogger();
        
        /* spectator main lifecycle */
        while(!celebrating) {
            switch(getSpectatorState()) {

                case OUTSIDE_HIPPIC_CENTER :
                    waitForNextRace(); // spectators are waiting for a race to start
                    setSpectatorState(WAITING_FOR_A_RACE_TO_START);
                    writeSpecSt(WAITING_FOR_A_RACE_TO_START);
                    makeLogger();
                    break;
                
                case WAITING_FOR_A_RACE_TO_START :
                    System.out.println("WAITING_FOR_A_RACE_TO_START, SPECTATOR " + spec_id); 
                    goCheckHorses(); // spectators go watching the horses after all oF them are at the paddock
                    setSpectatorState(APPRAISING_THE_HORSES);
                    writeSpecSt(APPRAISING_THE_HORSES);
                    makeLogger();
                    break;

                case APPRAISING_THE_HORSES : 
                    System.out.println("APPRAISING_THE_HORSES, SPECTATOR " + spec_id);
                    int bet_money = ThreadLocalRandom.current().nextInt(0, money);
                    this.money = money - bet_money;
                    placeABet(ThreadLocalRandom.current().nextInt(0, NUM_HORSES), bet_money); // spectators make a bet
                    setSpectatorState(PLACING_A_BET);
                    writeSpecSt(PLACING_A_BET);
                    makeLogger();
                    break;

                case PLACING_A_BET:
                    System.out.println("PLACING_A_BET, SPECTATOR " + spec_id);
                    goWatchTheRace(); // spectators go watch the race
                    setSpectatorState(WATCHING_A_RACE);
                    writeSpecSt(WATCHING_A_RACE);
                    makeLogger();
                    break;

                case WATCHING_A_RACE:
                    System.out.println("WATCHING_A_RACE, SPECTATOR " + spec_id);
                    
                    race_number++;
                    if(haveIWon()) { // spectator know if they have won after the broker have reported the results
                        System.out.println("haveIWon() SPECTATOR: " + this.spec_id);
                        this.money += goCollectTheGains();
                        setSpectatorState(COLLECTING_THE_GAINS);
                        writeSpecSt(COLLECTING_THE_GAINS);
                        makeLogger();
                    }
                    else if(race_number != NUM_RACES) {
                        waitForNextRace();
                        setSpectatorState(WAITING_FOR_A_RACE_TO_START);
                        writeSpecSt(WAITING_FOR_A_RACE_TO_START);
                        makeLogger();
                    }
                    else {
                        relaxABit();
                        setSpectatorState(CELEBRATING);
                        writeSpecSt(CELEBRATING);
                        makeLogger();
                    }
                    break;
                    
                case COLLECTING_THE_GAINS:
                    System.out.println("COLLECTING_THE_GAINS, SPECTATOR " + spec_id);
                    
                    if(race_number!=NUM_RACES) {
                        waitForNextRace();
                        setSpectatorState(WAITING_FOR_A_RACE_TO_START);
                        writeSpecSt(WAITING_FOR_A_RACE_TO_START);
                        makeLogger();
                        
                    }
                    else {
                        relaxABit();
                        setSpectatorState(CELEBRATING);
                        writeSpecSt(CELEBRATING);
                        makeLogger();
                    }
                    break;
                    
                case CELEBRATING:
                    System.out.println("CELEBRATING, SPECTATOR " + spec_id);
                    celebrating = true;
                    break;
                
                default:
                    break;
            }

        }
       
    }
}