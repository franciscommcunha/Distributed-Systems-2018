package AfternoonRacesDistributed.CommunicationChannel;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Data type for communication channel creation on the client side
 * based on socket class and TCP protocol. 
 * Data transfer is based on objects
 * Adapted from class ClientCom used in the theoretical classes
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class CommunicationChannelClient {
    
    /**
     * Communication socket
     * @serialField communication_socket
     */
    private Socket communication_socket = null;
    
    /**
     * Name of the computational system where the server is located
     */
    private String server_name = null;
    
    /**
     * Number of the listening port of the server
    */
    private int server_port;
    
    /**
     * Input stream of the communication channel
     */
    private ObjectInputStream input_stream = null;
    
    /**
     * Output stream of the communication channel
     */
    private ObjectOutputStream output_stream = null;
    
    
    /**
     * Instantiation of a communication channel
     * @param name name of the computational system where the server is located
     * @param port number of the listening port of the server
     */
    public CommunicationChannelClient (String name, int port){
        server_name = name;
        server_port = port;
    }
    
    /**
     * Opening of the communication channel
     * Instantiation of a communication socket and association to the server address
     * Opening of input and output streams of the socket
     * @return true if communication channel opened successful, false otherwise
     */
    public boolean open(){
        boolean success = true; 
        SocketAddress server_addr = new InetSocketAddress (this.server_name, this.server_port);
        
        try{
            communication_socket = new Socket();
            communication_socket.connect (server_addr);           
        }
        catch(UnknownHostException e){  
            System.out.println("Thread "+Thread.currentThread().getName() + ", unkown server :" + server_name);
            e.printStackTrace ();
            System.exit (1);
        }
        catch(NoRouteToHostException e){
            System.out.println("Thread "+Thread.currentThread().getName() + ", server could not be reached :" + server_name);
            e.printStackTrace ();
            System.exit (1);
        }
        catch(ConnectException e){ 
            System.out.println("Thread "+Thread.currentThread().getName() + ", no response from server at :" + server_name + "." + server_port);
            if (e.getMessage ().equals ("Connection refused"))
                success = false;
            else { 
                System.out.println("Thread "+Thread.currentThread().getName() + ", "+e.getMessage ());
                e.printStackTrace ();
                System.exit (1);
            }    
        }
        catch(SocketTimeoutException E){
            System.out.println("Thread "+Thread.currentThread().getName() + ", server timeout : "+ server_name + "." + server_port);
            success = false;
        }
        catch(IOException e){ // fatal error, other errors
            System.out.println("Thread "+Thread.currentThread().getName() + ", Unknown error : " + server_name + "." + server_port);
            e.printStackTrace ();
            System.exit (1);
        }
        
        if (!success)  return (success);
        
        try{ 
            output_stream = new ObjectOutputStream (communication_socket.getOutputStream());
        }
        catch (IOException e){      
            System.out.println("Thread "+Thread.currentThread().getName() + ", Could not open input socket channel : " + server_name + "." + server_port);
            e.printStackTrace ();
            System.exit (1);
        }

        try{
            input_stream = new ObjectInputStream (communication_socket.getInputStream());
        }
        catch (IOException e){ 
            System.out.println("Thread "+Thread.currentThread().getName() + ", Could not open output socket channel : " + server_name + "." + server_port);
            e.printStackTrace ();
            System.exit (1);
        }
        
        return (success);
    }
    
    /**
     * Closing of the communication channel
     * Closing of the communication sockets
     * Closing of the input and ouput socket streams
     */
    public void close (){
        try{ 
            input_stream.close();
        }catch(IOException e){
            System.out.println(Thread.currentThread ().getName () + " - could not close input socket channel!");
            e.printStackTrace ();
            System.exit (1);
        }

        try{
            output_stream.close();
        }
        catch (IOException e){ 
            System.out.println(Thread.currentThread ().getName () + " - could not close output socket channel!");
            e.printStackTrace ();
            System.exit (1);
        }

        try{ 
            communication_socket.close();
        }
        catch (IOException e)
        { 
            System.out.println(Thread.currentThread ().getName () + " - could not close communication socket!");
            e.printStackTrace ();
            System.exit (1);
        }
        
    }
     
   /**
   * Reading of an object from the communication channel
   * @return read object
   */
   public Object readObject(){
        // object 
        Object fromServer = null;

        try{
            fromServer = input_stream.readObject ();
        }
        catch(InvalidClassException e){ 
            System.out.println(Thread.currentThread ().getName () + " - could not deserialize object to be read!");
            e.printStackTrace ();
            System.exit (1);
        }
        catch (IOException e){ 
            System.out.println(Thread.currentThread ().getName () + " - error while reading object from the input channel of the communication socket!");
            e.printStackTrace ();
            System.exit (1);
        }
        catch (ClassNotFoundException e)
        { 
            System.out.println(Thread.currentThread ().getName () + " - read object corresponds to an unknown data type!");
            e.printStackTrace ();
            System.exit (1);
        }

        return fromServer;
    }

    /**
    * Writting of and object to the communication channel
    * @param toServer object to be written
    */
    public void writeObject (Object toServer){
        try{ 
            output_stream.writeObject (toServer);
        }
        catch (InvalidClassException e){ 
            System.out.println(Thread.currentThread ().getName () + " - could not deserialize object to be written!");
            e.printStackTrace ();
            System.exit (1);
        }
        catch (NotSerializableException e){ 
            System.out.println(Thread.currentThread ().getName () + " - object to be written belong to a data type not serializable!");
            e.printStackTrace ();
            System.exit (1);
        }
        catch (IOException e){ 
            System.out.println(Thread.currentThread ().getName () + " - error while writting object of the output channel communication socket!");
            e.printStackTrace ();
            System.exit (1);
        }
        
   }
    
}
