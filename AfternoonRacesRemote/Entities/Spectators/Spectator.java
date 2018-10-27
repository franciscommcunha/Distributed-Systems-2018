package AfternoonRacesRemote.Entities.Spectators;

import java.util.concurrent.ThreadLocalRandom;

import static AfternoonRacesRemote.Constants.Constants.*;
import AfternoonRacesRemote.Entities.Spectators.SpectatorsEnum.SpectatorStates;
import static AfternoonRacesRemote.Entities.Spectators.SpectatorsEnum.SpectatorStates.*;

import AfternoonRacesRemote.interfaces.BettingCenterInterface;
import AfternoonRacesRemote.interfaces.ControlCenterInterface;
import AfternoonRacesRemote.interfaces.GeneralRepositoryInterface;
import AfternoonRacesRemote.interfaces.PaddockInterface;
import AfternoonRacesRemote.interfaces.RacingTrackInterface;
import java.rmi.RemoteException;

/*
 * @author João Amaral
 * @author Francisco Cunha
*/
public class Spectator extends Thread{
	
    private final int spec_id;
    private int money;
    
    SpectatorStates state;
    
    private final BettingCenterInterface bettingCenterInterface;
    private final ControlCenterInterface controlCenterInterface;
    private final PaddockInterface paddockInterface;
    private final GeneralRepositoryInterface generalRepositoryInterface;
    
    /**
     * Spectator entity of the problem. Initialized with the given id and money value
     * Initializes the communication channels used in order to communicate
     * with the problem server monitors
     * 
     * @param spec_id integer value of the id of the spectator
     * @param money integer value representing the money of the spectator
     * @param bci represents the Betting center interface
     * @param cci represents the Control center interface
     * @param pi represents the Paddock interface
     * @param gri represents the General Repository interface
     */
    public Spectator(int spec_id, int money, BettingCenterInterface bci, ControlCenterInterface cci,
            PaddockInterface pi, GeneralRepositoryInterface gri) {
        this.spec_id = spec_id;
	this.money = money;     
        
        this.bettingCenterInterface = bci;
        this.controlCenterInterface = cci;
        this.paddockInterface = pi;
        this.generalRepositoryInterface = gri;
        
        this.state = SpectatorStates.OUTSIDE_HIPPIC_CENTER;
    }
    
    private void initLog() { 
        try {
            generalRepositoryInterface.initLog();
        } 
        catch (RemoteException e) {
            System.err.println("Excepção na invocação remota de método" + getName() + ": " + e.getMessage() + "!");
            System.exit(1);
        }
    }
    
    private void setSpectatorState(SpectatorStates state) {
        try {
            generalRepositoryInterface.setSpectatorState(state, this.spec_id);
        } 
        catch (RemoteException e) {
            System.err.println("Excepção na invocação remota de método" + getName() + ": " + e.getMessage() + "!");
            System.exit(1);
        }
    }
    
    private void writeSpecSt(SpectatorStates state) {
        try {
            generalRepositoryInterface.writeSpecSt(state);
        } 
        catch (RemoteException e) {
            System.err.println("Excepção na invocação remota de método" + getName() + ": " + e.getMessage() + "!");
            System.exit(1);
        }
    }
    
    private void makeLogger() {
        try {
            generalRepositoryInterface.makeLogger();
        } 
        catch (RemoteException e) {
            System.err.println("Excepção na invocação remota de método" + getName() + ": " + e.getMessage() + "!");
            System.exit(1);
        }
    }
    
    private SpectatorStates getSpectatorState() {
        try {
            this.state = generalRepositoryInterface.getSpectatorState(this.spec_id);
        } 
        catch (RemoteException e) {
            System.err.println("Excepção na invocação remota de método" + getName() + ": " + e.getMessage() + "!");
            System.exit(1);
        }
        
        return this.state;
    }
    
    private void waitForNextRace() {
        try {
            paddockInterface.waitForNextRace(this.spec_id);
        } 
        catch (RemoteException e) {
            System.err.println("Excepção na invocação remota de método" + getName() + ": " + e.getMessage() + "!");
            System.exit(1);
        }
    }
    
    private void goCheckHorses() { 
        try {
            paddockInterface.goCheckHorses(spec_id);
        } 
        catch (RemoteException e) {
            System.err.println("Excepção na invocação remota de método" + getName() + ": " + e.getMessage() + "!");
            System.exit(1);
        }
    }
    
    private void placeABet(int horse_id, int bet_money) {
        try {
            bettingCenterInterface.placeABet(this.spec_id, horse_id, bet_money);
        } 
        catch (RemoteException e) {
            System.err.println("Excepção na invocação remota de método" + getName() + ": " + e.getMessage() + "!");
            System.exit(1);
        }
    }
    
    private void goWatchTheRace() {
        try {
            controlCenterInterface.goWatchTheRace(this.spec_id);
        } 
        catch (RemoteException e) {
            System.err.println("Excepção na invocação remota de método" + getName() + ": " + e.getMessage() + "!");
            System.exit(1);
        }
    }
    
    private boolean haveIWon() {
        boolean won = false;
        
        try {
            won = controlCenterInterface.haveIwon(this.spec_id);
        } 
        catch (RemoteException e) {
            System.err.println("Excepção na invocação remota de método" + getName() + ": " + e.getMessage() + "!");
            System.exit(1);
        }
        
        return won;
    }
    
    private int goCollectTheGains() {
        int gain = 0;
        
        try {
            bettingCenterInterface.goCollectTheGains(this.spec_id);
        } 
        catch (RemoteException e) {
            System.err.println("Excepção na invocação remota de método" + getName() + ": " + e.getMessage() + "!");
            System.exit(1);
        }
        
        return gain;
    }
    
    private void relaxABit() {
        try {
            controlCenterInterface.relaxABit(this.spec_id);
        } 
        catch (RemoteException e) {
            System.err.println("Excepção na invocação remota de método" + getName() + ": " + e.getMessage() + "!");
            System.exit(1);
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
                
                case OUTSIDE_HIPPIC_CENTER:
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
                    // have i won do control center
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