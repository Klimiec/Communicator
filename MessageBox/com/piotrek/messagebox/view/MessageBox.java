package com.piotrek.messagebox.view;

import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import com.piotrek.client.controller.Controller;

@SuppressWarnings("serial")
public class MessageBox extends JFrame {
	
	// Attributes: set in the constructor 
	Controller controller;
	
	// Nazwa uzytkownika do ktorego to okno wysyla wiadomosc, pojawia sie tez w tytule // TODO : zmodyfikuj tytul
	String userWindowOwner;
	/*
	 * info: Wykorzystywane przy obs³udze przychodz¹cych wiadomosci. 
	 * Numer kazdego okna, to numer uzytkownika z ktorym prowadzony jest chat.
	 * Na podstawie tego numeru bêd¹ odnajdowane okna konwersacji, a je¿eli ich nie bêdzie, 
	 * to bêd¹ tworzone. 
	 */
	int windowNumber;  
	
	
	// Attributes: UI elements
	JTextPane conversationJTextPane;    
	JTextPane messageJTextPane;
	JButton redoButton;
	JButton settingsButton;
	JButton sendButton;
	JButton undoButton;
	
	JPanel panel;
	JPanel panel_1;
	JScrollPane scrollPane_1;
	JScrollPane scrollPane;
	
	// Used by the conversation JTextPane
	HTMLEditorKit kit = new HTMLEditorKit();
	HTMLDocument conversationContent = new HTMLDocument();
	

	
    // Constructor 1
	/**
	 * @wbp.parser.constructor
	 */
	public MessageBox(Controller controller, String userConversation) {
		
		/*
		 *  Info: Przekazanie do controllera referencji do obs³ugiwanego widoku
		 */
		this.userWindowOwner = userConversation;
		this.controller = controller;
		controller.addMessageBoxView(userConversation, this);   // Ustawienie mapMessageBox
		 
		
		setWindowsComponent();
		setActionLIsteners();
	} 
	
	// Constructor 2 
	public MessageBox(Controller controller, String userConversation, String receivedMessage) {
		
		/*
		 *  Info: Przekazanie do controllera referencji do obs³ugiwanego widoku
		 */
		this.userWindowOwner = userConversation;
		this.controller = controller;
		controller.addMessageBoxView(userConversation, this);   // Ustawienie mapMessageBox
		 
		
		setWindowsComponent();
		setActionLIsteners();
		
		// Dodanie nowej wiadomosci do okna wiadomosci 
		try{
			this.addNewMessage(receivedMessage);
		}catch(Exception ex){
			/* handle exception*/
			ex.printStackTrace();
		}
	} 
	
	
	private void setWindowsComponent(){
		
		// Set Window details 
		getContentPane().setFont(new Font("Arial", Font.PLAIN, 11));
		setTitle("MessageBox | Rozmowa z : " + userWindowOwner);
		setIconImage(Toolkit.getDefaultToolkit().getImage(MessageBox.class.getResource("/com/piotrek/resources/letterTopIcon.png")));
		setBounds(100, 100, 459, 409);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		getContentPane().setLayout(null);
		this.setLocationRelativeTo(controller.getViewMessanger());  
		
		// --------------------- Components not modified by the controller ---------- // 
	    panel = new JPanel();
		panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Conversation", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		panel.setBounds(16, 11, 405, 217);
		getContentPane().add(panel);
		panel.setLayout(null);
		
		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 22, 385, 184);
		panel.add(scrollPane_1);
		
		panel_1 = new JPanel();
		panel_1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_1.setBounds(16, 233, 405, 83);
		getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 385, 61);
		panel_1.add(scrollPane);
		
	
		// ------------------------- Components modified by the controller ---------- |
	    // --- Conversation window
		conversationJTextPane = new JTextPane();
		conversationJTextPane.setEditable(false);
		conversationJTextPane.setEditorKit(kit);
		conversationJTextPane.setDocument(conversationContent);
		scrollPane_1.setViewportView(conversationJTextPane);
		
		// --- Message window 
		messageJTextPane = new JTextPane();
		scrollPane.setViewportView(messageJTextPane);
		messageJTextPane.setMargin(new Insets(15, 15, 5, 5));
		
		// --- Buttons
		redoButton = new JButton("Redo");
		redoButton.setEnabled(false);
		redoButton.setToolTipText("Redo");
		redoButton.setIcon(new ImageIcon(MessageBox.class.getResource("/com/piotrek/resources/Redo.png")));
		redoButton.setBounds(108, 337, 82, 23);
		getContentPane().add(redoButton);
		
		undoButton = new JButton("Undo");
		undoButton.setEnabled(false);
		undoButton.setToolTipText("Undo");
		undoButton.setIcon(new ImageIcon(MessageBox.class.getResource("/com/piotrek/resources/Undo(3).png")));
		undoButton.setBounds(16, 337, 82, 23);
		getContentPane().add(undoButton);
		
		settingsButton = new JButton("");
		settingsButton.setToolTipText("Text Settings");
		settingsButton.setIcon(new ImageIcon(MessageBox.class.getResource("/com/piotrek/resources/settings.png")));
		settingsButton.setBounds(207, 337, 50, 23);
		getContentPane().add(settingsButton);
		
		sendButton = new JButton(" Send");
		sendButton.setIcon(new ImageIcon(MessageBox.class.getResource("/com/piotrek/resources/sendIcon.png")));
		sendButton.setBounds(328, 337, 93, 23);
		getContentPane().add(sendButton);
		
	}
	
	
	public void setActionLIsteners(){
		
		/* TODO: Przerob pobieranie gotowej wiadomosci aby pobierac jej tresc z obiektu Originator a nie bezposrednio z GUI
		 * 
		 */
		sendButton.addActionListener(new ActionListener() {  // # PRZETESTOWANO
			public void actionPerformed(ActionEvent e) {
				// TEST 
				System.out.println("MessageBox | sendButton() - user: " + userWindowOwner);
				
				/*
				 *    Opis: Wywo³ane w momêcie wciœniêcia przycisku sendButton. 
				 *    Akcja ma spowodowaæ:
				 *  - dodanie treœci messageJTextPane do conversationJTextPane 
				 *    przy czym dane pobierane s¹ z modelu a nie widoku.
				 *  - wyzerowanie pola messageJTextPane 
				 *  - odblokowanie przycisku undoButton (operacja cofania)  
				 *  - wyslanie odpowiednio sformatowanej wiadomosci do innego uzytkowmnika przez siec
				 */
				
				// #NOWE: to ma docelowo zastapic metoda ktora jest nizej zakomentowana 
				/*
				 *  Tresc wiadomosci pobierana jest z obiektu originator a nie bezposrednio z GUI
				 */
				controller.addTextToConversationPanel(userWindowOwner); 
				
//				// Wyslanie do controllera : Nazwa Uzytkownika do ktorego wyslac + tresc wiadomosci 
//				controller.sendMessageFromMessageBox(userNickRecipient, messageJTextPane.getText());
			}
		});
		
		
		undoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TEST 
				System.out.println("MessageBox | undoButton");
				
				/*
				 *    Opis: wywo³ane w momêcie klikniêcia przycisku undoButton.
				 *    Akcja ma spowodowaæ:
				 *  - pobranie wczeœniejszego stanu messageJTextPane z obiektu CaretakerModel
				 *  - ustawienie pola messageJTextPane na podstawie otrzymanego obiektu Memento z CaretakerModel
				 *  - odblokowanie przycisku redoButton
				 *  - w pewnych warunkach zablokowanie przycisku undoButton
				 *  - wyzerowanie pola wiadomoœci messageJTextPane (przed ustawieniem nowej wartosci)
				 */
				
				// #NOWE : przekazuje nazwe uzytkownika - osoby z ktory rozmawiam, aby poznac 
				controller.makeUndo(userWindowOwner);
			}
		});
		
		
		redoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// TEST 
				System.out.println("MessageBox | redoButton");
				
				/*
				 *   Opis: wywo³ane w momêcie klikniêcia przycisku undoButton.
				 *   Akcja ma spowodowaæ:
				 *   - pobranie pó¿niejszego stanu messageJTextPane z obiektu CaretakerModel
				 *   - ustawienie pola messageJTextPane na podstawie otrzymanego obiektu Memento z CaretakerModel
				 *   - odblokowanie przycisku unddoButton
				 *   - w pewnych warunkach zablokowanie przycisku redoButton
				 *   - wyzerowanie pola wiadomoœci messageJTextPane (przed ustawieniem nowej wartosci)
				 */
				controller.makeRedo(userWindowOwner); 
			}
		});
		
		
		messageJTextPane.addKeyListener(new KeyAdapter() {   //# SPRAWDZONE 
			public void keyReleased(KeyEvent e) {
				// TEST 
				System.out.println("MessageBox | messageJTextPane.addKeyListener() | aktuala wartosc msg : " + messageJTextPane.getText() + ", user: " + userWindowOwner);
				
				/*
				 *  Info : wysy³a aktualn¹ treœæ okna wiadomoœci messageJTextPane do modelu OriginatorModel 
				 *  który przechowuje biezace informacje o tym polu. Metoda jest wywo³ywana przy kazdorazowym 
				 *  zmianie tresci pola! 
				 *  
				 *  Opis: 
				 *  - zmienia aktualn¹ zawartoœæ obiektu OriginatorModel. 
				 */
				// #NOWE
				controller.setNewTextInOriginatorModel(userWindowOwner, messageJTextPane.getText());
			}
		});
		
		
		settingsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*
				 *  TODO : dorobic okno z wyborem czcionki + ustawien
				 */
			}
		});
		
		
		// TODO :  Akcje podczas zamykania okna 
		this.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				controller.zamykanieMessageBox(userWindowOwner);
			}
		});
	}
	
	//------------------------------------------------------------------------ | 
	// Methods invoked by the controller : change views property because model has changed
	
	
	// Add a new message to the conversation JTextPane
	public void addNewMessage(String message) throws BadLocationException, IOException{
		kit.insertHTML(conversationContent, conversationContent.getLength(), message, 0, 0, null);
	}
	
	// Erase message JTextPane 
	public void eraseMessageJTextPane(){
		messageJTextPane.setText("");
	}
	
	// Set message JTextPane
	public void setMessageJTextPane(String message){
		messageJTextPane.setText(message);
	}
	
	// Unlock redo button
	public void unlockRedoButton(){
		redoButton.setEnabled(true);
	}
	
	// Lock redo button
	public void lockRedoButton(){
		redoButton.setEnabled(false);
	}
	
	// Unlock undo button
	public void unlockUndoButton(){
		undoButton.setEnabled(true);
	}
	
    // Lock undo button 
	public void lockUndoButton(){
		undoButton.setEnabled(false);
	}
	
	// Set a new font for message JTextArea
	public void setNewFont(Font newFont){
		messageJTextPane.setFont(newFont);
	}
	
	// Przywracanie okna : widocznosc 100 
	public void setVisi(){
		setVisible(true);
	}

	// Zamykanie okna : widocznosc 0 
	public void turnOffWindow() {
		setVisible(false);
		
	}
}


