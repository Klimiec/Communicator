package com.piotrek.client.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/*
 *  Opis: Klasa nawiazuje po³¹czenie z serwerem.
 *  W wyniku czego otrzymujemy strumienie we/wy 
 *  które bêd¹ przekazane do innych modeli. 
 *  
 *  Obiekt tej klasy jest przekazywany do modeli : 
 *  - SendDataModel
 *  - ReceiveDataModel
 */
public class EstablishConnectionSupport{

	// Connection Attributes
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Socket clientSocket;
	
	// Constructor : nazwa hosta + nr portu
	public EstablishConnectionSupport(String host, int port){
		try {
			connectToServer(host, port);
			getStreams();
		} catch(Exception ex){/*handle exception */}
	}
	
	// Methods 
	private void connectToServer(String host, int port) throws UnknownHostException, IOException{    
		clientSocket = new Socket(host, port);
	}
	
	private void getStreams() throws IOException {  
		out = new ObjectOutputStream(clientSocket.getOutputStream()); // send to server
		in = new ObjectInputStream(clientSocket.getInputStream());    // get from server 
	}
	
	public void closeConnection(){   
		try{
			out.close();
			in.close();
			clientSocket.close();
		}catch (IOException ioException) {/* handleException */}
	}

	
	// Getters 
	public ObjectInputStream getIn() {
		return in;
	}
	public ObjectOutputStream getOut() {
		return out;
	}
	public Socket getClientSocket() {
		return clientSocket;
	}
}



