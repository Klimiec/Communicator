package com.piotrek.client.model;

import java.io.ObjectInputStream;

import com.piotrek.server.beans.Message;


public class ReceiveDataModel extends AbstractModel implements Runnable {

	// Attributes 
	EstablishConnectionSupport connectionModel;
	ObjectInputStream in;
	
	
	// Constructor 
	public ReceiveDataModel(EstablishConnectionSupport connectionModel) {
		this.connectionModel = connectionModel;
		this.in = connectionModel.getIn();
	}
	
	
	/*
	 *  Opis: w¹tek ten bêdzie zajmowa³ siê obs³ug¹ wiadomoœci przychodz¹cych 
	 *  do klienta. Klient mo¿e otrzymaæ ró¿ne rodzaje wiadomoœci i na tej podstawie nale¿y podj¹æ odpowiednie 
	 *  kroki - tym zajmie siê controller. W¹tek ten ma tylko odebraæ wiadomoœc i przes³aæ j¹ do kontrolera 
	 *  gdzie nast¹pi odpowiednia obróbka danych. 
	 */
	@Override
	public void run() {
		
		do{
			try {
				Message message = (Message) in.readObject();                               // Blokowanie na czas nadejscia wiadomosci
				this.firePropertyChange("newMessageFromReceiveDataModel", null, message);  // Notify controller about new message 
				
			} catch (Exception e){                                                         // In case of error close connections
				connectionModel.closeConnection();
				break;                                                                     // Po bledzie opusc petle 
			}
		}while (true);
	}
}
