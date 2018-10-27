package AfternoonRacesRemote.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface that defines the operations available over the objects that
 * represents the Control Center
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public interface ControlCenterInterface extends Remote {
    
    public void goWatchTheRace(int spec_id) throws RemoteException;
    
    public void reportResults() throws RemoteException;
    
    public boolean areThereAnyWinners() throws RemoteException;
    
    public boolean haveIwon(int spec_id) throws RemoteException;
    
    public void entertainTheGuests() throws RemoteException;
    
    public void relaxABit(int spec_id) throws RemoteException;
    
    public void signalShutdown() throws RemoteException;
    
}
