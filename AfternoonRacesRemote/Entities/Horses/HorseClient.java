package AfternoonRacesRemote.Entities.Horses;

import static AfternoonRacesRemote.Constants.Constants.*;
import AfternoonRacesRemote.interfaces.GeneralRepositoryInterface;
import AfternoonRacesRemote.interfaces.PaddockInterface;
import AfternoonRacesRemote.interfaces.RacingTrackInterface;
import AfternoonRacesRemote.interfaces.StableInterface;
import AfternoonRacesRemote.Registry.RegistryConfig;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author João Amaral
 * @author Francisco Cunha
 */
public class HorseClient {
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
        PaddockInterface paddockInterface = null;
        RacingTrackInterface racingTrackInterface = null;
        StableInterface stableInterface = null;
        
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
        
        Horse[] horse = new Horse[NUM_HORSES];
        
        for (int i = 0; i < NUM_HORSES; i++) {
            horse[i] = new Horse(i, ThreadLocalRandom.current().nextInt(HORSE_MIN_AGILITY, HORSE_MAX_AGILITY+1), stableInterface, paddockInterface, racingTrackInterface, generalRepositoryInterface);
            System.out.println("Horse " + i + " has started");
            horse[i].start();
        }
        
        for (int i = 0; i < NUM_HORSES; i++) {
            try {
                horse[i].join();
                System.out.println("Horse " + i + " has finished");
            } catch (InterruptedException e) {
                System.out.println("Horse " + i + " has finished - exeption");
            }
        }
        

        try {
            generalRepositoryInterface.finished();
        } catch (RemoteException ex) {
            System.out.println("Error closing all!");
            System.exit(1);
        }
        System.out.println("Horses Done!");
        
    }   
}