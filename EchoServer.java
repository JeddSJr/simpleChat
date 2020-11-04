// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com

import java.io.*;
import ocsf.server.*;
import common.*;

/**
 * This class overrides some of the methods in the abstract
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer
{
  //Class variables *************************************************

  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  /**
   * The interface type variable.  It allows the implementation of
   * the display method in the server.
   */
   ChatIF serverUI;

  //Constructors ****************************************************

  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ChatIF serverUI)
  {
    super(port);
    this.serverUI=serverUI;
  }


  //Instance methods ************************************************

  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
    System.out.println("Message received: " + msg + " from " + client);
    if(((String)msg).startsWith("#login ") && client.getInfo("id") == null){
        int loginid = Integer.parseInt(((String)msg).replaceAll("#login ", ""));
        client.setInfo("id", loginid);
    }
    else if (((String)msg).startsWith("#login ") && client.getInfo("id") != null) {
      try {
        client.close();
      } catch(Exception e) {}
      System.out.println("Error: deja login");
    }
    else if (((String)msg).equals("#quit")) {
      try {
        client.close();
      } catch(Exception e) {  }
      System.out.println("Disconnecting client: "+client.getInfo("id"));
    }
    else{
      this.sendToAllClients(client.getInfo("id")+":"+msg);
    }
  }

  /**
   * This method handles all data coming from the UI
   *
   * @param message The message from the UI.
   */
  public void handleMessageFromServerUI(String message)
  {
    try
    {
      serverUI.display("SERVER MSG "+message);
      sendToAllClients("SERVER MSG "+message);
      if(message.startsWith("#")==true){
        switch (message) {
          case("#quit"):
            sendToAllClients("SERVER MSG "+message);
            System.exit(0);
            break;
          case("#stop"):
            this.stopListening();;
            break;
          case("#close"):
            this.close();
            break;
          case("#start"):
            if(!this.isListening()){
              this.listen();
            }
            else{
              System.out.println("Le serveur est en marche");
            }
            break;
          case("#getport"):
            System.out.println(this.getPort());
            break;
          default:
          if(message.startsWith("#setport ")){
            if(!this.isListening()){
              int port = Integer.parseInt(message.replaceAll("#setport ", ""));
              this.setPort(port);
            }
            else{
              System.out.println("Erreur: Vous devez vous deconnecte pour changer de port ou host.");
            }
          }
        }
      }
    }
    catch(IOException e)
    {
      System.out.println(e);
      serverUI.display
        ("Could not read UI's message.  Terminating UI.");
      System.exit(0);
    }
  }

  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }

  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }

  protected void clientConnected(ConnectionToClient client) {
    System.out.println("Connection of: " +client.toString()+ "");
  }


  //Class methods ***************************************************

  /**
   * This method is responsible for the creation of
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555
   *          if no argument is entered.
   */
  public static void main(String[] args)
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }

    ChatIF serverUI= new ServerConsole(port);
    EchoServer sv = new EchoServer(port, serverUI);

    try
    {
      sv.listen(); //Start listening for connections
    }
    catch (Exception ex)
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
}
//End of EchoServer class
