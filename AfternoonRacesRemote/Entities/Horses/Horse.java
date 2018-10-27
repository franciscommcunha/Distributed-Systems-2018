package AfternoonRacesRemote.Entities.Horses;

import static AfternoonRacesRemote.Constants.Constants.*;
import AfternoonRacesRemote.Entities.Horses.HorsesEnum.HorseStates;
import static AfternoonRacesRemote.Entities.Horses.HorsesEnum.HorseStates.*;

import AfternoonRacesRemote.interfaces.GeneralRepositoryInterface;
import AfternoonRacesRemote.interfaces.PaddockInterface;
import AfternoonRacesRemote.interfaces.RacingTrackInterface;
import AfternoonRacesRemote.interfaces.StableInterface;
import java.rmi.RemoteException;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Active class representing the horses
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class Horse extends Thread{
	
    private int horse_id;
    private int agility;
    
    private final PaddockInterface paddockInterface;
    private final RacingTrackInterface racingTrackInterface;
    private final StableInterface stableInterface;
    private final GeneralRepositoryInterface generalRepositoryInterface;
    
    private HorseStates state;

    /**
     * Horse entity of the problem. Initialized with the given id and agility values
     * Initializes the communication channels used in order to communicate
     * with the problem server monitors
     * 
     * @param horse_id integer value identifying the horse
     * @param agility integer value correspondent to the agility of the horse
     * @param si represents the Stable interface
     * @param pi represents the Paddock interface
     * @param rti represents the Racing Track interface
     * @param gri represents the Genereal Repository interface
     */
    public Horse(int horse_id, int agility, StableInterface si, PaddockInterface pi, RacingTrackInterface rti, 
    GeneralRepositoryInterface gri) {
        
       this.horse_id = horse_id;
       this.agility = agility;
       
       this.paddockInterface = pi;
       this.racingTrackInterface = rti;
       this.stableInterface = si;
       this.generalRepositoryInterface = gri;
       
       this.state = AT_THE_FIELDS;
    }
    
    private void writeHorseSt(HorseStates state) {
        try {
            generalRepositoryInterface.writeHorseSt(state);
        }
        catch(RemoteException e) {
            System.err.println("Excepção na invocação remota de método" + getName() + ": " + e.getMessage() + "!");
            System.exit(1);
        }
    }
    
    private void setHorseState(HorseStates state, int horse_id) {
        try {
            generalRepositoryInterface.setHorseState(state, horse_id);
        }
        catch(RemoteException e) {
            System.err.println("Excepção na invocação remota de método" + getName() + ": " + e.getMessage() + "!");
            System.exit(1);
        }
    }
    
    private HorseStates getHorseState() {
        try {
            this.state = generalRepositoryInterface.getHorseState(this.horse_id);
        }
        catch(RemoteException e) {
            System.err.println("Excepção na invocação remota de método" + getName() + ": " + e.getMessage() + "!");
            System.exit(1);
        }
        return this.state;
    }
    
    private void makeLogger() {
        try {
            generalRepositoryInterface.makeLogger();
        }
        catch(RemoteException e) {
            System.err.println("Excepção na invocação remota de método" + getName() + ": " + e.getMessage() + "!");
            System.exit(1);
        }
    }
    
    private void closeWriter() {
        try {
            generalRepositoryInterface.closeWriter();
        }
        catch(RemoteException e) {
            System.err.println("Excepção na invocação remota de método" + getName() + ": " + e.getMessage() + "!");
            System.exit(1);
        }
    }
    
    private void proceedToStable(int horse_id, int race_number) {
        try {
           stableInterface.proceedToStable(horse_id, race_number);
        }
        catch(RemoteException e) {
            System.err.println("Excepção na invocação remota de método" + getName() + ": " + e.getMessage() + "!");
            System.exit(1);
        }
    }
    
    private void proceedToPaddock() {
        try {
            paddockInterface.proceedToPaddock(horse_id);
        }
        catch(RemoteException e) {
            System.err.println("Excepção na invocação remota de método" + getName() + ": " + e.getMessage() + "!");
            System.exit(1);
        }
    }
    
    private void proceedToStartLine() {
        try {
            paddockInterface.proceedToStartLine(horse_id);
        }
        catch(RemoteException e) {
            System.err.println("Excepção na invocação remota de método" + getName() + ": " + e.getMessage() + "!");
            System.exit(1);
        }
    }
    
    private void makeAMove(int move_unit) {
        try {
            racingTrackInterface.makeAMove(this.horse_id, move_unit);
        }
        catch(RemoteException e) {
            System.err.println("Excepção na invocação remota de método" + getName() + ": " + e.getMessage() + "!");
            System.exit(1);
        }
    }
    
    private boolean hasFinishLineBeenCrossed() {
        boolean finished = false;
        
        try {
            finished = racingTrackInterface.hasFinishLineBeenCrossed(this.horse_id);
        }
        catch(RemoteException e) {
            System.err.println("Excepção na invocação remota de método" + getName() + ": " + e.getMessage() + "!");
            System.exit(1);
        }
        
        return finished;
    }
    
    @Override
    public void run()
    {	
        int race_number = 0;
        boolean event_ended = false;

        System.out.println(getHorseState()+", HORSE " + this.horse_id);

        writeHorseSt(getHorseState());
        makeLogger();

        /* horse/jockey pair main lifecycle */
        while(!event_ended) {
            switch(getHorseState()) {

                case AT_THE_FIELDS : 
                    proceedToStable(this.horse_id, race_number); // horse proceeds to stable at the start of the race waking up the broker
                    setHorseState(AT_THE_STABLE, this.horse_id);
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
                    setHorseState(AT_THE_PADDOCK, this.horse_id);
                    writeHorseSt(AT_THE_PADDOCK);
                    makeLogger();
                    break;

                case AT_THE_PADDOCK : 
                    System.out.println("AT_THE_PADDOCK, HORSE " + horse_id);  
                    proceedToStartLine(); // horse proceeds to start line after all the spectators have checked them
                    setHorseState(AT_THE_START_LINE, this.horse_id);
                    writeHorseSt(AT_THE_START_LINE);
                    makeLogger();
                    break;

                case AT_THE_START_LINE : 
                    System.out.println("AT_THE_START_LINE, HORSE " + horse_id);
                    makeAMove(ThreadLocalRandom.current().nextInt(1, this.agility+1)); // horses start the race
                    setHorseState(RUNNING, this.horse_id);
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
                        setHorseState(AT_THE_FINISH_LINE, this.horse_id);
                        writeHorseSt(AT_THE_FINISH_LINE);
                        makeLogger();
                    }
                    break;
                    
                case AT_THE_FINISH_LINE : 
                    System.out.println("AT_THE_FINISH_LINE, HORSE: "+horse_id);
                    race_number++;
                    proceedToStable(horse_id, race_number);
                    setHorseState(AT_THE_STABLE, this.horse_id);                    
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