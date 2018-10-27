package AfternoonRacesRemote.Monitors.GeneralRepository;

import AfternoonRacesRemote.interfaces.GeneralRepositoryInterface;
import AfternoonRacesRemote.interfaces.Register;
import AfternoonRacesRemote.Registry.RegistryConfig;

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
public class GeneralRepositoryServer {
    public static void main(String[] args) {
        
        // obtains the location of the RMI register service
        String rmiRegHostName = args[0];
        int rmiRegPortNumb = Integer.parseInt(args[1]);
        
        /*
        String rmiRegHostName = "localhost";
        int rmiRegPortNumb = 1099;
        */
        
        /*
        // instanciação e instalação do gestor de segurança 
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        */
        System.out.println("Security manager was installed!");
        
        // General Repository Instantiation
        GeneralRepository generalRepository = new GeneralRepository(rmiRegHostName, rmiRegPortNumb); // provalmente meter rmiRegHostName, rmiRegPortNumb como args
        //GeneralRepository generalRepository = new GeneralRepository(); // provalmente meter rmiRegHostName, rmiRegPortNumb como args
        GeneralRepositoryInterface generalRepositoryInterface = null;
        
        try{ 
            generalRepositoryInterface = (GeneralRepositoryInterface)
                    UnicastRemoteObject.exportObject((Remote) generalRepository, RegistryConfig.PORT_GENERAL_REPOSITORY);
        }
        catch(RemoteException e) {
            System.out.println("Excepção na geração do stub para o General Repository: " + e.getMessage());
            System.exit(1);
        }
        System.out.println("O stub para o General Repository foi gerado!");
        
        // it's register oon RMI service register
        String nameEntryBase = RegistryConfig.RMI_REGISTER_NAME;
        String nameEntryObject = RegistryConfig.RMI_REGISTRY_GENREPO_NAME;
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
            reg.bind(nameEntryObject, generalRepositoryInterface);
        } 
        catch (RemoteException e) {
            System.out.println("General Repository registration exception: " + e.getMessage ());
            System.exit(1);
        } 
        catch (AlreadyBoundException e) {
            System.out.println("General Repository already bound exception: " + e.getMessage());
            System.exit(1);
        }

        System.out.println("General Repository object was registered!");
    }   
}