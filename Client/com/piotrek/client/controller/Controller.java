package com.piotrek.client.controller;

import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

import com.piotrek.client.model.AbstractModel;
import com.piotrek.client.model.HTMLmessageModel;
import com.piotrek.client.model.SendDataModel;
import com.piotrek.client.model.UserListJTreeModel;
import com.piotrek.client.view.Messanger;
import com.piotrek.loginbox.view.LoginBox;
import com.piotrek.messagebox.memento.CaretakerModel;
import com.piotrek.messagebox.memento.Memento;
import com.piotrek.messagebox.memento.OriginatorModel;
import com.piotrek.messagebox.view.MessageBox;
import com.piotrek.server.beans.Message;


public class Controller implements PropertyChangeListener {
	
	// Attributes : models 
	private Map<String, AbstractModel> modelMap;      // models
	
	// Attributes : views
	private Map<String, MessageBox> viewsMessageBox;  // okienka rozmow z konkretnymi osobami 
	private Messanger viewMessanger;                  // glowne okno programu 
	private LoginBox viewLoginBox;
	
	private String userNickSender;                    // nick usera który jest zalogowany w oknie gl. programu

	// Attributes : memento support 
	private Map<String, OriginatorModel> usersOriginatorModels;
	private Map<String, CaretakerModel> usersCaretakerModels;   
	
	
	// Constructors
	public Controller() {
		
		modelMap = new HashMap<String, AbstractModel>();
		viewsMessageBox = new HashMap<String, MessageBox>();
		usersOriginatorModels = new HashMap<>();
		usersCaretakerModels = new HashMap<>();
	}
	
//	public Controller(Messanger view) {      // Zablokowalem, ale to jest chyba do wywalenia 
//		setViewMessanger(view);
//	}
	
	// Settings Method : rejestruje modele w controllerze
	public void addModel(String nazwa, AbstractModel model){
		modelMap.put(nazwa, model);
		model.addPropertyChangeListener(this);      // Rejestruje Controller jak obiek nas³uchuj¹cy zdarzen w modelach
	}
	
	// Setters & Getters 
	public String getUserNick() {
		return userNickSender;
	}
	
	public void setUserNick(String userNick) {
		this.userNickSender = userNick;
	}
	
	public Messanger getViewMessanger() {  
		return viewMessanger;
	}
	
	public LoginBox getViewLoginBox() {
		return viewLoginBox;
	}
	
	
	/***********************************************************
	 *****  Metody controllera wywo³ywane w : LoginBox     *****
	 ***********************************************************/
	
	public void setViewLoginBox(LoginBox viewLoginBox) {
		this.viewLoginBox = viewLoginBox;
	}

	
	public void receivedLoginData(String userLoginNick, String userLoginStatus) {    // Dane otrzymane od uzytkownika - wpisane do LoginBox
		
		Message message = new Message(null, userLoginNick, userLoginStatus);         // Wys³anie sformatowanej wiadomoœci do serwera celem rejestracji 
		((SendDataModel) modelMap.get("sendDataModel")).sendData(message);
	}
	
	/*************************************************** LoginBox/ 
	
	
	
	
	/*********************************************************************
	 * Metody controllera wywo³ywane w Messanger (g³ówne okno aplikacji) *
	 *********************************************************************/
	
	public void setViewMessanger(Messanger view) {
		this.viewMessanger = view;
	}
	
	
	public void selectedUserFromJTreee(DefaultMutableTreeNode node) {           // Otwarcie okna rozmowy z konkretnym uzytkownikiem 
		
		Message message = (Message)node.getUserObject();
		final String choosenUserNickJTree = message.getNickFrom();              // Nazwa uzytkownika wybrana z JTree
		
		viewMessanger.resetTree();                                              // Odznacz zaznaczona opcje w JTree
		
		// Jezeli nie ma to stworz okno rozmowy z danym uyztkownikiem a jezeli jest to przywroc
		if (!viewsMessageBox.containsKey(choosenUserNickJTree)){
			
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					MessageBox frame = new MessageBox(Controller.this, choosenUserNickJTree);
					frame.setVisible(true);
				}
			};
			EventQueue.invokeLater(runnable);
			
			OriginatorModel originatorModel = new OriginatorModel(Controller.this, choosenUserNickJTree); // Tworzenie modeli na potrzeby obslugi memento
			usersOriginatorModels.put(choosenUserNickJTree, originatorModel);
			
			CaretakerModel caretakerModel = new CaretakerModel(Controller.this, choosenUserNickJTree);    // Ustaw ten controller jak nasluchiwacza tego co dzieje sie w tym modelu
			usersCaretakerModels.put(choosenUserNickJTree, caretakerModel);
			
		}else
			viewsMessageBox.get(choosenUserNickJTree).setVisible(true);                                   // Maksymalizacja okna wczesniej zminimalizowanego 
	}
	
	
    public void makeBroadcast(Message broadcastMessage) {        // Wys³anie komunikatu do wszystkich uzytkownikow na serwerze : pole chat & info 
		
		//------------------------------------------------------------- Online
		
		// Wyslanie golej, nie smormatowanej wiadomosci  - online
		((SendDataModel) modelMap.get("sendDataModel")).sendData(broadcastMessage);
		
		//-------------------------------------------------------------- Localnie 
		// Prze³anie wiadomosci do formatowania - localnie 
		((HTMLmessageModel) modelMap.get("htmLmessageModel")).convertStringToHTMLformatForBroadcastPane(broadcastMessage);
		
		getViewMessanger().erraseMessageTextArea();
	}
	
	public void changedStatus(String newStatus) {
		
		Message messageNewStatus =  new Message("broadcastNewStatus", userNickSender, newStatus);   // Wiadomosc ze zmieniona wartoscia statusu
		
		((SendDataModel) modelMap.get("sendDataModel")).sendData(messageNewStatus);                 // Wyslanie wiadomosci na server 
	}
	
	/************************************************************************** Messanger */
	
	
	
	
	
	/*****************************************************************************************************************
	 *****   Metody controllera wywo³ywane w : MessageBox (okno prowadzonej rozmowy z konkretnym uzytkownikiem)  *****
	 *****************************************************************************************************************/
	
	// Setter : view 
	public void addMessageBoxView(String user, MessageBox viewMessageBox){  
		viewsMessageBox.put(user, viewMessageBox);
	}
	
	 
	public void zamykanieMessageBox(String userNick) {  // TODO : przetestuj ten shit!
		viewsMessageBox.get(userNick).turnOffWindow();
		
		viewMessanger.resetTree();
		
		//NOWE! :  Jezeli nie ma tego uzytkownika na JTree to usun daneo tym oknie oraz z suportu dla mementu 
		((UserListJTreeModel) modelMap.get("userListJTreeModel")).isUserAtTheList(userNick);
	}
	
	/************************************************************************** MessageBox */
	
	
	
	
	
	
	
	/***************************************
	 *****   Obsluga wzorca memento    *****
	 ***************************************/
	
	// Wywo³ywane, ze ka¿dym razem kiedy zmienie sie wartosc w polu wiadomosci 
	public void setNewTextInOriginatorModel(String userWindowOwner, String textMessage){     
		usersOriginatorModels.get(userWindowOwner).setCurrentMessageJTextPaneCurrentContent(textMessage);  // Aktualizacja obiektu originatora uzytkownika podanego w parametrze
	}
	
	// Wywo³ywane, za kazdym razem kiedy wciskam przycisk sendButton
	public void addTextToConversationPanel(String userNickRecipient) {

		Memento newMemento = usersOriginatorModels.get(userNickRecipient).saveCurrentState();     // Pobranie nowego obiektu Memento 
		
		String textMessage = newMemento.savedMessage();                                           // Pobranie aktualnej tresci pola messageJTextPane
		
		usersCaretakerModels.get(userNickRecipient).addMemento(newMemento);                       // Wyslanie obiektu memento do zapisania : tworzenie histori 
		
		Message message = new Message(userNickRecipient, userNickSender, textMessage);            // Przygotowanie wiadomosci do wyslania 
		
		((HTMLmessageModel) modelMap.get("htmLmessageModel")).convertStringToHTMLformat(message); // Prze³anie wiadomosci do smofrmatowania : zarowno dla odbiorcy localnie jak i przez siec 
	}
	
	
	// Wywo³ane kiedy wciskam przycisk undoButton
	public void makeUndo(String userNickRecipient) {
		
		/*
		 * Opis: pobranie obiektu Memento z obiektu CaretakerModel na podstawie którego 
		 * zostanie przywrócony wczeœniejszy stan obiektu OriginatorModel przechowuj¹cy 
		 * infomracje o aktualnej zawartosci pola wiadomosci messageJTextPane
		 */
		
		Memento memento = usersCaretakerModels.get(userNickRecipient).retrieveMementoUndo();
		
		usersOriginatorModels.get(userNickRecipient).restoreStateFromMemento(memento);     // Przeslanie obiektu memento do Originatora w celu zmiany jego wewnetrznych danych  
	}
	
	public void makeRedo(String userNickRecipient) {
		// Pobranie obiektu Memento na podstawie które zostanie zmieniony obiekt Originator przechowujacy infomrajce o polu wiadomosci
		Memento memento = usersCaretakerModels.get(userNickRecipient).retrieveMementoRedo();
		
		// Przeslanie obiektu memento do Originatora w celu zmiany jego wewnetrznych danych
		usersOriginatorModels.get(userNickRecipient).restoreStateFromMemento(memento);
	}
	
	/**********************************************  Obsluga wzorca memento  */
	
	
	
	
	
	
	
	
	
	/********************************************************
	 *   Opis: obs³uga wiadomosci przychodz¹cych z modeli   *
	 *********************************************************/
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		
		/*
		 *  Opis: zmienna 'propertyName' zawiera instrukcje otrzymane z poszczegolnych modeli. 
		 *  Jest on ustawiana podczas informowania obiektow obserwujacych o zmianie stanu modelu. 
		 *  Na jej podstawie podejmowane beda decyzje co znalezy zrobic.
		 *  (ktory model sie zmienil i co z tego wynika dla funkcjonowania programu)
		 */
		String propertyName = evt.getPropertyName();
		
		if (propertyName.equals("newMessageFromReceiveDataModel")){
			
			/*
			 *  Opis: Pierwszy if obsluguje wiadomosci ktore zostaly otrzymane z modelu ReceiveDataModel.
			 *  Wiadomoœæ pierw zostaje rozpakowana podejmowane sa odpowiednie dzialania. 
			 *  Model ReceiveDataModel otrzymuje dane z servera za pomoca sieci
			 */
			
			
			Message message = (Message) evt.getNewValue();    // Rzutowanie otrzymanej wiadomosci na odpowiedni typ 
			
			/*
			 *  Opis: Pobranie odpowiednich pol z otrzymanej wiadomosci na podstawie 
			 *  ktorych beda podejmowane dalsze kroki. 
			 *  TODO : zastanow sie czy nie dalo by sie tego przerobic tak aby polecenia 
			 *  znajdowaly sie tylko w polu message obiektu Message. Bylo by to bardziej jednolite
			 *  Teraz jes to rozszucone miedzy message a nickTo co nie jest czytelne
			 */
			String nickTo = message.getNickTo(); 
			String messageContent = message.getMessage(); 
			
			
			
			 if (nickTo.equals("broadcastMessage")){  
				
				 /*
				  *  Opis: Otrzymano wiadomosc typu 'broadcast'. 
				  *  Nalezy ja odpowiednio obrobiæ w modelu HTMLmessageModel:
				  *  (przerobienie do postaci HTML oraz ustawienie tla)
				  */
				
				((HTMLmessageModel) modelMap.get("htmLmessageModel")).convertStringToHTMLformatForBroadcastPane(message);
				
			} else if (nickTo.equals("addNewUser")){  
				
				 /*
				  *  Opis: Otrzymano infomracje o tym, ze pojwil sie nowy uzytkownik. 
				  *  Nalezy poinformowac o tym model UserListJTreeModel ktory przechowuje 
				  *  infomracje o : nazwie + statusie uzytkownika w zmiennych typu DefaultMutableTreeNode 
				  *  na potrzeby wyswietlania infomracji o nich w JTree
				  */
				
                ((UserListJTreeModel) modelMap.get("userListJTreeModel")).addNewUser(message);
				
			} else if (messageContent.equals("uniqueNick")){   
				
				/* 
				 *  Opis: Dane podane podczas logowania zostaly zaakceptowane. 
				 *  Wy³¹czam okno logowania co skutkuje przywroceniem okna gl. programu. 
				 *  Nastepnie nalezy :
				 *  - ustawic status w oknie gl programu (comboBox)
				 *  - ustawic tytul okna glownego 
				 *  - przekazac do niego nazwe uzytkownika podana podczas procesu logowania
				 */
				
				
				String userName = message.getNickFrom();        // Nazwa uzytkownika podany podczas logowania 
				String status = message.getNickTo();            // Status uzytkownika podany podczas logowania
				int index;
				
				if (status.equals("Dostepny"))
					index = 0;
				else if (status.equals("Away")) 
					index = 1;
				else 
					index = 2;
				
				 
				setUserNick(userName);                          // Ustawienie nazwy 'wlasciciela' controllera
						
				getViewLoginBox().closeLoginBox();               
				getViewMessanger().setUserName(userName);        
				getViewMessanger().setWindowTitle(userName);
				getViewMessanger().setChoosenInitialStatus(index);
				
				
			} else if (messageContent.equals("wrongNick")){
				
				/*
				 *  Opis: Dane podane podczas logowania zostaly odrzucone. 
				 *  Poproœ u¿ytkownika o ponowne wpisanie nicku w oknie LoginBox. 
				 */
				
				getViewLoginBox().wrongNick();  
				
			}else if (messageContent.equals("removeUserFromJTree")){ 
				
				/*
				 *  Opis: Obslug sytuacji kiedy jakis uzytkownik opuscil server.
				 *  Nalezy poinformowac model UserListJTreeModel aby wyrzucil go z 
				 *  listy aktualnie dostepnym uzytkownikow. 
				 *  
				 *  TODO: zmien nazwe komunikatu z 'removeUserFromJTree'  na   'removeUser'
				 */
				
				((UserListJTreeModel) modelMap.get("userListJTreeModel")).removeUser(message);
				
				
			} else if (nickTo.equals("broadcastNewStatus")) { 
				
				/*
				 *  Opis: Obsluga sytuacji kiedy jakis uzytkownik zmienil status 
				 *  w momecie dzialania programu. Nalezy poinformowac o tym 
				 *  model UserListJTreeModel aby dla danego uzytkownika zmienil 
				 *  wartosc statusu w jego lisciu.
				 */
				
				((UserListJTreeModel) modelMap.get("userListJTreeModel")).changeStatus(message);
				
			}else if (!nickTo.equals("brak") && !message.getNickFrom().equals("brak")){
				
				/*
				 *  Opis: Obsluga odbierania wiadomosci do konkretnego uzytkowika. 
				 *  Po otrzymaniu wiadomosci nalezy albo przekazac ja do istniejacego 
				 *  okna rozmowy typu MessageBox dla danego uzytkownika albo stworzy to okno
				 *  z zainicjalizowanym komunikatem ktory otrzymalismy. 
				 *  
				 *  TODO : zastanow sie czy nie mozna wywalic tej obslugi wyjatek - po co ona w ogole tu jest?
				 */
				
				if (viewsMessageBox.containsKey(message.getNickFrom())) {                // Czy istnieje okno rozmowy z takim uzytkownikiem ?
					
					MessageBox messageBox = viewsMessageBox.get(message.getNickFrom());  // Pobranie referencji do okna
					messageBox.setVisible(true);                                         // Ustawia widocznosc ramki
					
					try {
						messageBox.addNewMessage(message.getMessage());                  // Przekazanie wiadomosci 
					} catch (Exception e) {/* handle exception here*/}
					
				} else {                                                                 // Nie ma takiego okna : stwarzam nowe okno 
					
					final String text = message.getMessage();
					final String user = message.getNickFrom();
					
					Runnable runnable = new Runnable() {
						@Override
						public void run() {
							MessageBox frame = new MessageBox(Controller.this, user, text);
							frame.setVisible(true);
						}
					};
					EventQueue.invokeLater(runnable);
					
					// Tworzenie modeli na potrzeby obslugi memento (Undo i Redo)
					OriginatorModel originatorModel = new OriginatorModel(Controller.this, message.getNickFrom());   
					usersOriginatorModels.put(message.getNickFrom(), originatorModel);
					
					CaretakerModel caretakerModel = new CaretakerModel(Controller.this, message.getNickFrom());  
					usersCaretakerModels.put(message.getNickFrom(), caretakerModel);
					
				}
			}  // Koniec obslugi wiadomosci odebranych z servera
			 
			 
			 /***************************************************************************************************
			  *  Opis: poni¿ej znajduje siê obdluga danych przychodz¹cych z innych modeli niz ReceiveDataModel. *
			  ***************************************************************************************************/
			 
		} else if (propertyName.equals("addNewUserToJTree")){   
			
			/*
			 *  Opis: Model UserListJTreeModel informuje o zapisaniu danych 
			 *  nowego uzytkownika w obiekcie DefaultMutableTreeNode oraz wewnetrznej mapie
			 *  Nastepnie nalezy przeslac obiekt liscia do widoku Messanger aby 
			 *  dodaæ go do listy JTree ktory wyswietla aktualnych uzytkownikow 
			 */
			
			DefaultMutableTreeNode newLeaf = (DefaultMutableTreeNode) evt.getNewValue();
			getViewMessanger().addUserToTheList(newLeaf);        // Przes³anie nowego liscia do widoku 
			
		} else if (propertyName.equals("removeUserFromJTree")){
			
			/*
			 * Opis: Model UserListJTreeModel informuje o  usunieciu uzytkownika z wewnetrznej pamieci. 
			 * Usuwany lisc przekazywany jest do controllera a nastepnie do widoku Messanger 
			 * w celu usuniecia go z listy JTree
			 */
			
			DefaultMutableTreeNode removeLeaf = (DefaultMutableTreeNode) evt.getNewValue();
			getViewMessanger().removeUserFromTheList(removeLeaf);
			
		} else if (propertyName.equals("HTMLmessageForBroadcast")){ 
			
			/*
			 * Opis: Model HTMLmessageModel przekazuje poprawnie sformatowan¹ wiadomoœæ 
			 * na potrzeby wyswietlenie jej w oknie rozmowy typu broadcast. 
			 * Otrzymana wiadomoœæ przesy³ana jest do widoku Messanger.
			 */
			
			try {
				getViewMessanger().addNewMessageToBroadcastPanel((String)evt.getNewValue());
			} catch (Exception ex){/* handle exception*/}
			
		} else if (propertyName.equals("newMessageHTMLLocal")){
			
			/*
			 *  Opis: Wiadomosc otrzymana z modelu HTMLmessageModel.
			 */
			
			String receiptUserNick = (String) evt.getOldValue();              // Nick uzytkownika z kotrym prowadze rozmowe - okno konwersacji
			MessageBox messageBox = viewsMessageBox.get(receiptUserNick);     // Pobranie referencji do okna do ktorego trzeba wyslac dane
			messageBox.eraseMessageJTextPane();                               // Wyzeruj pole gdzie wpisuje sie wiadomosc w oknie konwersacji  
			
			try {
				messageBox.addNewMessage((String) evt.getNewValue());         // Dodaj wiadomosc do pola konwersacji
			} catch (Exception e) { /* handle exception  here*/}
			
		} else if (propertyName.equals("newMessageHTMLSend")){
			
			/*
			 *  Opis: Model HTMLmessageModel przekazuje poprawnie sformatowana wiadomosc 
			 *  w formie HTML do wyslania przez siec dla konkretnego uzytkownika. 
			 *  Otrzymana wiadomosc nalezy przekazac modelowi wysylaj¹cemu ktory przesle j¹ na server.
			 */
			
			((SendDataModel) modelMap.get("sendDataModel")).sendData((Message) evt.getNewValue());      
			
	    } else if (propertyName.equals("changeUserStatus")) {
			
			/*
			 * TODO : O co tu kurwa chodzi ? :P :P 
			 * 
			 * odswieze cale drzewo
			 */
			
	    	
            getViewMessanger().refreshJTreeList();               	   // Wywo³anie funkcji odswiezajacej JTree: 
	    	
	    } else if (propertyName.equals("unlockUndoButton")){
			
			/*
			 *  Opis: Wiadomosc otrzymana z modelu CaretakerModel. 
			 *  Nalezy odblokowac przycisk undoButtom w widoku Messanger
			 *  nalezacego do konkretnego rozmowcy - przekazane jako parametr powiadomienia 
			 */
			
	    	String userNick = (String) evt.getOldValue();           
	    	viewsMessageBox.get(userNick).unlockUndoButton();          // Pobranie referencji do widoku w ktorym trzeba odblkowowac undoButton 
	    	
	    } else if (propertyName.equals("blockUndoButton")){
			
			/*
			 * Opis: Wiadomosc otrzymana z modelu CaretakerModel. 
			 * Zablokowanie przycisku undoButton.
			 */
	    	
	    	String userNick = (String) evt.getOldValue();
	    	viewsMessageBox.get(userNick).lockUndoButton();             // Pobranie referencji do widoku w ktorym trzeba zablokowac undoButton 
	    	
	    } else if (propertyName.equals("unlockRedoButton")){
			
			/*
			 *  Opis: Wiadomosc otrzymana z modelu CaretakerModel. 
			 *  Odblokowanie przycisku redoButton.
			 */
	    	
	    	String userNick = (String) evt.getOldValue();
	    	viewsMessageBox.get(userNick).unlockRedoButton();
	    	
	    } else if (propertyName.equals("blockRedoButton")){
			
			/*
			 * Opis: Wiadomosc otrzymana z modelu CaretakerModel. 
			 * Zablokowanie przycisku redoButton.
			 */
			
	    	String userNick = (String) evt.getOldValue();
	    	viewsMessageBox.get(userNick).lockRedoButton();
	    	
	    }else if(propertyName.equals("restoreStateFromMemento")){           // originatorNewValue
			
			/*
			 * Opis: zmienil sie model zawierajacy aktualna wartosc w polu tresci wiadomosci OriginatorModel:
			 * - wyzeruj pole wiadomosci 
			 * - ustaw pole wiadomosci na aktualna wartosc jaka ma w sobie originator
			 */
	    	String userName = (String) evt.getOldValue();
	    	viewsMessageBox.get(userName).eraseMessageJTextPane();
	    	viewsMessageBox.get(userName).setMessageJTextPane((String)evt.getNewValue());
	    	
		}else if(propertyName.equals("infoAboutUserInJTree")){
			
			/*
			 *  TODO : to jest zjebane, napraw to! 
			 * 
			 *  usunalem infomracje o danym oknie !
			 */
			
			String removeUser = (String)evt.getNewValue();
			
			usersCaretakerModels.remove(removeUser);
			usersOriginatorModels.remove(removeUser);
			viewsMessageBox.remove(removeUser);  
		}
	}
}
