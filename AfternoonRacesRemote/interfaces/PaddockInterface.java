package AfternoonRacesRemote.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface that defines the operations available over the objects that
 * represents the Paddock
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public interface PaddockInterface extends Remote {
    
    public void summonHorsesToPaddock() throws RemoteException;
    
    public void proceedToPaddock(int horse_id) throws RemoteException;
    
    public void waitForNextRace(int spec_id) throws RemoteException;
    
    public void goCheckHorses(int spec_id) throws RemoteException;
    
    public void proceedToStartLine(int horse_id) throws RemoteException;
    
    public void signalShutdown() throws RemoteException;
}
