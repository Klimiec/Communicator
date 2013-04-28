package com.piotrek.messagebox.memento;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import com.piotrek.client.model.AbstractModel;

public class CaretakerModel extends AbstractModel{
	
	// Attributes
	private int currentIndex;       // pozycja najmlodszego memento na liscie
	private List<Memento> list;
	private String userNick;
	
	// Constructor 
	public CaretakerModel(){
		currentIndex = -1;          // wskazuje aktualna pozycje ktora nalezy pobrac dla Undo
	}
	
	public CaretakerModel(PropertyChangeListener listener, String user){
		this();
		this.addPropertyChangeListener(listener);
		this.userNick = user;
	}
	
	public void addMemento(Memento memento){
		
		System.out.println("CaretakerModel | addMemento(Memento memento) |  wartosc zapisywanego memento : " + memento.savedMessage());
		
		if (currentIndex == -1){                                          // dodanie na pocz¹tek 
			list = new ArrayList<>();
			list.add(memento);
			list.add(new Memento(""));
			currentIndex++;
			this.firePropertyChange("unlockUndoButton", userNick, null);  // odblokowanie przycisku undoButton
			
		}else if (currentIndex == list.size() - 2){                       // dodanie na koniec listy 
			currentIndex++;
			list.add(currentIndex, memento);
			
		}else{                                             
			currentIndex++;                                               // gdzieœ w œrodku : zmodyfikuj odpowiednio liste 
			list = list.subList(0, currentIndex);
			list.add(memento);
			list.add(new Memento(""));
		}
	}
	
	public Memento retrieveMementoUndo(){
		
		Memento memento = list.get(currentIndex);                         // Pobranie elementu z którego zostanie odtworzony stan 
		currentIndex--;
		
		this.firePropertyChange("unlockRedoButton", userNick, null);      // Odblokuj przycisk Redo 
		
		if (currentIndex == -1)                                           // Zablokowanie przycisk Undo 
			this.firePropertyChange("blockUndoButton", userNick, null);
		
		return memento;
	}
	
	public Memento retrieveMementoRedo(){ 
		
		this.firePropertyChange("unlockUndoButton", userNick, null);   // Odblokowanie przycisku Undo - jezeli byl zablokowany 
		
		currentIndex = currentIndex + 2;                               // Wyznaczenie nowej pozycji dla Undo 
		Memento memento = list.get(currentIndex);                      // Pobranie elementu z którego zostanie odtworzony stan. 
		
		if (currentIndex == list.size() - 1)                           // Zablokowanie przycisk Redo 
			this.firePropertyChange("blockRedoButton", userNick, null);
		
		currentIndex--;                                                // Aktualna pozycja do wykonania operacji undo
		
		return memento;
	}
}
