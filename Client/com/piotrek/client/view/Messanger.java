package com.piotrek.client.view;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.piotrek.client.controller.Controller;
import com.piotrek.client.view.custom.ComboBoxRenderer;
import com.piotrek.client.view.custom.MyTreeCellRenderRenderer;
import com.piotrek.loginbox.view.LoginBox;
import com.piotrek.server.beans.Message;

@SuppressWarnings("serial")
public class Messanger extends JFrame {

	
	// Attributes: UI elements
	private JPanel contentPane;
    private JPanel usersPanel;
    private JPanel broadcastPanel;
    private JPanel messagePanel; 
    private JScrollPane scrollPane;
    private JScrollPane scrollPane_1;
    private JScrollPane scrollPane_2;
    private JTextPane broadcastTextPane;
    private HTMLEditorKit kit;
    private HTMLDocument conversationContent;
    private JTextArea messageTextArea;
	private JButton sendButton;
	@SuppressWarnings("rawtypes")
	private JComboBox statusComboBox;
	private ComboBoxRenderer renderer;
    private JTree usersTree;
    private DefaultMutableTreeNode rootNode ;   
    private DefaultTreeModel treeModel;
    
    
    // Attributes : 
    private Controller controller;  
    private String userName;        // Nazwa podana podczas logowania

    
	// Getters & Setters 
	public Controller getController() {
		return controller;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
    
    
    
    // Constructor 
	public Messanger(Controller controller) {
		
		setController(controller);                                                      // Ustawienie controllera 
		getController().setViewMessanger(this);         
		
		rootNode = new DefaultMutableTreeNode(new Message("Users", "Users", "Users"));  // Ustawienie listy JTree : lista uzytkownikow na serwerze
		treeModel = new DefaultTreeModel(rootNode); 
		usersTree = new JTree(treeModel);
		
		kit = new HTMLEditorKit();
		conversationContent = new HTMLDocument();
		
		setWindowsComponent();                                                         // Inicjalizacja obiektow UI
		setActionLIsteners();                                                          // Ustawienie Listenerów
	}
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setWindowsComponent(){
		
		setTitle("Messanger ");
		setIconImage(Toolkit.getDefaultToolkit().getImage(Messanger.class.getResource("/com/piotrek/client/resources/chat3.000.png")));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 657, 409);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//--
		
		usersPanel = new JPanel();
		usersPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Available users: ", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		usersPanel.setBounds(10, 16, 228, 291);
		contentPane.add(usersPanel);
		usersPanel.setLayout(null);
		
		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 21, 210, 258);
		usersPanel.add(scrollPane_1);
		
		//---
		
	    broadcastPanel = new JPanel();
		broadcastPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Chat & Info", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		broadcastPanel.setBounds(270, 16, 352, 259);
		contentPane.add(broadcastPanel);
		broadcastPanel.setLayout(null);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 16, 332, 232);
		broadcastPanel.add(scrollPane);
		
		broadcastTextPane = new JTextPane();
		broadcastTextPane.setEditable(false);
		broadcastTextPane.setEditorKit(kit);
		broadcastTextPane.setDocument(conversationContent);
		scrollPane.setViewportView(broadcastTextPane);
		
		//--
		
		messagePanel = new JPanel();
		messagePanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Message : ", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		messagePanel.setBounds(270, 281, 287, 64);
		contentPane.add(messagePanel);
		messagePanel.setLayout(null);
		
		scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(10, 16, 264, 37);
		messagePanel.add(scrollPane_2);
		
		messageTextArea = new JTextArea();
		messageTextArea.setFont(new Font("Verdana", Font.PLAIN, 11));
		messageTextArea.setMargin(new Insets(12, 15, 4, 5));
		scrollPane_2.setViewportView(messageTextArea);
		
		//--
		
		MyTreeCellRenderRenderer render = new MyTreeCellRenderRenderer();
		usersTree.setCellRenderer(render); 
	    
	    scrollPane_1.setViewportView(usersTree);
	    usersTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	    
	    //-- 
	    
		Integer[] intArray = {0, 1, 2};
		statusComboBox = new JComboBox(intArray);
		statusComboBox.setBounds(88, 318, 110, 23);
		renderer= new ComboBoxRenderer();
		renderer.setPreferredSize(new Dimension(130, 20));
		statusComboBox.setRenderer(renderer);
		contentPane.add(statusComboBox);
		
		
		//--
		
		sendButton = new JButton("");
		sendButton.setIcon(new ImageIcon(Messanger.class.getResource("/com/piotrek/client/resources/message.icon.png")));
		sendButton.setBounds(562, 296, 45, 35);
		contentPane.add(sendButton);
		
		//--
		
		JLabel statusLabel = new JLabel("Status : ");
		statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
		statusLabel.setBounds(20, 318, 58, 20);
		contentPane.add(statusLabel);
		
		new LoginBox(controller);     // Odpalenie okienka logowania 
	}
	
	
	/****************************************************************************************
	 ******             Ustawienie Listenerów na poszczególnych obiektach             *******
	 *****************************************************************************************/
	public void setActionLIsteners(){
		
		sendButton.addActionListener(new ActionListener() {   
			public void actionPerformed(ActionEvent arg0) {
				
				Message message = new Message("broadcastMessage", userName, messageTextArea.getText());
				System.out.println(" Messanger : --> Message[" +message.getNickTo()+", "+message.getNickFrom()+", "+message.getMessage()+"]" );
			    controller.makeBroadcast(message);
			}
		});
		
		
		usersTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) usersTree.getLastSelectedPathComponent();
				
				try{                                                                // tylko w momecie keidy wybrany element nie jest korzeniem
					if (node.isLeaf() && !node.isRoot())
						controller.selectedUserFromJTreee(node);
				}catch(Exception ex){
					/* handle exception here : exception when reoladed model */
				}
			}
		});
		
		
		statusComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				
				Integer nr = (Integer) ((JComboBox)arg.getSource()).getSelectedItem();
				String status = renderer.getUserStatus(nr);
				
				controller.changedStatus(status);
			}
		});
		
	}
	
	
	/****************************************************************************************
	 *  Methods invoked by the controller : change views property because model has changed *
	 *****************************************************************************************/
	
	public void addUserToTheList(DefaultMutableTreeNode user){               // Dodanie uzytkownika do listy JTree.
	    treeModel.insertNodeInto(user, rootNode, rootNode.getChildCount());
	    usersTree.scrollPathToVisible(new TreePath(user.getPath()));
	}
	
	public void removeUserFromTheList(DefaultMutableTreeNode user){          // Usuniecie uzytkownika z listy JTree
		treeModel.removeNodeFromParent(user);  
		refreshJTreeList();  
	}
	
	public void refreshJTreeList(){
		usersTree.repaint();
	}
	
	public void addNewMessageToBroadcastPanel(String message) throws BadLocationException, IOException{  // Dodanie wiadomosci (typu broadcast) do broadcastJTextPane
		kit.insertHTML(conversationContent, conversationContent.getLength(), message, 0, 0, null);
	}
	
	public void erraseMessageTextArea(){
		messageTextArea.setText("");
	}
	
	public void setWindowTitle(String userNick){
		this.setTitle("Messanger: " + userNick);
	}
	
	public void setChoosenInitialStatus(int index){
		statusComboBox.setSelectedIndex(index);
	}
	
	public void resetTree(){
		usersTree.clearSelection();
	}
	
}
