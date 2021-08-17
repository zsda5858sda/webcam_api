package com.ubot.api;

import java.net.ServerSocket;
import java.net.Socket;

public class CheckFile extends Thread {
	public CheckFile() {
	}

	@Override
	public void run() {
		try {
			ServerSocket ss = new ServerSocket(8000);
			while (true) {
				System.out.println("Socket open");
				Socket s = ss.accept();
				Thread t = new Thread(new SocketServer(s));
				t.start();
			}
		} catch (Exception e) {
		}
	}
}