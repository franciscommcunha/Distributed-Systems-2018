package AfternoonRacesDistributed.CommunicationChannel;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Data type for communication channel creation on the server side
 * based on socket class and TCP protocol. 
 * Data transfer is based on objects
 * Adapted from class ServerCom used in the theoretical classes
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class CommunicationChannelServer {

    /**
     * Listening socket
     * @serialField listening_socket
     */
    private ServerSocket listening_socket = null;

    /**
     * Communication socket
     * @serialField communication_socket
     */
    private Socket communication_socket = null;
    
    /**
     * Number of the listening port of the server
     * @serialField 
     */
    private int server_port;

    /**
     * Input stream of the communication channel
     * @serialField input_stream
     */
    private ObjectInputStream input_stream = null;
    
    /**
     * Output stream of the communication channel
     * @serialField output_stream
     */

    private ObjectOutputStream output_stream = null;

    /**
     * Instantiation of a communication channel (type 1)
     * @param portNumb number of the listening port of the server
     */

    public CommunicationChannelServer(int portNumb){
        server_port = portNumb;
    }

    /**
     * Instantiation of a communication channel (type 1)
     *
     * @param portNumb number of server listening port
     * @param lSocket listening socket of server
     */
     public CommunicationChannelServer(int portNumb, ServerSocket lSocket){
        server_port = portNumb;
        listening_socket = lSocket;
    }
    
      /**
       * Establishment of the listening socket and association 
       * with the machine public listening address and port 
      */
    public void start (){
        try{ 
            listening_socket = new ServerSocket (server_port);
        }
        catch (BindException e){ // fatal error, port already in use
            System.out.println(Thread.currentThread ().getName () + " - could not link listening socket to port: " + server_port + "!");
            e.printStackTrace ();
            System.exit (1);
        }
        catch (IOException e){ // fatal error, other causes
            System.out.println(Thread.currentThread ().getName () + " - unkown error ocurred while linking listening socket to port: " + server_port + "!");
            e.printStackTrace ();
            System.exit (1);
        }
    }
    
    /**
     * Closing of the service
     * Closing of the listening socket
     */
    public void end (){
        try{ 
            listening_socket.close ();
        }
        catch (IOException e){
            System.out.println(Thread.currentThread ().getName () + " - não foi possível fechar o socket de escuta!");
            e.printStackTrace ();
            System.exit (1);
        }
        
    }  
    
   /**
   * Listening process
   * Creation of a communication channel for a pending request
   * Instantiation of a communication socket and its association with the client address
   * Opening of input and output streams of the socket
   * 
   * @return communication channel
   */

    public CommunicationChannelServer accept (boolean... a){
        
        CommunicationChannelServer scon; // communication channel

        scon = new CommunicationChannelServer(server_port, listening_socket);
        
        try{ 
            scon.communication_socket = listening_socket.accept();
        }
        catch (SocketException e){ 
            //System.out.println(Thread.currentThread ().getName () + " - listening socket was closed during listening process!");
            //e.printStackTrace ();
            //if(!a[0])
            //    System.exit (1);
            return scon;
        }
        catch (IOException e)
        { 
            System.out.println(Thread.currentThread ().getName () + " - could not open a communication channel for a pending request!");
            e.printStackTrace ();
            System.exit (1);
        }

        try{ 
            scon.input_stream = new ObjectInputStream (scon.communication_socket.getInputStream ());
        }
        catch (IOException e){ 
            System.out.println(Thread.currentThread ().getName () + " - could not open socket input channel!");
            e.printStackTrace ();
            System.exit (1);
        }

        try
        { 
            scon.output_stream = new ObjectOutputStream (scon.communication_socket.getOutputStream ());
        }
        catch (IOException e){
            System.out.println(Thread.currentThread ().getName () + " - could not open socket output channel");
            e.printStackTrace ();
            System.exit (1);
        }

        return scon;
    }
     
    /**
    * Closing of the communication channel
    * Closing of the input and output socket streams
    * Closing of the communication socket
    */
    public void close (){
        try{ 
            input_stream.close();
        }
        catch (IOException e){
            System.out.println(Thread.currentThread ().getName () + " - could not close socket input channel!");
            e.printStackTrace ();
            System.exit (1);
        }

        try{ 
            output_stream.close();
        }
        catch (IOException e){ 
            System.out.println(Thread.currentThread ().getName () + " - could not close socket output channel!");
            e.printStackTrace ();
            System.exit (1);
        }

        try{ 
            communication_socket.close();
        }
        catch (IOException e){
            System.out.println(Thread.currentThread ().getName () + " - could not close communication channel!");
            e.printStackTrace ();
            System.exit (1);
        }
    } 
 
    /**
     * Reading of a communication channel object
     * @return object read
     */
    public Object readObject(){
      
        // Object
        Object fromClient = null;

        try{ 
           fromClient = input_stream.readObject ();
        }
        catch (InvalidClassException e){
             System.out.println(Thread.currentThread ().getName () + " - could not deserialize object to be read!");
             e.printStackTrace ();
             System.exit (1);
        }
        catch (IOException e){
             System.out.println(Thread.currentThread ().getName () + " - error while reading object from the input channel of the communication socket!");
             e.printStackTrace ();
             System.exit (1);
        }
        catch (ClassNotFoundException e){ 
             System.out.println(Thread.currentThread ().getName () + " - read object corresponds to an unknown data type!");
             e.printStackTrace ();
             System.exit (1);
        }

        return fromClient;
    }

    /**
     * Writting of an object to the communication channel
     * @param toClient object to be written
     */
    public void writeObject (Object toClient){
      
        try{ 
            output_stream.writeObject (toClient);
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
