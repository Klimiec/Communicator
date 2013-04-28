package com.piotrek.client.view.custom;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.piotrek.client.view.Messanger;

/*
 * Opis: Klasa ta jest wykorzystywana w :
 * - oknie g³ównym programu Messanger : wybór statusu 
 * - oknie lgowania LoginBox : wybór statusu 
 */
@SuppressWarnings({ "serial", "rawtypes" })
public class ComboBoxRenderer extends JLabel implements ListCellRenderer{

	// Attributes 
	ImageIcon[] userStatusIcons = new ImageIcon[]{
			new ImageIcon(Messanger.class.getResource("/com/piotrek/client/resources/dostepny.gif")),
			new ImageIcon(Messanger.class.getResource("/com/piotrek/client/resources/zw.gif")),
			new ImageIcon(Messanger.class.getResource("/com/piotrek/client/resources/zajety.gif"))
	};
	String [] userStatusNames = new String[]{"Dostepny", "Away", "Zajêty"};
	
	
	// Constructor 
	public ComboBoxRenderer() {
		// Ustawienie textu wzglêdem ikonki
		setHorizontalAlignment(LEFT);
		setVerticalAlignment(CENTER);
	}
	
	// Methods 
	public String getUserStatus(int index){
		return userStatusNames[index];
	}

	@Override
	public Component getListCellRendererComponent(JList arg0, Object arg1,
			int arg2, boolean arg3, boolean arg4) {
		
		int selectedIndex = (Integer) arg1;
		
		ImageIcon icon = userStatusIcons[selectedIndex];
		String pet = userStatusNames[selectedIndex];
		setIcon(icon);
		setText(pet);
		setFont(arg0.getFont());
		
		return this;
	}
}
