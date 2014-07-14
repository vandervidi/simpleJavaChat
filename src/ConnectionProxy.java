import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ConnectionProxy extends Thread implements StringConsumer, StringProducer
{
private InputStream is=null;
private OutputStream os=null;
private DataInputStream dis=null;
private DataOutputStream dos=null;
private Socket ProxySocket;
private Boolean loop=true;
private ClientGUI gui=null;
private MessageBoard mb=null;
private String tempString;
public Socket getSocket()
{
	return ProxySocket;
}
public DataInputStream getDis()
{
	return dis;
}
	public ConnectionProxy(Socket socket,ClientGUI gui) throws IOException 
	{
		is=socket.getInputStream();
		dis=new DataInputStream(is);
		os=socket.getOutputStream();
		dos=new DataOutputStream(os);
		ProxySocket=socket;
		this.gui=gui;
	}
	
	public ConnectionProxy(Socket socket,MessageBoard mb) throws IOException 
	{
		is=socket.getInputStream();
		dis=new DataInputStream(is);
		os=socket.getOutputStream();
		dos=new DataOutputStream(os);
		ProxySocket=socket;
		this.mb=mb;
	}
	public void run()
	{
		while(loop)
		{
			if (gui != null)
			{
				try 
				{
					if (ProxySocket.isClosed()==false)
					{
						tempString=dis.readUTF();
							if (tempString.startsWith("UserConnected-"))
							{
								gui.getOnlineUsers().setText("");
								tempString=tempString.replace("UserConnected-","");
							gui.getOnlineUsers().append(tempString+"\n");
							}
							else
							{
								gui.getChatBox().append(tempString+"\n");
							}
					}
				} 
				catch (IOException e) 
				{
					
					loop=false;
					try {
						ProxySocket.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
				
			}
			else 
			{
				try 
				{
					if (ProxySocket.isClosed()==false)
					{
						tempString=dis.readUTF();  //the message that the server recieves
						mb.broadcast(this.currentThread().getName()+": "+tempString);
					}
				}
				
				catch (IOException e) 
				{
					loop=false;
					try {
						mb.Disconnect(this);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
			}
		}
	}
	
	public void consume(String str) {
		try {
			dos.writeUTF(str);
		} catch (IOException e) 
		{
				e.printStackTrace();
		}
	}


	
	public void addConsumer(StringConsumer sc) {
		// TODO Auto-generated method stub
		
	}



	
	public void removeConsumer(StringConsumer sc) {
		// TODO Auto-generated method stub
		
	}
 
}

