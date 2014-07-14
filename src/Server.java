import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
 @SuppressWarnings("resource")
public static void main(String args[])
 {
  ServerSocket server = null;
  MessageBoard mb = new MessageBoard();
  try
  {
   server = new ServerSocket(1500,5);
  } 
  catch(IOException e)
  {
  }
  Socket socket = null;
  ConnectionProxy connection = null;
  while(true)
  {
   try
   {
    socket = server.accept();
    connection = new ConnectionProxy(socket,mb); //connection= Connection Proxy
    //mb.sortUsersArray();			//bubble sorts the connected usrs array; 
    mb.addConsumer(connection);  //sends proxy thread to mb and gets into an array
    connection.start();
   }
   catch(IOException e)
   {
   }
  }
 }
}