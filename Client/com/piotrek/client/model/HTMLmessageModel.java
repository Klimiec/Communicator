package com.piotrek.client.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.piotrek.server.beans.Message;

/*
 *  Opis: business logic która odpowiednio przekszta³ca otrzymany string 
 *  do postaci w formie HTML który bêdzie nastêpnie dodany do conversationJTextPane. 
 *  Model ten ma przygotowaæ tylko odpowiedni HTML string.
 */

public class HTMLmessageModel extends AbstractModel {

	// Attributes
    private DateFormat dateFormat;
    private Calendar calendar;    
    private int number;
	
    
	// Constructor 
	public HTMLmessageModel(){
		dateFormat = new SimpleDateFormat("HH:mm:ss");
		calendar = Calendar.getInstance();
	}
	
	/*
	 *  Opis: Przerobienie stringa do postaci HTML. 
	 *  Ustawia odpowiednie tlo
	 *  - dla localnych wiadomoœci kolor tla to : #eeeeef
	 *  - dla wysylanych wiadomosci kolor tla to : #ffffff 
	 */
	public void convertStringToHTMLformat(Message message){
		
		String odbiorca = message.getNickTo();                 
		String nadawca = message.getNickFrom();
		String trescWiadomosci = message.getMessage();
		
		// Wiadomosc formatowana dla nadawcy : localnie
		String newMessage = "[" + dateFormat.format(calendar.getTime()) + "] <b>" + nadawca + "</b> :  " + trescWiadomosci + "\n";
		String stringHtml ="<p style=\"background-color:#eeeeef;\">" + newMessage + "</p>";
		
		this.firePropertyChange("newMessageHTMLLocal", odbiorca, stringHtml);
		
		//Wiadomoœæ która jest wysy³ana do innego uzytkownika : online 
		String sendStringHtml ="<p style=\"background-color:#ffffff;\">" + newMessage + "</p>";
		
		// Podmieniamy wartosc wiadomosci na sformatowany text w HTML + odpowiedni color 
		message.setMessage(sendStringHtml);
		
		this.firePropertyChange("newMessageHTMLSend", "brak", message);
	}
	
	/*
	 *  Opis: Formatowanie otrzymanej wiadomosc do postaci HTML
	 *  na potrzeby wiadomosci typu brodcast - formatowana localnie. 
	 */
	public void convertStringToHTMLformatForBroadcastPane(Message message){
		
		String stringHtml;
		String newMessage = "[" + dateFormat.format(calendar.getTime()) + "] <b>" + message.getNickFrom() + "</b> : " + message.getMessage() + "\n";
		
		if (number % 2 == 0)
			stringHtml ="<p style=\"background-color:#eeeeef;\">" + newMessage + "</p>";
		else
			stringHtml ="<p style=\"background-color:#ffffff;\">" + newMessage + "</p>";
		
		number++;
		
		this.firePropertyChange("HTMLmessageForBroadcast", null, stringHtml);
	}
}
