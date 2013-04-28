package com.piotrek.client.model;

import java.io.IOException;
import java.io.ObjectOutputStream;

import com.piotrek.server.beans.Message;

public class SendDataModel extends AbstractModel{

	// Attributes 
	private EstablishConnectionSupport connectionModel;
	private ObjectOutputStream out;

	// Constructor 
	public SendDataModel(EstablishConnectionSupport connectionModel) {
		this.connectionModel = connectionModel;
		out = connectionModel.getOut();
	}
	
	// Methods 
	public void sendData(Message msg){
		try {
			out.writeObject(msg);
			out.flush();
		} catch (IOException e) {
			connectionModel.closeConnection();  
		}
	}
}
