// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com

package client;

import ocsf.client.*;
import common.*;
import java.io.*;
/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************

  /**
   * The interface type variable.  It allows the implementation of
   * the display method in the client.
   */
  ChatIF clientUI;
  /**
   * The id of the client
   */
  int idLogin;
  //Constructors ****************************************************

  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */

  public ChatClient(int idLogin,String host, int port, ChatIF clientUI)
    throws IOException
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.idLogin=idLogin;
    try{
      openConnection();
      sendToServer("#login "+idLogin);
    }
    catch (Exception e) {
      clientUI.display
        ("Could not login to server.  Terminating client.");
      quit();
    }
  }


  //Instance methods ************************************************

  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg)
  {
    clientUI.display(msg.toString());
    if(((String)msg).equals("SERVER MSG #quit")){
      connectionClosed();
    }
  }

  /**
   * This method handles all data coming from the UI
   *
   * @param message The message from the UI.
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
      if(message.startsWith("#")==true){
        switch (message) {
          case("#quit"):
            sendToServer("#quit");
            this.quit();
            break;
          case("#logoff"):
            this.closeConnection();
            break;
          case("#login"):
            if(this.isConnected()==false){
              sendToServer("#login "+idLogin);
            }
            else{
              System.out.println("Erreur: Vous etes deja connecte.");
            }
            break;
          case("#gethost"):
            System.out.println(this.getHost());
            break;
          case("#getport"):
            System.out.println(this.getPort());
            break;
          default:
            if(message.startsWith("#set")){
              if(this.isConnected()==false){
                if (message.startsWith("#sethost ")) {
                  String host = message.replaceAll("#sethost ", "");
                  this.setHost(host);
                }
                else if (message.startsWith("#setport ")){
                  int port = Integer.parseInt(message.replaceAll("#setport ", ""));
                  this.setPort(port);
                }
              }
            else{
              System.out.println("Erreur: Vous devez vous deconnecte pour changer de port ou host.");
            }
            }
            else{
              sendToServer(message);
            }
          break;
        }
      }
      else{
        openConnection();
        sendToServer(message);
      }
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }

  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }

  public void connectionClosed(){
    if(!this.isConnected()){
      System.out.println("Disconnecting from server.");
      System.exit(0);
    }
  }

  protected void connectionException(Exception exception){

  }
}
//End of ChatClient class
