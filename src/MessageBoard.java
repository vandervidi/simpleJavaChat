import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.util.Date;

import javax.swing.plaf.ColorChooserUI;

public class MessageBoard implements StringConsumer, StringProducer
{
int clientsCounter=0;
Date date=new Date();
private StringConsumer ClientsArray[];
private StringBuffer NicknamesArray[];

public MessageBoard()
{
	ClientsArray = new ConnectionProxy[50];
	NicknamesArray= new StringBuffer[50];
}
public void Disconnect(ConnectionProxy proxy) throws IOException
{
	for (int i=0;i<ClientsArray.length;i++)
	{
		if (((ConnectionProxy)ClientsArray[i])==proxy)
		{
			broadcast(((ConnectionProxy)ClientsArray[i]).getName()+" disconnected");
			((ConnectionProxy)ClientsArray[i]).getSocket().close();
			ClientsArray[i]=null;
			NicknamesArray[i]=null;
			clientsCounter--;
		}
	}
		sortClientsArray();
		sortNickNamesArray();
		
		/*		for (int j=0; j<50;j++)
		{
			System.out.println("Index["+j+"]"+ClientsArray[j]+"   "+ NicknamesArray[j]);
		}
		
		*/
		
		
		broadcast(ExportNicknameArrayToString());
	
}
public void sortClientsArray()
{
	
		for(int current=0; current<ClientsArray.length-1; current++)
		{
			if (ClientsArray[current]==null)
			{
				ClientsArray[current]=((ConnectionProxy)ClientsArray[current+1]);
				ClientsArray[current+1]=null;
			}
			else
				continue;
		
	}
}

public void sortNickNamesArray()
{
		for(int current=0; current<NicknamesArray.length-1; current++)
		{
			if (NicknamesArray[current]==null)
			{
				NicknamesArray[current]=NicknamesArray[current+1];
				NicknamesArray[current+1]=null;			
			}
		}
}

public void broadcast(String Message)			//when a message is recieved it is broadcasted to all users
{
	for (int i=0; clientsCounter<ClientsArray.length; i++)
    {
    	if (i>=ClientsArray.length)
			break;
    	if (((ConnectionProxy)ClientsArray[i])==null)
    	{
    	 			continue;
    	}
    	else
    	{
    		((ConnectionProxy)ClientsArray[i]).consume(Message);
    	}
    }
}

String ExportNicknameArrayToString()
{
	String string="UserConnected-";
	for(int j=0;j<NicknamesArray.length;j++)
	{
		if (NicknamesArray[j]!=null)
		string=(string+"- "+NicknamesArray[j]+"\n");
		else 
			break;
	}
	return string;
	
}
	
	public void addConsumer(StringConsumer sc)									// add a new connection to the proxy array
	{																			//broadcast to everyone that this user is now connected
		 if (clientsCounter < ClientsArray.length)		
		    {
				 ClientsArray[clientsCounter] = sc;
			    try 
			    {
					((ConnectionProxy)ClientsArray[clientsCounter]).setName(((ConnectionProxy)ClientsArray[clientsCounter]).getDis().readUTF());
					NicknamesArray[clientsCounter]=new StringBuffer(((ConnectionProxy)ClientsArray[clientsCounter]).getName()); // input new nickname into connected nicknames array
			    }
			    catch (IOException e) 
				{
					e.printStackTrace();
				}
			    for (int i=0; clientsCounter<ClientsArray.length; i++)			// a loop that broadcasts to everyone that this user is now connected
			    {
			    	if (i>=ClientsArray.length)
		    			break;
			    	if (ClientsArray[i]==null)
			    	{
			    	 			continue;
			    	}
			    	else
			    	{
			    		((ConnectionProxy)ClientsArray[i]).consume(date.toGMTString()+": "+((ConnectionProxy)ClientsArray[clientsCounter]).getName()+" is now connected.."); //confirming connection
			    		((ConnectionProxy)ClientsArray[i]).consume(ExportNicknameArrayToString());// send connected Nickanmes list to gui
	
			    	}
			    }
			    
			    clientsCounter++;
		    }
		    else
		    {
		    	try 
		    	{
					((ConnectionProxy)sc).getSocket().close();
				} 
		    	catch (IOException e) 
				{
					e.printStackTrace();
				}
		    }
}

	
	
	public void removeConsumer(StringConsumer sc) {
		// TODO Auto-generated method stub
		
	}

	
	public void consume(String str) {
		// TODO Auto-generated method stub
		
	}

}

