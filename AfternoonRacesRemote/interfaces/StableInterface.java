package AfternoonRacesRemote.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface that defines the operations available over the objects that
 * represent the Stable Monitor
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public interface StableInterface extends Remote {
    
    public void proceedToStable(int horse_id, int race_number) throws RemoteException;
    
    public void summonHorsesToPaddock() throws RemoteException;
    
    public void signalShutdown() throws RemoteException;
    
}
