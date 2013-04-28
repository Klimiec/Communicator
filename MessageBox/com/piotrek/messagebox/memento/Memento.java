package com.piotrek.messagebox.memento;

public class Memento {

	// Attributes 
	private final String savedMessage;        

	// Constructor 
	public Memento(String savedMessage) {
		this.savedMessage = savedMessage;
	}

	// Getter
	public String savedMessage() {
		return savedMessage;
	}
}
