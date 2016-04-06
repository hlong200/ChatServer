package com.derp.chat;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JLabel;

public class ChatHandler extends Thread {
	
	private String name;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	
	public ChatHandler(Socket socket) {
		
		this.socket = socket;
		
	}
	
	public void run() {
		SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss");
		String formattedDate = df.format(new Date());
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			while (true) {
				out.println("/nick");
				name = in.readLine();
				if (name == null) {
					return;
				}
				JLabel label = new JLabel();
				label.setText(name);
				Server.users.put(name, label);
				Server.userTab.add(label);
				label.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent me) {
						Server.messageArea.append("[" + formattedDate + "]" + " Kicking " + name + "...\n");
						try {
							out.println("/kick " + name);
							socket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						Server.userTab.remove(label);
						Server.users.remove(label);
						Server.names.remove(name);
						Server.userTab.revalidate();
						Server.userTab.repaint();
					}
				});
				Server.userTab.revalidate();
				Server.userTab.repaint();
				for (PrintWriter writer : Server.writers) {
					writer.println("/adduser " + name);
				}
				for (String client : Server.names) {
					if (client != name) {
						out.println("/adduser " + client);
					}
				}
				synchronized (Server.names) {
					if (!Server.names.contains(name)) {
						Server.names.add(name);
						break;
					}
				}
				
			}
			out.println("+nickaccepted");
			Server.writers.add(out);
			while (true) {
				String input = in.readLine();
				if (input == null) {
					return;
				} else if (!input.startsWith("/test")) {
					for (PrintWriter writer : Server.writers) {
						writer.println("/broadcast " + name + "> " + input);
					}
					Server.messageArea.append("[" + formattedDate + "] " + name + "> " + input + "\n");
				}
				
			}
		} catch (IOException e) {}
		finally {
			if (name != null) {
				Server.names.remove(name);
				JLabel toBeRemoved = Server.users.get(name);
				Server.userTab.remove(toBeRemoved);
				Server.userTab.revalidate();
				Server.userTab.repaint();
				Server.users.remove(name);
				for (PrintWriter writer : Server.writers) {
					writer.println("/removeuser " + name);
				}
			}
			if (out != null) {
				Server.writers.remove(out);
			}
			try {
				Server.serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
