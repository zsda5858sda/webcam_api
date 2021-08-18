package com.ubot.api;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;

public class ClientThreadCode extends Thread {
	File clientFile;
	private FileInputStream fis;
	private DataOutputStream dos;

	public ClientThreadCode(File file) {
		clientFile = file;
	}

	@Override
	public void run() {
		try {
			System.out.println("upload started");
			Socket s = new Socket("172.16.45.245", 8000);
			if (clientFile.exists()) {
				fis = new FileInputStream(clientFile);
				dos = new DataOutputStream(s.getOutputStream());
				dos.writeUTF(clientFile.getName());
				dos.flush();
				dos.writeLong(clientFile.length());
				dos.flush();
				System.out.println("======== 開始傳輸檔案 ========");
				byte[] bytes = new byte[(int) clientFile.length()];
				int length = 0;
				long progress = 0;
				while ((length = fis.read(bytes, 0, bytes.length)) != -1) {
					dos.write(bytes, 0, length);
					dos.flush();
					progress = length;
					System.out.print("| " + (100 * progress / clientFile.length()) + "% |");
				}
				System.out.println();
				System.out.println("======== 檔案傳輸成功 ========");
				fis.close();
				dos.close();
				s.close();
			} else {
				System.out.println("file not exists");
			}
		} catch (Exception e) {
			System.out.println("Error occur :" + e);
		}
	}

}