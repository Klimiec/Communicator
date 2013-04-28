package com.piotrek.messagebox.memento;

import java.beans.PropertyChangeListener;

import com.piotrek.client.model.AbstractModel;


/*
 *  Opis: Ten obiekt przechowuje infomracje o aktualnym stanie pole JTextPane : tresc wiadomosci 
 */

public class OriginatorModel extends AbstractModel{

	// Attributes 
	String jTextPaneCurrentContent;    // zawiera to co aktualnie jest trescia okienka tresci wiadomosci 
	String userNick;
	
	// Constructor 
	public OriginatorModel(){
		jTextPaneCurrentContent = "";
	}
	
	public OriginatorModel(PropertyChangeListener listener, String userNick){
		this();
		this.addPropertyChangeListener(listener);
		this.userNick = userNick;
	}

	// Setters & Getters 
	public String getMessageJTextPaneCurrentContent() {
		return jTextPaneCurrentContent;
	}
	
	
	 /*  Info: ustawia wartosc na aktualnie wpisywana w oknie wiadomosci.  */ 
	public void setCurrentMessageJTextPaneCurrentContent(String string){
		this.jTextPaneCurrentContent = string;
	}
	
	/* Memento support ***********************/
	
	/*
	 *  Info: Metoda ta bedzie wywolywana przez controller w momecie wcisniecia przycisku Send.
	 *  Nowy obiekt zwrocony zostanie do controllera a nastepnie poddany dalszej obrobce. 
	 *  Obiekt OriginatorModel oczywiscie nic o tym nie wiem - nic go to nie obchodzi. 
	 *  On ma tylko wiedziec jak zwrocic obiekt memento i jak przywrocic wczesniejszy stan na podstawie 
	 *  otrzymanego obiektu memento.
	 */
	public Memento saveCurrentState(){
		return new Memento(jTextPaneCurrentContent);
	}
	
	/*
	 *  Info: ta funkcja musi zrobic broadcasta do controllera poniewaz zmienily sie dane w modelu
	 *  Dlatego musi rozszerzac class AbstractModel. Te dane s¹ prezentowane w widoku w polu messageJTextPane
	 */
	public void restoreStateFromMemento(Memento memento){
		jTextPaneCurrentContent = memento.savedMessage();                    
		this.firePropertyChange("restoreStateFromMemento", userNick, jTextPaneCurrentContent);
	}
	
}
