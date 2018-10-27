package AfternoonRacesRemote.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface that defines the operations available over the objects that
 * represents the Betting Center
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public interface BettingCenterInterface extends Remote {
    
    public void acceptTheBets() throws RemoteException;
    
    public void placeABet(int spec_id, int horse_id, int money) throws RemoteException;
    
    public void honourTheBets() throws RemoteException;
    
    public double goCollectTheGains(int spec_id) throws RemoteException;
    
    public void signalShutdown() throws RemoteException;
    
}
