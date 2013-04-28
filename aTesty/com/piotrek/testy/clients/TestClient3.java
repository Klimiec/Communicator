package com.piotrek.testy.clients;

import java.awt.EventQueue;

import com.piotrek.client.controller.Controller;
import com.piotrek.client.model.EstablishConnectionSupport;
import com.piotrek.client.model.HTMLmessageModel;
import com.piotrek.client.model.ReceiveDataModel;
import com.piotrek.client.model.SendDataModel;
import com.piotrek.client.model.UserListJTreeModel;
import com.piotrek.client.view.Messanger;

public class TestClient3 {

	public static void main(String[] args) {
		
		
		
		
		// 1.
		/*
		 *  CONTROLER : zarzadza glownym widokiem programu. 
		 *  Nas³uchuje wiadomosci wejsciowe itd 
		 */
		final Controller controller = new Controller();
		System.out.println("Controller - OK");
		
		
		
		// 2. 
		/* OBIEKT POMOCNICZY : uzyskanie strumieni we/wy
		 * Opis: nawiazuje polaczenie z serverem efektem czego 
		 * jest uzyskanie strumieni we/wy które nastêpnie s¹ 
		 *  przekazywane do odpowiednich modelów. 
		 *  
		 *  Tutaj mamy zajac sie tylko uzyskaniem tych strumieni, rejestracja
		 *  uzytkownika nastapi dalej! 
		 *  
		 *  Uwaga: wykorzystywany przez model wysy³aj¹cy i odbieracj¹cy (delegalizacja) 
		 *  Nie rejestruje zadnego nas³uchiwacza!
		 */
		EstablishConnectionSupport establishConnectionModel = new EstablishConnectionSupport("localhost", 8389);
		System.out.println("Connection - OK");
		
		
		// 4. Konfiguracja modeli odbioru i wysy³ania wiadomosci 
		
		// 4.1 
		/* MODEL WYSY£AJACY DANE 
		 * Opis: Jako parametr pobiera obiekt EstablishConnectionSupport z którego uzyskuje strumienie 
		 * do servera. Zajmuje sie wylaczniem przeslaniem wiadomosci 
		 * na server - nie wnika w tresc wiadomosci, ani jej nie formatuj.
		 * Zajmuje sie tym controller i on przesyla gotowa wiadomosc 
		 * do wyslania do tego modelu
		 */
		SendDataModel sendDataModel = new SendDataModel(establishConnectionModel);
		controller.addModel("sendDataModel", sendDataModel);
		System.out.println("Send Model - OK");
		
		
		// 4.2 
		/* MODEL ODBIERAJACY DANE 
		 * Opis: Sledzi wejscie w |odzielnym watku| na okolicznosc pojawienia sie wiadomosci. 
		 * Kiedy wiadomosc sie pojawia (juz sformatowana do postaci stringHTML) 
		 * powiadamia o tym controllera za pomoca powiadomienia.
		 * 
		 * Jako parametr pobiera strumien wyjciowy z którego ma czytac 
		 * przychodzace dane.
		 */
		ReceiveDataModel receiveDataModel = new ReceiveDataModel(establishConnectionModel);
        controller.addModel("receiveDataModel", receiveDataModel);
		Thread thread = new Thread(receiveDataModel);  // Odpal obsluge przychodzacych wiadomosci w oddzielnym watku! 
		thread.start();
		System.out.println("Receive Model - OK");
		
		// 5.
		/* MODEL REJESTRUCJ¥CY UZYTKOWNIKÓW 
		 * Opis: wykorzystywany jest przez JTree jako zbiór danych które maj¹ zostac wyswietlone. 
		 * Obiekty przechowywne sa w wewnetrznej mapie w formie <String, DefaultMutableTreeNode>
		 * Obiekt ten rejestruje controller jako swojego obserwatora. 
		 */
		UserListJTreeModel userListModel = new UserListJTreeModel();
		controller.addModel("userListJTreeModel", userListModel);
		System.out.println("JTree Model - OK");

		// 6.
		/* MODEL PARSUJACY WIADOMOSC NA POTRZEBY OKIENKA DO OBSLUGI BROADCASTU 
		 * Opis: model ten wykorzystywany jest przy dodawaniu wiadomosci typu 
		 * broadcast do okienka broadcastJTextPane jak i rowniez do obslugi formatowania 
		 * wiadomosci w okineku wiadomosci (osobne dla kazdej konwersacji) 
		 */
		HTMLmessageModel htmLmessageModel = new HTMLmessageModel();
		controller.addModel("htmLmessageModel", htmLmessageModel);
		System.out.println("HTML message Model - OK");
		
		
		
		// 7.
		/*
		 *  Opis: tutaj ma byc nawiazanie polaczenia z serverem. 
		 *  Wyslanie walsciwych danych
		 */
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					LoginBox dialog = new LoginBox();
//					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//					dialog.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
		
		
		// 2.
        /*
         * VIEW : na koncu odpalam widok 
         */
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Messanger frame = new Messanger(controller);
				frame.setVisible(true);
			}
		};
		EventQueue.invokeLater(runnable);
		System.out.println("Widok - OK");
		
		
		
		
	}

}
