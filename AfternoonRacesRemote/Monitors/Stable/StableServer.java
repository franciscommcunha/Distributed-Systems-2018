package AfternoonRacesRemote.Monitors.Stable;

import AfternoonRacesRemote.interfaces.StableInterface;
import AfternoonRacesRemote.interfaces.Register;
import AfternoonRacesRemote.Registry.RegistryConfig;
import AfternoonRacesRemote.interfaces.GeneralRepositoryInterface;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * @author João Amaral
 * @author Francisco Cunha
 */
public class StableServer {
    public static void main(String[] args) {
        
        // obtains the location of the RMI register service
        
        String rmiRegHostName = args[0];
        int rmiRegPortNumb = Integer.parseInt(args[1]);
        
        /*
        String rmiRegHostName = "localhost";
        int rmiRegPortNumb = 1099;
        */
        
        // instanciação e instalação do gestor de segurança 
        /*
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        */
        
        System.out.println("Security manager was installed!");
        
        GeneralRepositoryInterface generalRepositoryInterface = null;
        
        try {
            Registry registry = LocateRegistry.getRegistry(rmiRegHostName, rmiRegPortNumb);
            generalRepositoryInterface = (GeneralRepositoryInterface)
                    registry.lookup(RegistryConfig.RMI_REGISTRY_GENREPO_NAME);
        } 
        catch (RemoteException e) {
            System.out.println("Excepção na localização do General Repository: " + e.getMessage() + "!");
            System.exit(1);
        } 
        catch (NotBoundException e) {
            System.out.println("O General Repository não está registado: " + e.getMessage() + "!");
            System.exit(1);
        }
        
        
        // Stable Instantiation
        Stable stable = new Stable(rmiRegHostName, rmiRegPortNumb); // provalmente meter rmiRegHostName, rmiRegPortNumb como args
        StableInterface stableInterface = null;
        
        try{ 
            stableInterface = (StableInterface)
                    UnicastRemoteObject.exportObject((Remote) stable, RegistryConfig.PORT_STABLE);
        }
        catch(RemoteException e) {
            System.out.println("Excepção na geração do stub para o Stable: " + e.getMessage());
            System.exit(1);
        }
        System.out.println("O stub para o Stable foi gerado!");
        
        // it's register oon RMI service register
        String nameEntryBase = RegistryConfig.RMI_REGISTER_NAME;
        String nameEntryObject = RegistryConfig.RMI_REGISTRY_STABLE_NAME;
        Registry registry = null;
        Register reg = null;
        
        try {
            registry = LocateRegistry.getRegistry(rmiRegHostName, rmiRegPortNumb);
        } 
        catch (RemoteException e) {
            System.out.println("RMI registry creation exception: " + e.getMessage ());
            System.exit(1);
        }
        System.out.println("RMI registry was created!");

        try {
            reg = (Register) registry.lookup(nameEntryBase);
        } 
        catch (RemoteException e) {
            System.out.println("RegisterRemoteObject lookup exception: " + e.getMessage());
            System.exit(1);
        } 
        catch (NotBoundException e) {
            System.out.println("RegisterRemoteObject not bound exception: " + e.getMessage());
            System.exit(1);
        }

        try {
            reg.bind(nameEntryObject, stableInterface);
        } 
        catch (RemoteException e) {
            System.out.println("Stable registration exception: " + e.getMessage ());
            System.exit(1);
        } 
        catch (AlreadyBoundException e) {
            System.out.println("Stable already bound exception: " + e.getMessage());
            System.exit(1);
        }
        System.out.println("Stable object was registered!");
    }   
}
