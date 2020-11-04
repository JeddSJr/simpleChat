import java.io.*;
import java.util.Scanner;
import client.*;
import common.*;

public class ServerConsole implements ChatIF {
  //Class variables *************************************************

  /**
   * The default port to connect on.
   */
  final public static int DEFAULT_PORT = 5555;

  //Instance variables **********************************************

  /**
   * The instance of the client that created this ConsoleChat.
   */
  EchoServer server;


  /**
   * Scanner to read from the console
   */
  Scanner fromConsole;

  //Constructors ****************************************************

  /**
   * Constructs an instance of the ServerConsole UI.
   *
   * @param port The port to connect on.
   */
  public ServerConsole(int port)
  {
    try
    {
      server= new EchoServer(port, this);

    }
    catch(Exception exception)
    {
      System.out.println("Error: Can't setup server!"
                + " Terminating client.");
      System.exit(1);
    }
    try
    {
      server.listen(); //Start listening for connections
    }
    catch (Exception ex)
    {
      System.out.println("ERROR - Could not listen for clients!");
    }

    // Create scanner object to read from console
    fromConsole = new Scanner(System.in);
  }

  //Instance methods ************************************************

  /**
   * This method waits for input from the console.  Once it is
   * received, it sends it to the server's message handler.
   */
  public void accept(){
    try
    {
      String message;
      while (true)
      {
        message = fromConsole.nextLine();
        server.handleMessageFromServerUI(message);
      }
    }
    catch (Exception ex)
    {
      System.out.println
        ("Unexpected error while reading from console!");
    }
  }

  /**
   * This method overrides the method in the ChatIF interface.  It
   * displays a message onto the screen.
   *
   * @param message The string to be displayed.
   */
  public void display(String message)
  {
    System.out.println("> " + message);
  }
  //Class methods ***************************************************

  /**
   * This method is responsible for the creation of the Client UI.
   *
   * @param args[0] The port to connect to.
   */
  public static void main(String[] args) {
    int port;

    try{
      port = Integer.parseInt(args[0]);
    }
    catch(ArrayIndexOutOfBoundsException e){
      port = DEFAULT_PORT;
    }
    ServerConsole chat= new ServerConsole(port);
    chat.accept();
  }

}
