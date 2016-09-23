
package gamechat;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;



public class Client extends JFrame{

	private static JTextField userText;
	private static JTextArea chatWindow;
	private static ObjectOutputStream output;
	private static ObjectInputStream input;
	private static String message= "";
	private static String serverIP;
	private static Socket connection;

	
	//constructor 
	public Client(String host){
		super("Client");
		serverIP= host;
		userText= new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e){
						sendMessage(e.getActionCommand());
						userText.setText("");
					}
				}
				
				);
		add(userText, BorderLayout.NORTH);
		chatWindow= new JTextArea();
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(300,150);
		setVisible(true);
	}
	
	//connect to server
	public static void startRunning(){
		try {
			connectToServer();
			setupStreams();
			whileChatting();
			
		}catch(EOFException e){
			showMessages("\n Client terminated the connection");
		}
		catch(IOException e){
			e.printStackTrace();
		}finally {
			closeCrap();
		}
	}
	
	private static void connectToServer() throws IOException {
		
		showMessages("Attempting Connection... \n");
		connection= new Socket(InetAddress.getByName(serverIP), 6789);
		showMessages("Conencted to" + connection.getInetAddress().getHostName());
	}
	
	//set up streams to send and receive messages
	private static void setupStreams() throws IOException{
		
		output= new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input= new ObjectInputStream(connection.getInputStream());
		showMessages("\n Your streams are now connected \n");
	}
	
	//while chatting with server
	private static void whileChatting() throws IOException{
		ableToType(true);
		do {
			try {
				message= (String) input.readObject();
				showMessages("\n" + message);
			}catch(ClassNotFoundException e){
				showMessages("\n U don't know that object type");
			}
			
			
		}while(!message.equals("SERVER - END"));
	}
	
	//close the streams and sockets
	private static void closeCrap(){
		showMessages("\n Closing...");
		ableToType(false);
		
		try {
			output.close();
			input.close();
			connection.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	//send messages to server
	private  void sendMessage(String message){
		try {
			
			output.writeObject("CLIENT - " + message);
			output.flush();
			showMessages("\n Client- " + message);
		}catch(IOException e){
			chatWindow.append("\n Error");
		}
	}
	
	//Change/updata chatWindow
	private static  void showMessages(final String m){
		SwingUtilities.invokeLater(
				
			new Runnable(){
			public void run(){
				chatWindow.append(m);
			}
		});
	
	}
	
	//Allows user to type
	private static void ableToType(final boolean toF){
		SwingUtilities.invokeLater(
				
				new Runnable(){
				public void run(){
					userText.setEditable(toF);
				}
			});
	}
	
}
