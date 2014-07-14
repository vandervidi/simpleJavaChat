import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.*;


public class ClientGUI implements StringConsumer, StringProducer
{
	private JPanel North, South, Center;
	private JFrame frame;
	private JButton btConnect;
	private JButton btDisconect;
	private JButton btSend;
	private JLabel labelNickname;
	private JLabel labelMessage;
	private JLabel labelPort;
	private JLabel labelIp;
	private JLabel labelConnectedUsers;
	private JTextField tfIp, tfPort, tfUserMessage,tfNickName;
	private  JTextArea chatBox;
	private JTextArea OnlineUsers;
	private Socket socket=null;
	private ConnectionProxy connection = null;
	private JScrollPane sbChatBox;
	private JScrollPane sbOnlineUsers;
	
	public JTextArea getChatBox() {
		return chatBox;
	}
	public void setChatBox(JTextArea chatBox) {
		this.chatBox = chatBox;
	}
	public JTextArea getOnlineUsers() {
		return OnlineUsers;
	}
	public void setOnlineUsers(JTextArea onlineUsers) {
		OnlineUsers = onlineUsers;
	}
	public void connect() throws IOException
	{
		connection= new ConnectionProxy(socket,this);			//set Server side proxy's Name = User`s Nickname
		chatBox.append("Welcome to the chat!"+"\n");
		connection.start();
		connection.setName("ClientSideProxy");
		connection.consume(tfNickName.getText());
	}
	public ClientGUI()
	{
		North=new JPanel();
		South=new JPanel();
		Center=new JPanel();
		frame=new JFrame("Java Chat by Vidran Abdovich");
		btConnect=new JButton("Connect");
		btDisconect=new JButton("Disconnect");
		btDisconect.setEnabled(false);
		btSend=new JButton("Send");
		btSend.setEnabled(false);
		labelNickname=new JLabel("NickName:");
		labelMessage=new JLabel("Message:");
		labelConnectedUsers=new JLabel("Online users;");
		labelIp=new JLabel("Ip:");
		labelPort=new JLabel("Port:");
		tfIp=new JTextField(16);	//16 chars long
		tfPort=new JTextField("1500"); 	//5 chars long 
		tfNickName=new JTextField(10);	//10 chars long
		tfUserMessage=new JTextField(40);  //40 chars long
		tfUserMessage.setEditable(false);
		chatBox=new JTextArea(16,50);
		chatBox.setEditable(false);
		OnlineUsers=new JTextArea(16,10);
		OnlineUsers.setEditable(false);
		Center.setBackground(Color.DARK_GRAY);
		chatBox.setLineWrap(true);
		frame.getRootPane().setDefaultButton(btSend);
		sbChatBox=new JScrollPane(chatBox);
		sbOnlineUsers=new JScrollPane(OnlineUsers);
		chatBox.setLineWrap(true);

	}
	class ConnectbtListener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			String Nickname=null;
			String ip=null;
			int Port = 0;
			
			Nickname=tfNickName.getText();
			ip=tfIp.getText();
			Port=Integer.parseInt(tfPort.getText());
			if((tfNickName.getText()).equals("") || (tfIp.getText()).equals("") || (tfPort.getText()).equals("") )
			{
				chatBox.append("Error: Make sure your input is correct..."+"\n");
			}
			
			else
			{
			
			try
			{
				socket=new Socket(ip,Port);
				chatBox.append("Connecting.."+"\n");
			}
			 
			catch (Exception e2) 
			{
				chatBox.append("Error: The server you are trying to connect to is offline.."+"\n");
				e2.printStackTrace();
			}

			try
			{
				connect();
			} 
			catch (IOException e1)
			{
				System.out.println("Error connectin to the server...try again");
				e1.printStackTrace();
			}
			
			tfUserMessage.setEditable(true);
			btConnect.setEnabled(false);
			btDisconect.setEnabled(true);
			btSend.setEnabled(true);
			}
		}		
	}
	
	class disconnectBt implements ActionListener
	{

		public void actionPerformed(ActionEvent e) {
			try {
				socket.close();
				connection=null;
				chatBox.append("Disconnecting.."+"\n");
			} catch (IOException e1) {
				chatBox.append("You are already disconnected."+"\n");
				e1.printStackTrace();
			}
			if(socket.isClosed())
			{
			chatBox.append("Disconnected"+"\n");
			tfUserMessage.setEditable(false);
			btConnect.setEnabled(true);
			btDisconect.setEnabled(false);
			btSend.setEnabled(false);
			OnlineUsers.setText("");
			tfUserMessage.setText("");
			
			}
			
		}		
	}
	
	class sendListener implements ActionListener
	{

		public void actionPerformed(ActionEvent e) {
			
			if (!(tfUserMessage).getText().equals(null) || !(tfUserMessage).getText().equals(""))
			{	
				connection.consume(tfUserMessage.getText());
				tfUserMessage.setText("");
				
			}						
		}
		
	}

	public void start()
	{	
		ActionListener connectListener= new ConnectbtListener();
		btConnect.addActionListener(connectListener);
		ActionListener disconnectListener=new disconnectBt();
		btDisconect.addActionListener(disconnectListener);
		ActionListener sendListener= new sendListener();
		btSend.addActionListener(sendListener);
		frame.setLayout(new BorderLayout());
		North.add(labelNickname);
		North.add(tfNickName);
		North.add(labelIp);
		North.add(tfIp);
		North.add(labelPort);
		North.add(tfPort);
		North.add(btConnect);
		North.add(btDisconect);
		Center.add(sbChatBox);
		Center.add(sbOnlineUsers);
		South.add(labelMessage);
		South.add(tfUserMessage);
		South.add(btSend);
		frame.add(North, BorderLayout.NORTH);
		frame.add(Center, BorderLayout.CENTER);
		frame.add(South, BorderLayout.SOUTH);
		frame.setSize(700, 400);
		frame.setVisible(true);
		
		sbChatBox.getVerticalScrollBar().addAdjustmentListener(	//JScrollpane - Force autoscroll to bottom
				new AdjustmentListener() {
					public void adjustmentValueChanged(AdjustmentEvent e) {
						chatBox.select(chatBox.getHeight() +1000, 0);
						
					}
				});
		frame.addWindowListener(new WindowAdapter() 		//window closing listener
		{
			public void windowClosing(WindowEvent event)
			{
				frame.setVisible(false);
				frame.dispose();
				System.exit(0);
			}
		});
		
	}
	public static void main(String[] args) 
	{

				ClientGUI gui = new ClientGUI();
				gui.start();

	}
	public void addConsumer(StringConsumer sc) {
		// TODO Auto-generated method stub
		
	}
	
	public void removeConsumer(StringConsumer sc) {
		// TODO Auto-generated method stub
		
	}
	
	public void consume(String str) {
		
		
	}
}

