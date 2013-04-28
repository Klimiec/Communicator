package com.piotrek.client.view.custom;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.piotrek.client.view.Messanger;
import com.piotrek.server.beans.Message;

/*
 *  Opis: Klasa ta jest uzywana przez JTree w oknie g³. programu : Messanger 
 *  Wykorzystywana jest do renderowania listy uzytkowników i ich statusów na serwerze 
 */
@SuppressWarnings("serial")
public class MyTreeCellRenderRenderer extends DefaultTreeCellRenderer{

	// Attributes 
	
	/*
	 *  Komentarz: tablica userStatusIcons jest taka sama jak w ComboBoxRenderer.
	 *  Lepiej zrobic oddzielna klase która przechowywa³a by te dane! 
	 */
	ImageIcon[] userStatusIcons = new ImageIcon[]{
			new ImageIcon(Messanger.class.getResource("/com/piotrek/client/resources/dostepny.gif")),
			new ImageIcon(Messanger.class.getResource("/com/piotrek/client/resources/zw.gif")),
			new ImageIcon(Messanger.class.getResource("/com/piotrek/client/resources/zajety.gif"))
	};
	ImageIcon usersIcon = new ImageIcon(Messanger.class.getResource("/com/piotrek/client/resources/Users.icon.png"));
	
	
    public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean sel,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus) {

           super.getTreeCellRendererComponent(
            tree, value, sel,
            expanded, leaf, row,
            hasFocus);
           
           DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
           Message nodeInfo = (Message) node.getUserObject();
           
           String userStatus = nodeInfo.getMessage();
           String userName = nodeInfo.getNickFrom();
           
	        if (leaf == true && !node.isRoot()) {
	        	
		        if (userStatus.equals("Dostepny"))
		        	setIcon(userStatusIcons[0]);
		        else if (userStatus.equals("Away"))
		        	setIcon(userStatusIcons[1]);
		        else 
		        	setIcon(userStatusIcons[2]);
	        }else
	        	setIcon(usersIcon);
	        
	        setText(userName);
	        
	        if (selected)
	        	super.setBorderSelectionColor(null);
	        
           return this;
    }
}
