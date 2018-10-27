package AfternoonRacesRemote.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface that defines the operations available over the objects that
 * represents the Racing Track
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public interface RacingTrackInterface extends Remote {
    
    public void startTheRace() throws RemoteException;
    
    public void makeAMove(int horse_id, int move_unit) throws RemoteException;
    
    public boolean hasFinishLineBeenCrossed(int horse_id) throws RemoteException;
    
    public void signalShutdown() throws RemoteException;
    
}
