package com.derp.chat;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Server {
	
	private static final int PORT = 50007;
	public static HashSet<String> names = new HashSet<String>();
	public static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();
	public static ServerSocket serverSocket;
	public static JFrame frame = new JFrame("Chat Server v0.0.5 - Console");
	private static JTextField textField = new JTextField(40);
	public static JTextArea messageArea = new JTextArea(8, 40);
	private static JScrollPane scrollPane = new JScrollPane(messageArea);
	public static HashMap<String, JLabel> users = new HashMap<String, JLabel>();
	public static JPanel userTab = new JPanel();
	private static JPanel main = new JPanel();
	
	public static void main(String[] args) {
		frame.setPreferredSize(new Dimension(640, 480));
		frame.setResizable(false);
		messageArea.setEditable(false);
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
		textField.setMaximumSize(new Dimension(600, 25));
		main.add(scrollPane);
		main.add(textField);
		userTab.setLayout(new BoxLayout(userTab, BoxLayout.Y_AXIS));
		frame.getContentPane().add(main, "East");
		frame.getContentPane().add(userTab, "West");
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss");
				String formattedDate = df.format(new Date());
				for (PrintWriter writer : writers) {
					writer.println("/broadcast Console> " + textField.getText());
				}
				messageArea.append("[" + formattedDate + "]" + " Console> " + textField.getText() + "\n");
				textField.setText("");
				
			}
		});
		
		messageArea.append("Chat server now running...\n");
		while (true) {
			try {
				serverSocket = new ServerSocket(PORT);
			} catch (IOException e) {}
			try {
				while (true) {
					new ChatHandler(serverSocket.accept()).start();
				}
			} catch (IOException e) {
				
			} finally {
				try {
					serverSocket.close();
				} catch (IOException e) {}
			}
		}
	}
}
