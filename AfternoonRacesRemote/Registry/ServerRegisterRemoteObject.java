package AfternoonRacesRemote.Registry;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.AccessException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.*;
import AfternoonRacesRemote.interfaces.Register;

/**
 *  This data type instantiates and registers a remote object that enables the registration of other remote objects
 *  located in the same or other processing nodes in the local registry service.
 *  Communication is based in Java RMI.
 */

public class ServerRegisterRemoteObject
{
  /**
   *  Main task.
     * @param args
   */
   public static void main(String[] args)
   {
        /* get location of the registry service */
        
        String rmiRegHostName = args[0];
        int rmiRegPortNumb = Integer.parseInt(args[1]);
        
        /*
        String rmiRegHostName = "localhost";
        int rmiRegPortNumb = 1099;  
        */

       /* create and install the security manager */
       /* talvez apagar isto
        if (System.getSecurityManager () == null)
           System.setSecurityManager (new SecurityManager ());
        System.out.println("Security manager was installed!");
       */
       /* instantiate a registration remote object and generate a stub for it */

        RegisterRemoteObject regEngine = new RegisterRemoteObject (rmiRegHostName, rmiRegPortNumb);
        Register regEngineStub = null;

        try{ 
            regEngineStub = (Register) UnicastRemoteObject.exportObject (regEngine, RegistryConfig.RMI_REGISTER_PORT);
            //regEngineStub = (Register) UnicastRemoteObject.exportObject (regEngine, 22000);
        }
        catch (RemoteException e) { 
           System.out.println("RegisterRemoteObject stub generation exception: " + e.getMessage ());
           System.exit (1);
        }
        System.out.println("Stub was generated!");

       /* register it with the local registry service */

        String nameEntry = "RegisterHandler";
        Registry registry = null;

        try{ 
            registry = LocateRegistry.getRegistry (rmiRegHostName, rmiRegPortNumb);
        }
        catch (RemoteException e){ 
           System.out.println("RMI registry creation exception: " + e.getMessage ());
           System.exit (1);
        }
        System.out.println("RMI registry was created!");

        try{ 
            registry.rebind (nameEntry, regEngineStub);
        }
        catch (RemoteException e){ 
           System.out.println("RegisterRemoteObject remote exception on registration: " + e.getMessage ());
           System.exit (1);
        }
        System.out.println("RegisterRemoteObject object was registered!");
    }
}

