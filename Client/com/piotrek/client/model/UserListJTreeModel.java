package com.piotrek.client.model;

import java.util.HashMap;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

import com.piotrek.server.beans.Message;


public class UserListJTreeModel extends AbstractModel {

	// Attributes 
	Map<String, DefaultMutableTreeNode> userMap;
	
	
	// Constructor 
	public UserListJTreeModel() {
		userMap = new HashMap<String, DefaultMutableTreeNode>();
	}
	
	public void addNewUser(Message message){ 
		String userNick = message.getNickFrom();                                   // Nazwa uzytkownika ktorego trzeba dodaæ 
		DefaultMutableTreeNode userLeaf = new DefaultMutableTreeNode(message);
		userMap.put(userNick, userLeaf);
		
		this.firePropertyChange("addNewUserToJTree", null, userLeaf);
	}
	
 
	public void removeUser(Message message){         

		String userToRemoveNick = message.getNickFrom();                            // Nazwa uzytkownika ktorego trzeba usunaæ 
		DefaultMutableTreeNode userLeafToRemove = userMap.get(userToRemoveNick);    // Lisc tego uzytkownika 
		userMap.remove(userToRemoveNick);                                           // Usun tego liscia z bazy 
		this.firePropertyChange("removeUserFromJTree", null, userLeafToRemove);     // Notify controller which user has to be remove from JTree
	}
	
	public void changeStatus(Message message){
		
		String userChangedStatus = message.getNickFrom();                           // Nazwa uzytkownika ktory zmienil stan
		DefaultMutableTreeNode userLeafToRemove = userMap.get(userChangedStatus);   // Lisc tego uzytkownika 
		Message nodeInfo = (Message) userLeafToRemove.getUserObject();              // Wartosc liscia 
		
		nodeInfo.setMessage(message.getMessage());                                  // Zmodyfikuje wartosc odpowiedzialna za pole status 
		
		this.firePropertyChange("changeUserStatus", null, "brak");                  // Czy nie moga byc dwa nule?
	}
	
	public void isUserAtTheList(String userName){                                   //TODO : Przerób to! To nie powinno informowac kontrollera! 
		if (!userMap.containsKey(userName))
			this.firePropertyChange("", userName, null);
	}
}





