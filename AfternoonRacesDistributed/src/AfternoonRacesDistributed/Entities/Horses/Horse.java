package AfternoonRacesDistributed.Entities.Horses;

import AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelClient;
import static AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelPorts.*;
import AfternoonRacesDistributed.Monitors.GeneralRepository.GeneralRepository;
import AfternoonRacesDistributed.Entities.Horses.HorsesInterfaces.*;
import static AfternoonRacesDistributed.Constants.Constants.*;
import AfternoonRacesDistributed.Entities.Horses.HorsesEnum.HorseStates;
import static AfternoonRacesDistributed.Entities.Horses.HorsesEnum.HorseStates.*;
import AfternoonRacesDistributed.Messages.PaddockMessage.PaddockMessage;
import AfternoonRacesDistributed.Messages.RepositoryMessage.RepositoryMessage;
import AfternoonRacesDistributed.Messages.StableMessage.StableMessage;
import AfternoonRacesDistributed.Messages.RacingTrackMessage.RacingTrackMessage;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Active class representing the horses
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class Horse extends Thread{
	
    private CommunicationChannelClient cc_stable;
    private CommunicationChannelClient cc_paddock;
    private CommunicationChannelClient cc_racing_track; 
    private CommunicationChannelClient cc_repository;

    private int horse_id;
    private int agility;

    /**
     * Horse entity of the problem. Initialized with the given id and agility values
     * Initializes the communication channels used in order to communicate
     * with the problem server monitors
     * 
     * @param horse_id integer value identifying the horse
     * @param agility integer value correspondent to the agility of the horse
     */
    public Horse(int horse_id, int agility) {
       this.horse_id = horse_id;
       this.agility = agility;
       this.cc_stable = new CommunicationChannelClient(NAME_STABLE, PORT_STABLE);
       this.cc_paddock = new CommunicationChannelClient(NAME_PADDOCK, PORT_PADDOCK);
       this.cc_racing_track = new CommunicationChannelClient(NAME_RACING_TRACK, PORT_RACING_TRACK);
       this.cc_repository = new CommunicationChannelClient(NAME_GENERAL_REPOSITORY, PORT_GENERAL_REPOSITORY);
    }
    
    /**
     * Sends message to repository and retrieves the current state of the horse
     * @return The horse state contained in the message
     */
    private HorseStates getHorseState(){
        RepositoryMessage response;
        openComm(cc_repository, "Horse " + this.horse_id + ": Repository");
        cc_repository.writeObject(new RepositoryMessage(RepositoryMessage.HORSE_STATE, this.horse_id));
        response = (RepositoryMessage) cc_repository.readObject(); 
        return response.getHorseState();
    }

    /**
     * Sends RepositoryMessage to General Repository server to update the horse internal state to state
     * @param state Next state of the horse
     */
    private void setHorseState(HorseStates state){
        RepositoryMessage response;
        openComm(cc_repository, "Horse " + this.horse_id + ": Repository");
        cc_repository.writeObject(new RepositoryMessage(RepositoryMessage.SET_HORSE_STATE,state, this.horse_id));
        response = (RepositoryMessage) cc_repository.readObject();
        cc_repository.close(); 
    }
    
    /**
     * Sends INIT_LOG message to General Repository server and receives respective response
     */
    private void initLog() {
        RepositoryMessage response;
        openComm(cc_repository, "Horse " + this.horse_id + ": Repository");
        cc_repository.writeObject(new RepositoryMessage(RepositoryMessage.INIT_LOG));
        response = (RepositoryMessage) cc_repository.readObject(); 
        cc_repository.close();
    }
    
    /**
     * Sends MAKE_LOGGER message to General Repository server and receives respective response
     */
    private void makeLogger() {
        RepositoryMessage response;
        openComm(cc_repository, "Horse " + this.horse_id + ": Repository");
        cc_repository.writeObject(new RepositoryMessage(RepositoryMessage.MAKE_LOGGER));
        response = (RepositoryMessage) cc_repository.readObject(); 
        cc_repository.close(); 
    }
    
    /**
     * Sends WRITE_HORSE_ST and state of the horse message to General Repository server and receives respective response
     * @param state state of the horse
     */
    private void writeHorseSt(HorseStates state) {
        RepositoryMessage response;
        openComm(cc_repository, "Horse " + this.horse_id + ": Repository");
        cc_repository.writeObject(new RepositoryMessage(RepositoryMessage.WRITE_HORSE_ST, state));
        response = (RepositoryMessage) cc_repository.readObject(); 
        cc_repository.close(); 
    }
    
    /**
     * Sends CLOSE_WRITTER message to General Repository server and receives respective response
     */
    private void closeWriter() {
        RepositoryMessage response;
        openComm(cc_repository, "Horse " + this.horse_id + ": Repository");
        cc_repository.writeObject(new RepositoryMessage(RepositoryMessage.CLOSE_WRITTER));
        response = (RepositoryMessage) cc_repository.readObject(); 
        cc_repository.close();
    }
    
    /**
     * Sends PROCEED_TO_STABLE message to stable server and receives respective response
     * @param horse_id
     * @param race_number
     */
    private void proceedToStable(int horse_id, int race_number){  
        StableMessage response;
        openComm(cc_stable, "Horse " + this.horse_id + ": Stable");
        cc_stable.writeObject(new StableMessage(StableMessage.PROCEED_TO_STABLE, this.horse_id, race_number));
        response = (StableMessage) cc_stable.readObject();
        cc_stable.close();
    }
    
    /**
     * Sends PROCEED_TO_PADDOCK message to paddock server and receives respective response
     */
    private void proceedToPaddock() {  
        PaddockMessage response;
        openComm(cc_paddock, "Horse " + this.horse_id + ": Paddock");
        cc_paddock.writeObject(new PaddockMessage(PaddockMessage.PROCEED_TO_PADDOCK, this.horse_id));
        response = (PaddockMessage) cc_paddock.readObject();
        cc_paddock.close();
    }   
    
    /**
     * Sends PROCEED_TO_START_LINE message to Racing Track server and receives respective response
     */
    private void proceedToStartLine(){
        PaddockMessage response;
        openComm(cc_paddock, "Horse " + this.horse_id + ": Paddock");
        cc_paddock.writeObject(new PaddockMessage(PaddockMessage.PROCEED_TO_START_LINE, this.horse_id));
        response = (PaddockMessage) cc_paddock.readObject(); 
        cc_paddock.close(); 
    }
    
    /**
     * Sends MAKE_A_MOVE message to Racing Track server and receives respective response
     * @param move_unit
     */
    private void makeAMove(int move_unit) {   
        RacingTrackMessage response;    
        openComm(cc_racing_track, "Horse " + this.horse_id + ": Racing Track");
        cc_racing_track.writeObject(new RacingTrackMessage(RacingTrackMessage.MAKE_A_MOVE, this.horse_id, move_unit));
        response = (RacingTrackMessage) cc_racing_track.readObject();
        cc_racing_track.close();
    }
    
    /**
     * Sends HAS_FINISH_LINE_BEEN_CROSSED message to Racing Track server and receives respective response
     */
    private boolean hasFinishLineBeenCrossed() {   
        RacingTrackMessage response;
        openComm(cc_racing_track, "Racing Track");
        cc_racing_track.writeObject(new RacingTrackMessage(RacingTrackMessage.HAS_FINISHED_LINE_BEEN_CROSSED, this.horse_id));
        response = (RacingTrackMessage) cc_racing_track.readObject();
        cc_racing_track.close(); 
        return response.getHasFinished();
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
    public void run()
    {	
        int race_number = 0;
        boolean event_ended = false;

        System.out.println(getHorseState()+", HORSE " + this.horse_id);

        //initLog();
        writeHorseSt(getHorseState());
        makeLogger();

        /* horse/jockey pair main lifecycle */
        while(!event_ended) {
            switch(getHorseState()) {

                case AT_THE_FIELDS : 
                    proceedToStable(this.horse_id, race_number); // horse proceeds to stable at the start of the race waking up the broker
                    setHorseState(AT_THE_STABLE);
                    writeHorseSt(AT_THE_STABLE);
                    makeLogger();             
                    break;

                case AT_THE_STABLE : 
                    System.out.println("AT_THE_STABLE, HORSE " + horse_id);  
                    if(race_number == NUM_RACES){ 
                        event_ended = true;
                        makeLogger();
                        break;
                    }
                    proceedToPaddock();
                    setHorseState(AT_THE_PADDOCK);
                    writeHorseSt(AT_THE_PADDOCK);
                    makeLogger();
                    break;

                case AT_THE_PADDOCK : 
                    System.out.println("AT_THE_PADDOCK, HORSE " + horse_id);  
                    proceedToStartLine(); // horse proceeds to start line after all the spectators have checked them
                    setHorseState(AT_THE_START_LINE);
                    writeHorseSt(AT_THE_START_LINE);
                    makeLogger();
                    break;

                case AT_THE_START_LINE : 
                    System.out.println("AT_THE_START_LINE, HORSE " + horse_id);
                    makeAMove(ThreadLocalRandom.current().nextInt(1, this.agility+1)); // horses start the race
                    setHorseState(RUNNING);
                    writeHorseSt(RUNNING);
                    makeLogger();
                    break;

                case RUNNING:
                    System.out.println("RUNNING, HORSE " + horse_id);
                    if(!hasFinishLineBeenCrossed()) { // horses moving while finish line has not been crossed                     
                        makeAMove(ThreadLocalRandom.current().nextInt(1, this.agility+1));
                        makeLogger();
                    }
                    else {
                        setHorseState(AT_THE_FINISH_LINE);
                        writeHorseSt(AT_THE_FINISH_LINE);
                        makeLogger();
                    }
                    break;
                    
                case AT_THE_FINISH_LINE : 
                    System.out.println("AT_THE_FINISH_LINE, HORSE: "+horse_id);
                    race_number++;
                    proceedToStable(horse_id, race_number);
                    setHorseState(AT_THE_STABLE);                    
                    writeHorseSt(AT_THE_STABLE);
                    makeLogger();
                    break;
   
                default :
                    break;
            }
        }

        closeWriter();

    }
}
