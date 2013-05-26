package com.piotrek.server.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.piotrek.server.beans.Message;

public class Server {

	// Attributes 
	private final ServerSocket serverSocket;         // Obs�uga w�tk�w klient�w
	private final ExecutorService clientsThreads;
	
	
	// Attributes : dane wsp�dzielone  
	private Map<String, ObjectOutputStream> mapUserConnetionOutputStreams; 
	private Map<String, String>  mapUserStatus;
	
	private int userCounter;                        // Ilosc uzytkownikow na serwerze
	
	
	// Constructor 
	/*
	 *  Opis: Construktor jak argumenty pobiera:
	 *  - serverSocketPort : numer portu servera 
	 *  - threadsAmount : ilo�� obs�ucgiwanych uzytkownikow 
	 */
	
	public Server(int serverSocketPort, int threadsAmount) throws IOException{
		serverSocket = new ServerSocket(serverSocketPort);
		clientsThreads = Executors.newFixedThreadPool(threadsAmount);
		
		// Inicjalizacja wsp�ldzielonych zasobow 
		mapUserConnetionOutputStreams = new HashMap<String, ObjectOutputStream>();
		mapUserStatus = new HashMap<String, String>();
	}
	
	
	/*
	 *  Opis: W nieskoczonej petli w w�tku g��wnym b�d� akceptowane 
	 *  po��czenia przychodz�ce na port servera a ich obs�ug� zajmie si� inny w�tek. 
	 *  W�tek g��wny servera zajmuje si� tylko akceptacj� po��cze�. 
	 */
    public void runServer() throws IOException{
    	while (true){
    		System.out.println("RegisterNewUsers() oczekuje na nowego usera do rejestracji ...");
    		
		    Socket incommingConnection = serverSocket.accept();               // Waiting for new connection : blocked
		 	clientsThreads.submit(new RequestHandler(incommingConnection));   // Execute in separate thread : client thread
		 	
		 	System.out.println("How many users: " + (++userCounter));         // Show info on the console
    	}
    }
    

	/*
	 *  Opis: 
	 *  Pobieranie strumienia wyjsciowego dla uzytkownika o podanym nicku. 
	 *  Jezeli uzytkownik zdarzyl juz opuscic serwer to zwrocony zostanie null. 
	 */
    // TODO : to musi byc thread safe! Wystarczy zatem, zeby mapa byla typu threadsafe 
	public synchronized ObjectOutputStream getRecipientObjectOutputStream(String recipientNick){
		return mapUserConnetionOutputStreams.get(recipientNick);
	}
    
    
	/*
	 *  Opis: 
	 *  Rejestrowanie w bazie nowego uzytkownika. 
	 *  W mapUserConnetionStreams zapisany zostanie : <Nazwa urzytkownika, Strumienie We/Wy>
	 *  W mapUserStatus zapisane zostnaie : <Nazwa uzytkownika, Status uzytkownika> 
	 */
	public boolean registerNewUser(String userNick, String userStatus, ObjectOutputStream outputObject) throws IOException{
		
		if (mapUserConnetionOutputStreams.containsKey(userNick))
			return false;
		else{
			
			synchronized(mapUserConnetionOutputStreams){ 
				
				// Wys�anie nowemu uzytkownikowi danych pozostalych uzytkownik�w : nick�w + statusow  ktorzy aktualnie przebywaja na serwerze 
				for (Map.Entry<String, ObjectOutputStream> entry : mapUserConnetionOutputStreams.entrySet()){
					
					String nick = entry.getKey();
					String status = mapUserStatus.get(nick);
					
					outputObject.writeObject(new Message("addNewUser", nick, status));  
					outputObject.flush();
				}
				
				// Zapisanie danych nowego uzytkownika
				mapUserConnetionOutputStreams.put(userNick, outputObject);
				mapUserStatus.put(userNick, userStatus);                          
				
				
				// Wyslanie do pozostalych uzytkownikow nazwy nowego uzytkownika 
				for (Map.Entry<String, ObjectOutputStream> entry : mapUserConnetionOutputStreams.entrySet())
					if (!entry.getKey().equals(userNick)){
						entry.getValue().writeObject(new Message("addNewUser", userNick, userStatus)); 
						entry.getValue().flush();
					}
			}
		}
		return true;
	} 
	
	
	/*
	 *  Opis: usuwa infomracje o uzytkowniku, kt�ry opuscil server
	 */
	public void remove(String removeUserNick) {
		
		mapUserStatus.remove(removeUserNick);  
		mapUserConnetionOutputStreams.remove(removeUserNick);
	}
	
	
	/*
	 * Opis: powiadomienie innych uzytkownik o tym, ze jakis uzytkownik wyszedl z servera i trzeba wywalic jego dane z JTree.
	 * Dodatkowo nalezy usunac dane o uzytkowniku - jego strumieniach i statusie bo go juz nie ma na serwerze 
	 */
	public void removeUserFromJTree(String userNick) {
		
		synchronized (mapUserConnetionOutputStreams){
			
			// Wyslanie do pozostalych uzytkownikow nazwy nowego uzytkownika 
			for (Map.Entry<String, ObjectOutputStream> entry : mapUserConnetionOutputStreams.entrySet())
				if (!entry.getKey().equals(userNick)){
					try {
						entry.getValue().writeObject(new Message(entry.getKey(), userNick, "removeUserFromJTree"));  
						entry.getValue().flush();
					} catch (IOException e) {/* handle exception */} 
				}
			
			// Wlasciwe usuniecie danych o uzytkowniku ktory opuscil serwer 
			mapUserConnetionOutputStreams.remove(userNick);
			mapUserStatus.remove(userNick);
		}
	}
	
	
    
	 /*  INNER CLASS RequestHandler:
	  * 
	  *  Opis:
	  *  > nawiazanie polaczenia z uzytkownikiem 
	  *  > rejestracja danych uzytkownika w zmiennej usersConnectionsInfo kt�ra jest wykorzystywana 
	  *  przez inne watki w celu uzyskiwania ifnomracji o uzytkownikach na podstawie ich nicku. 
	  *  > obs�uga przesy�ania wiadomosci mi�dzy poszczeg�lnymi uzytkownikami 
	  *  
	  *  Klasa ta korzysta ze wsp�dzielonego obiektu - ServerMap usersConnectionsInfo kt�y s�u�y 
	  *  jako baza danych o u�ytkownikach i mo�e by� modyfikowana przez kazd� klas� wewn�trzn�. 
	  */
    private class RequestHandler implements Runnable{
    	
    	// Attributes 
		ObjectInputStream inputObject;    // get data from user
		ObjectOutputStream outputObject;  // send data to user 
		Socket clientSocket;
		String userNick;           
		
		
		// Constructor : ustawia strumienie we/wy
		public RequestHandler(Socket socket) throws IOException{
			this.clientSocket = socket;
			inputObject = new ObjectInputStream(socket.getInputStream());
			outputObject =  new ObjectOutputStream(socket.getOutputStream());
		}
		
		
		// Getters & Setters 
		public String getUserNick() {
			return userNick;
		}
		public void setUserNick(String userNick) {
			this.userNick = userNick;
		}
		
		
		@Override
		public void run() {
			
			try{
				setConnectionParameters();
				messageListenerAndSender();  
				
			}catch(Exception ex){
				/* handle exception */
				// Tutaj zrob powiadomenie innych o tym, ze jakis uzytkownik opuscil program i trzeba go wywalic z listy JTree
				removeUserFromJTree(getUserNick());
			}finally{
				/* Humanitarne konczenie pracy z uzytkownikiem */
				
				closeClientStreams();              // zamkniecie strumieni 
				remove(getUserNick());             // usuniecie informacji o uzytkowniku 
				
				userCounter--;                     // Zmniejszenie licznika userow na serwerze  @@@@@ TODO: zr�b to thread safe!!!!!!
			}
			
			System.out.println("---> RequestHandler() client: " + getUserNick() + " |  Opu�ci� server , ilosc  users na serwerze: " + userCounter);
		}
		
		


		/*
		 *  Opis: funkcja pobiera od uzytkownika jego 'nick'.
		 *  Je�eli nick jest unikalny to dane o uzytkowniku zostan� zapisane w zmiennej usersConnectionsInfo
		 *  Je�eli uzytkownik o takim nicku juz istnieje na serwerze to czekaj ponownie na dane od uzytkownika. 
		 */
		private void setConnectionParameters() throws ClassNotFoundException, IOException {   
			
			boolean isRegistered = true;
			do {
		
				Message registrationMessage = (Message) inputObject.readObject();    // Odebranie danych ktore wczyta�em w oknie LoginBox 
				setUserNick(registrationMessage.getNickFrom());                      // Ustawienie parametru userNick na podstawie otrzymanych danych
				
				
				// Sprawdzanie bazy pod kontem poprawnosci danych -unikalnosci nicku, + rejestracja jezeli wszystko
			    isRegistered = registerNewUser(registrationMessage.getNickFrom(), registrationMessage.getMessage(), outputObject);  
				
				
				// Popro� u�ytkownika o ponowne podanie nicku je�eli taki user ju� jest zarejestrowany, albo zamknij LoginBox
				if (isRegistered == false)
					outputObject.writeObject(new Message("brak", "brak", "wrongNick"));    
				else
					outputObject.writeObject(new Message(registrationMessage.getMessage(), registrationMessage.getNickFrom(), "uniqueNick"));   // zamkniecie LoginBox
				
				outputObject.flush();          // wys�anie danych 
				
			} while(isRegistered == false); 
		}
		
		
		/*
		 *  Opis: Obs�uga wiadomo�ci przychodz�cych od uzytkownika. 
		 *  Informacja jest wys�ana zapakowana w obiekt typu Message.
		 *  Moze byc skierowana albo do pojedynczego uzytkownik - unicast 
		 *  Albo do wszystki uzytkownika aktualnie na serwerze - broadcast
		 */
		private void messageListenerAndSender() throws ClassNotFoundException, IOException{
			do{
				// Test 
				System.out.println("SERVER | RequestHandler | run() -> messageListenerAndSender() | czekam na dane od klienta - BLOKOWANIE");
				
				Message clientMessageObject = (Message) inputObject.readObject();                   // @ Flashpoint !! 
				
				String receiptNick = clientMessageObject.getNickTo();                               // Nick uzytkownika do ktorego trzeba wyslac wiadomosc
				
				
				if (receiptNick.equals("broadcastMessage")){   
					
					// Wysylanie tej samej wiadomosci do wszystkich uzytkownikow. - opr�cz siebie.  
					for (Map.Entry<String, ObjectOutputStream> clientEntry : mapUserConnetionOutputStreams.entrySet())
						if (!clientEntry.getKey().equals(clientMessageObject.getNickFrom())){  
							
							try {
								ObjectOutputStream out = clientEntry.getValue();
								out.writeObject(clientMessageObject);
								out.flush();
							} catch (IOException e) {
								/*  handle exception here  */
								/*
								 *  Opis: moze si� tak zdarzyc, �e w momecie wysy�ania danych do uzytkownika opu�ci on serwer. 
								 *  W takim wypadku zostanie rzucony wyjatek. Obs�uga powinna polegac na jakim� zapami�taniu 
								 *  tej wiadomo�ci w bazie do czasu az uzytkownik spowrotem si� pojawi. My na razie przymykamy na to oko.
								 *  Grunt to obsluzyc ten wyjatek tutaj, zeby serwer sie nie posypal jak zostanie zamkniety strumien do 
								 *  odbiorcy.
								 */
							}  
						}
				}else if (receiptNick.equals("broadcastNewStatus")){   
					/*
					 *  Opis: Wyslij do wszystkich uzytkownikow infomracje o 
					 *  tym, ze zmianie ulegl status jednego z uzytkownikow 
					 */
					
					// Zmodyfikuje wartosc mapy pamietajacej akutalne statusy uzytkownikow - zmiana globalna
					String userNick = clientMessageObject.getNickFrom();
					String userNewStatus = clientMessageObject.getMessage();
					mapUserStatus.remove(userNick);
					
					mapUserStatus.put(userNick, userNewStatus);
					
					// Wysylanie tej samej wiadomosci do wszystkich uzytkownikow. - opr�cz siebie : zmiana globalna
					for (Map.Entry<String, ObjectOutputStream> clientEntry : mapUserConnetionOutputStreams.entrySet())
						if (!clientEntry.getKey().equals(clientMessageObject.getNickFrom())){  // do kazdego wyslij oprocz siebie! Do siebie wysylam lokalnie!!!
						
							try {
								ObjectOutputStream out = clientEntry.getValue();
								out.writeObject(clientMessageObject);
								out.flush();
							} catch (IOException e) {
								
								/*  handle exception here  */
								/*
								 *  Opis: moze si� tak zdarzyc, �e w momecie wysy�ania danych do uzytkownika opu�ci on serwer. 
								 *  W takim wypadku zostanie rzucony wyjatek. Obs�uga powinna polegac na jakim� zapami�taniu 
								 *  tej wiadomo�ci w bazie do czasu az uzytkownik spowrotem si� pojawi. My na razie przymykamy na to oko.
								 *  Grunt to obsluzyc ten wyjatek tutaj, zeby serwer sie nie posypal jak zostanie zamkniety strumien do 
								 *  odbiorcy.
								 */
							}  //broadcastNewStatus
						}
					
				} else {   
					
					/*
					 *  Opis: obs�uga sytuacji w kt�rej trzeba wyslac dane do konkretnego uzytkowika.
					 *  Z bazy danych pobierany jest strumien wyjsciowy uzytkownika, do kt�rego trzeba wyslac wiadomo��.
					 */
					
					// Pobranie danych uzytkownika do kt�rego trzeba wyslac wiadomosc 
					ObjectOutputStream recipientObjectOutputStream = getRecipientObjectOutputStream(receiptNick);
					try {
						if (recipientObjectOutputStream != null){
							recipientObjectOutputStream.writeObject(clientMessageObject);
							recipientObjectOutputStream.flush();
						}
					} catch (IOException e) {
						
						/*
						 *  Opis: moze si� tak zdarzyc, �e w momecie wysy�ania danych do uzytkownika opu�ci on serwer. 
						 *  W takim wypadku zostanie rzucony wyjatek. Obs�uga powinna polegac na jakim� zapami�taniu 
						 *  tej wiadomo�ci w bazie do czasu az uzytkownik spowrotem si� pojawi. My na razie przymykamy na to oko.
						 *  Grunt to obsluzyc ten wyjatek tutaj, zeby serwer sie nie posypal jak zostanie zamkniety strumien do 
						 *  odbiorcy.
						 */
					}
				}
			}while (true);
		}
		
		
		/*
		 *  Opis: funkcja zamyka nawiazane polaczenia z serwerem. 
		 *  Dodatkowo usuwa z serwera zapisane wiadomosci o danym uzytkowniku. 
		 *  Serwer daje znac innym uzytkownikom o tym, ze dany user zakonczyl swoj pobyt na serwerze
		 */
		private void closeClientStreams(){
			try{
				inputObject.close();
				outputObject.close();
				clientSocket.close();
			}catch(Exception ex){/* handle exception*/}
		}
    } // inner class
}

