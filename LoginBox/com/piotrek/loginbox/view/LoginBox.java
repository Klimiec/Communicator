package com.piotrek.loginbox.view;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import com.piotrek.client.controller.Controller;
import com.piotrek.client.view.custom.ComboBoxRenderer;

@SuppressWarnings("serial")
public class LoginBox extends JDialog {
	
	
	// Attributes
	private final Controller controller;
	private ComboBoxRenderer renderer;
	
	
	// Attributes : UI 
	private JTextField nickTextField;
	private JLabel lblNewLabel;

	
	// Constructor 
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public LoginBox(Controller controller) {
		
		this.controller = controller;
        this.controller.setViewLoginBox(this);
		
		
		setTitle("Login");
		setIconImage(Toolkit.getDefaultToolkit().getImage(LoginBox.class.getResource("/com/piotrek/loginbox/resources/key-32.gif")));
		setBounds(100, 100, 293, 240);
		getContentPane().setLayout(null);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Login : ", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(16, 16, 231, 169);
		getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(6, 16, 219, 147);
		panel_1.add(panel);
		panel.setLayout(null);
		
		lblNewLabel = new JLabel("Nick : ");
		lblNewLabel.setBounds(10, 24, 35, 14);
		panel.add(lblNewLabel);
		
		nickTextField = new JTextField();
		nickTextField.setBounds(55, 21, 143, 20);
		panel.add(nickTextField);
		nickTextField.setColumns(10);
		
		JLabel lblStatus = new JLabel("Status : ");
		lblStatus.setBounds(10, 62, 46, 14);
		panel.add(lblStatus);
		
		final JComboBox statusComboBox = new JComboBox(new Integer[] {0, 1, 2});
		renderer= new ComboBoxRenderer();
		statusComboBox.setRenderer(renderer);
		statusComboBox.setBounds(55, 59, 143, 20);
		panel.add(statusComboBox);
		
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				LoginBox.this.controller.receivedLoginData(nickTextField.getText(), renderer.getUserStatus(statusComboBox.getSelectedIndex()));
			}
		});
		okButton.setBounds(69, 110, 89, 23);
		panel.add(okButton);
		
		setModal(true);                                       // Blokuj pozostale okienka! 
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}
	
	
	public void closeLoginBox(){
		setVisible(false);
		dispose();
	}
	
	
	/*
	 *  Opis: Je¿eli podane dane by³y juz wczesniej zarejestrowane to :
	 *  - wyzeruj pole tekstowe na ponowne wprowadzenie danych 
	 *  - ustaw etykiete nicku na kolor czerowny - sygnalizacja problemu
	 */
	public void wrongNick(){
		nickTextField.setText("");
		lblNewLabel.setForeground(Color.RED);
	}
	
}
