package AfternoonRacesRemote.Entities.Spectators;

import AfternoonRacesRemote.interfaces.BettingCenterInterface;
import AfternoonRacesRemote.interfaces.ControlCenterInterface;
import AfternoonRacesRemote.interfaces.PaddockInterface;
import AfternoonRacesRemote.interfaces.GeneralRepositoryInterface;
import static  AfternoonRacesRemote.Constants.Constants.*;
import AfternoonRacesRemote.Registry.RegistryConfig;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * @author João Amaral
 * @author Francisco Cunha
 */
public class SpectatorClient {
    public static void main(String[] args) {
        
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
        
        //localização por nome do objecto remoto no serviço de registos RMI
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
        
        Spectator[] spectator = new Spectator[NUM_SPECTATORS];
        
        for (int i = 0; i < NUM_SPECTATORS; i++) {
            spectator[i] = new Spectator(i, SPECTATOR_INIT_MONEY, bettingCenterInterface, controlCenterInterface, paddockInterface, generalRepositoryInterface);
            System.out.println("Spectator " + i + " has started");
            spectator[i].start();
        }

        for (int i = 0; i < NUM_SPECTATORS; i++) {
            try {
                spectator[i].join();
                System.out.println("Spectator " + i + " has finished");
            } catch (InterruptedException e) {
                System.out.println("Spectator " + i + " thas finished - exeption");
            }
        }

        try {
            generalRepositoryInterface.finished();
        } catch (RemoteException ex) {
            System.out.println("Error closing all!");
            System.exit(1);
        }
        System.out.println("Spectators Done!");
    }
}