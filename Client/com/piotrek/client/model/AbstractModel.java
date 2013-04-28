package com.piotrek.client.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class AbstractModel {

	// Attributes
	private PropertyChangeSupport propertyChangeSupport;

	// Constructor 
	public AbstractModel() {
		this.propertyChangeSupport = new PropertyChangeSupport(this);
	}
	
	// Methods : manipulate propertyChangeSupport
	public void addPropertyChangeListener(PropertyChangeListener listener){
		propertyChangeSupport.addPropertyChangeListener(listener);
	}
	
	public void firePropertyChange(String notificationName, Object oldValue, Object newValue){
		propertyChangeSupport.firePropertyChange(notificationName, oldValue, newValue);
	}
}
