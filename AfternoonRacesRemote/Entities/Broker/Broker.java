package AfternoonRacesRemote.Entities.Broker;

import static AfternoonRacesRemote.Constants.Constants.*;
import AfternoonRacesRemote.Entities.Broker.BrokerEnum.BrokerStates;
import static AfternoonRacesRemote.Entities.Broker.BrokerEnum.BrokerStates.*;

import AfternoonRacesRemote.interfaces.BettingCenterInterface;
import AfternoonRacesRemote.interfaces.ControlCenterInterface;
import AfternoonRacesRemote.interfaces.GeneralRepositoryInterface;
import AfternoonRacesRemote.interfaces.PaddockInterface;
import AfternoonRacesRemote.interfaces.RacingTrackInterface;
import AfternoonRacesRemote.interfaces.StableInterface;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Active class representing the Broker entity
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class Broker extends Thread {
    
    private final GeneralRepositoryInterface generalRepositoryInterface;
    private final BettingCenterInterface bettingCenterInterface;
    private final ControlCenterInterface controlCenterInterface;
    private final PaddockInterface paddockInterface;
    private final RacingTrackInterface racingTrackInterface;
    private final StableInterface stableInterface;
    
    private BrokerStates state;

    /**
     * Broker entity of the problem. Created with already initalized communications classes 
     * in order to communicate with the problem server monitors
     * 
     * @param bci represents the Betting center interface
     * @param cci represents the Control center interface
     * @param pi represents the Paddock interface
     * @param rti represents the Racing Track interface
     * @param si represents the Stable interface
     * @param gri represents the General Repository interface
     */
    public Broker(BettingCenterInterface bci, ControlCenterInterface cci,PaddockInterface pi, RacingTrackInterface rti,
            StableInterface si, GeneralRepositoryInterface gri) {
        
        this.bettingCenterInterface = bci;
        this.controlCenterInterface = cci;
        this.paddockInterface = pi;
        this.racingTrackInterface = rti;
        this.stableInterface = si;
        this.generalRepositoryInterface = gri;
        
        this.state = OPENING_THE_EVENT;
    }
    
    private void initLog() {
        try {
            generalRepositoryInterface.initLog();
        }
        catch(RemoteException e) {
            System.err.println("Excepção na invocação remota de método" + getName() + ": " + e.getMessage() + "!");
            System.exit(1);
        }
    }
    
    private void writeStatB(BrokerStates state) {
        try {
            generalRepositoryInterface.writeStatB(state);
        }
        catch(RemoteException e) {
            System.err.println("Excepção na invocação remota de método" + getName() + ": " + e.getMessage() + "!");
            System.exit(1);
        }
    }
    
    private void setBrokerState(BrokerStates state) {
        try {
            generalRepositoryInterface.setBrokerState(state);
        }
        catch(RemoteException e) {
            System.err.println("Excepção na invocação remota de método" + getName() + ": " + e.getMessage() + "!");
            System.exit(1);
        }
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
    
    private BrokerStates getBrokerState() {
        try {
            this.state = generalRepositoryInterface.getBrokerState();
        }
        catch(RemoteException e) {
            System.err.println("Excepção na invocação remota de método" + getName() + ": " + e.getMessage() + "!");
            System.exit(1);
        }
        
        return this.state;
    }
    
    private void summonHorsesToPaddockAtStable() {
        try {
            stableInterface.summonHorsesToPaddock();
        } 
        catch (RemoteException e) {
            System.err.println("Excepção na invocação remota de método" + getName() + ": " + e.getMessage() + "!");
            System.exit(1);
        }
    }
    
    private void summonHorsesToPaddockAtPaddock() {
        try { 
            paddockInterface.summonHorsesToPaddock();
        }
        catch (RemoteException e) {
            System.err.println("Excepção na invocação remota de método" + getName() + ": " + e.getMessage() + "!");
            System.exit(1);
        }
    }
    
    private void acceptTheBets() {
        try { 
            bettingCenterInterface.acceptTheBets();
        }
        catch (RemoteException e) {
            System.err.println("Excepção na invocação remota de método" + getName() + ": " + e.getMessage() + "!");
            System.exit(1);
        }
    }
    
    private void startTheRace() {
        try { 
            racingTrackInterface.startTheRace();
        }
        catch (RemoteException e) {
            System.err.println("Excepção na invocação remota de método" + getName() + ": " + e.getMessage() + "!");
            System.exit(1);
        }
    }
    
    private void reportResults() {
        try { 
            controlCenterInterface.reportResults();
        }
        catch (RemoteException e) {
            System.err.println("Excepção na invocação remota de método" + getName() + ": " + e.getMessage() + "!");
            System.exit(1);
        }
    }
    
    private boolean areThereAnyWinners() {
        boolean winners = false;
        try { 
            winners = controlCenterInterface.areThereAnyWinners();
        }
        catch (RemoteException e) {
            System.err.println("Excepção na invocação remota de método" + getName() + ": " + e.getMessage() + "!");
            System.exit(1);
        }
        
        return winners;
    }
    
    private void honourTheBets() {
        try { 
            bettingCenterInterface.honourTheBets();
        }
        catch (RemoteException e) {
            System.err.println("Excepção na invocação remota de método" + getName() + ": " + e.getMessage() + "!");
            System.exit(1);
        }
    }
    
    private void entertainTheGuests() {
        try { 
            controlCenterInterface.entertainTheGuests();
        }
        catch (RemoteException e) {
            System.err.println("Excepção na invocação remota de método" + getName() + ": " + e.getMessage() + "!");
            System.exit(1);
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

                case OPENING_THE_EVENT:
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
                    //endExecution();
                    event_finished = true;
                    break;
                
                default :
                    break;
            }
        }
    }
}