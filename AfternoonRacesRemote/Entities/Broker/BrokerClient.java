package AfternoonRacesRemote.Entities.Broker;

import AfternoonRacesRemote.interfaces.BettingCenterInterface;
import AfternoonRacesRemote.interfaces.ControlCenterInterface;
import AfternoonRacesRemote.interfaces.PaddockInterface;
import AfternoonRacesRemote.interfaces.RacingTrackInterface;
import AfternoonRacesRemote.interfaces.StableInterface;
import AfternoonRacesRemote.interfaces.GeneralRepositoryInterface;
import AfternoonRacesRemote.Registry.RegistryConfig;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/** 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class BrokerClient {
    public static void main(String[] args) throws RemoteException {
        
        // Initialize RMI Configurations
        
        String rmiRegHostName = args[0];
        int rmiRegPortNumb = Integer.parseInt(args[1]);
        
        /*
        String rmiRegHostName = "localhost";
        int rmiRegPortNumb = 1099;
        */
     
        Registry registry = null;
        
        // Initialize RMI Invocations
        GeneralRepositoryInterface generalRepositoryInterface = null;
        BettingCenterInterface bettingCenterInterface = null;
        ControlCenterInterface controlCenterInterface = null;
        PaddockInterface paddockInterface = null;
        RacingTrackInterface racingTrackInterface = null;
        StableInterface stableInterface = null;
        
        try {
            registry = LocateRegistry.getRegistry(rmiRegHostName, rmiRegPortNumb);
        } catch (RemoteException e) {
            System.out.println("RMI registry creation exception: " + e.getMessage());
            System.exit(1);
        }
        
        //localização por nome do objecto remoto no serviço de registos RMI
        try {
            generalRepositoryInterface = (GeneralRepositoryInterface)
                    registry.lookup(RegistryConfig.RMI_REGISTRY_GENREPO_NAME);

        } catch (RemoteException e) {
            System.out.println("Excepção na localização do General Repository: " + e.getMessage() + "!");
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("O General Repository não está registada: " + e.getMessage() + "!");
            System.exit(1);
        }
         
        try {
            bettingCenterInterface = (BettingCenterInterface)
                    registry.lookup(RegistryConfig.RMI_REGISTRY_BETTINGCENTER_NAME);

        } catch (RemoteException e) {
            System.out.println("Excepção na localização do Betting Center: " + e.getMessage() + "!");
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("O Betting Center não está registada: " + e.getMessage() + "!");
            System.exit(1);
        }
        
        try {
            controlCenterInterface = (ControlCenterInterface)
                    registry.lookup(RegistryConfig.RMI_REGISTRY_CONTROLCENTER_NAME);

        } catch (RemoteException e) {
            System.out.println("Excepção na localização do Control Center: " + e.getMessage() + "!");
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("O Control Center não está registada: " + e.getMessage() + "!");
            System.exit(1);
        }
        
        try {
            paddockInterface = (PaddockInterface)
                    registry.lookup(RegistryConfig.RMI_REGISTRY_PADDOCK_NAME);

        } catch (RemoteException e) {
            System.out.println("Excepção na localização do Paddock: " + e.getMessage() + "!");
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("O Paddock não está registada: " + e.getMessage() + "!");
            System.exit(1);
        }
        
        try {
            racingTrackInterface = (RacingTrackInterface)
                    registry.lookup(RegistryConfig.RMI_REGISTRY_RACINGTRACK_NAME);

        } catch (RemoteException e) {
            System.out.println("Excepção na localização do Racing Track: " + e.getMessage() + "!");
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("O Racing Track não está registada: " + e.getMessage() + "!");
            System.exit(1);
        }
        
        try {
            stableInterface = (StableInterface)
                    registry.lookup(RegistryConfig.RMI_REGISTRY_STABLE_NAME);

        } catch (RemoteException e) {
            System.out.println("Excepção na localização do Stable: " + e.getMessage() + "!");
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("O Stable não está registada: " + e.getMessage() + "!");
            System.exit(1);
        }
        
        Broker broker = new Broker(bettingCenterInterface, controlCenterInterface, paddockInterface, racingTrackInterface, stableInterface, generalRepositoryInterface);
        System.out.println("Broker has started his job");
        broker.start();
        
        try{
            broker.join();
            System.out.println("Broker has finished his job");
        }
        catch(InterruptedException e) {
            System.out.println("ERROR: Broker has finished his job");
        }
        
        try {
            generalRepositoryInterface.finished();
        } catch (RemoteException ex) {
            System.out.println("Error closing all!");
            System.exit(1);
        }

        System.out.println("Broker Done!");
        
    }
}