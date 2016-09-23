package gamechat;
  

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


    public class Server extends JFrame {
    private JTextField userText;//made a j text field for your texts that you will send
    private JTextArea chatWindow;//made chat window to view others text
    private ObjectOutputStream output;//made variable to out put text to the other computer 
    private ObjectInputStream input;//made variable to recve the text
    private ServerSocket server; //made some thing that can recive texts
    private Socket connection;
    
    //constructor
    public Server(){
            super("pushApp");
            userText = new JTextField();//made box where you can enter text and send it
            userText.setEditable(false);//made the conversaion not editable 
            userText.addActionListener(//made actoin listener to check if you pressed enter
                            new ActionListener(){
                                    public void actionPerformed(ActionEvent event) {
                                            sendMessage(event.getActionCommand ());//what to do when user sends message
                                            userText.setText("");//made it so after you enter text and send it it gets cleared	
                                    }

                            }

                            );
            add(userText , BorderLayout.NORTH);
            chatWindow = new JTextArea();
            add(new JScrollPane(chatWindow));
            setSize(300,150);
            setVisible(true);
    }
    //set up and run the server
    public void startRunning(){
        try {
            server= new ServerSocket(6789, 100);
            while(true){
                
                try {
                    
                    waitForConnection();
                    setupStreams();
                    whileChatting();
                    
                    //Connect and have conversation
                }catch(EOFException e){
                    showMessage("\n Server ended the connection");
                } finally {
                    closeCrap();
                }
                
                
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    //wait for connection
    public void waitForConnection() throws IOException{
            showMessage("Waiting for someone to connect...\n");//shows message that your not connected
            connection= server.accept();
            showMessage("Now connected to " + connection.getInetAddress().getHostName());
        }
    
    //get stream to send and recieve data
    private void setupStreams() throws IOException{
        output= new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input= new ObjectInputStream(connection.getInputStream());
        showMessage("\n Streams are now set \n");
    }
    
    //during the chat conversation
    
    public void whileChatting() throws IOException{
        
        String message= " You are now connected ";
        sendMessage(message);
        ableToType(true);
        do {
            //Have a conversation
            try {
                message= (String) input.readObject();
                showMessage("\n" + message);
                
            }catch(ClassNotFoundException e){
                showMessage("\n No idea what the user sent!");
            }
            
        }while(!message.equals("CLIENT - END"));
    }
    
    //close streams after you are done chatting
    
    public void closeCrap(){
        
        showMessage("\n Closing Connections... \n");
        ableToType(false);
        try {
        	output.close();
        	input.close();
        	connection.close();
        	
        }catch(IOException e){
        	e.printStackTrace();
        }
        
    }
    
    //send a message to client
    public void sendMessage(String message){
    	
    	try {
    		
    		output.writeObject("SERVER - " + message);
    		output.flush();
    		showMessage("\n SERVER - " + message);
    		
    	}catch(IOException e){
    		chatWindow.append("\n ERROR: DUDE I CANT SEND THAT MESSAGE");
    	}
    	
    }
    
    //updates chatWindow, i.e you are able to update parts of the gui
    private void showMessage(final String text){
    	SwingUtilities.invokeLater(
    			new Runnable(){
    				public void run(){
    					chatWindow.append(text);
    				}
    			}
    			
    	);
    }
    
    //let the user type stuff in their text field
    private void ableToType(final boolean toF){
    	SwingUtilities.invokeLater(
    			new Runnable(){
    				public void run(){
    					userText.setEditable(toF);
    				}
    			}
    			
    	);
    }
    
 }