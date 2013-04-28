package com.piotrek.testy.server;

import java.io.IOException;

import com.piotrek.server.server.Server;


/*
 *  Opis: Klasa testowa, która uruchamia serwer.
 */
public class DeployServer {

	public static void main(String[] args) {

		try {
			Server server = new Server(8389, 20);
			server.runServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
