package AfternoonRacesRemote.interfaces;

import AfternoonRacesRemote.Entities.Broker.BrokerEnum.BrokerStates;
import AfternoonRacesRemote.Entities.Horses.HorsesEnum.HorseStates;
import AfternoonRacesRemote.Entities.Spectators.SpectatorsEnum.SpectatorStates;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * Interface that defines the operations available over the objects that
 * represents the General Repository
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public interface GeneralRepositoryInterface extends Remote {
    
    public BrokerStates getBrokerState() throws RemoteException;
    
    public HorseStates getHorseState(int horse_id) throws RemoteException;
    
    public SpectatorStates getSpectatorState(int spec_id) throws RemoteException;
    
    public void setBrokerState(BrokerStates state) throws RemoteException;
    
    public void setBetInfo(int spec_id, int[] info) throws RemoteException;
    
    public void setHorseState(HorseStates state, int id) throws RemoteException;
    
    public void setSpectatorState(SpectatorStates state, int id) throws RemoteException;
    
    public void addHorseResult(int[] result) throws RemoteException;
    
    public int[] removeHorseResult() throws RemoteException;
    
    public int[] peekHorseResult() throws RemoteException;
    
    public int getHorseResultSize() throws RemoteException;
    
    public void addHorseWinner(int horse_id) throws RemoteException;
    
    public Map<Integer,int[]> getBetInfoReferenceCopy() throws RemoteException;
    
    public boolean horseIsWinner(int horse_id) throws RemoteException;
    
    public void addWinningSpectator(int spec_id) throws RemoteException;
    
    public boolean spectatorIsWinner(int spec_id) throws RemoteException;
    
    public int getWinningSpectatorsSize() throws RemoteException;
    
    public void clearBetInfo() throws RemoteException;
    
    public void clearHorseWinners() throws RemoteException;
    
    public void clearWinningSpectators() throws RemoteException;
    
    public void initLog() throws RemoteException;
    
    public void writeStatB(BrokerStates state) throws RemoteException;
    
    public void writeHorseSt(HorseStates state) throws RemoteException;
    
    public void writeSpecSt(SpectatorStates state) throws RemoteException;
    
    public void makeLogger() throws RemoteException;
    
    public void closeWriter() throws RemoteException;
    
    public void incRaceIteration() throws RemoteException;
    
    public void filterZeroAmountBets() throws RemoteException;
   
    /**
     * Function that flags the end of the program
     *
     * @throws RemoteException may throw during a execution of a remote method call
     */
    public void finished() throws RemoteException;
       
}