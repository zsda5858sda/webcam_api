package com.ubot.api;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.net.Socket;
import java.text.DecimalFormat;

class SocketServer extends Thread {
	Socket s;
	private static DecimalFormat df = null;
	static {
		// 設定數字格式，保留一位有效小數
		df = new DecimalFormat("#0.0");
		df.setRoundingMode(RoundingMode.HALF_UP);
		df.setMinimumFractionDigits(1);
		df.setMaximumFractionDigits(1);
	}

	SocketServer(Socket s) {
		this.s = s;
	}

	@Override
	public void run() {
		int current = 0;
		DataInputStream dis;
		FileOutputStream fos;
		
		FileInputStream fis;
		DataOutputStream dos;
		try {
			dis = new DataInputStream(s.getInputStream());
			String fileName = dis.readUTF();
			long fileLength = dis.readLong();
			if (fileName.endsWith(".zip")) {
				File client = new File("/Users/yangzhelun/Desktop/development/spring-boot-demo/" + fileName);
				fos = new FileOutputStream(client);
				byte[] bytes = new byte[(int) fileLength];
				int length = 0;
				while ((length = dis.read(bytes, 0, bytes.length)) != -1) {
					fos.write(bytes, 0, length);
					System.out.println(length);
					fos.flush();
				}
				System.out.println("======== 檔案接收成功 [File Name：" + fileName + "] [Size：" + getFormatFileSize(fileLength)
						+ "] ========");
				fis = new FileInputStream(client);
				dos = new DataOutputStream(s.getOutputStream());
				dos.writeUTF(client.getName());
				dos.flush();
				dos.writeLong(client.length());
				dos.flush();
				System.out.println("======== 開始傳輸檔案 ========");
				byte[] dbFileBytes = new byte[(int) client.length()];
				int dbFileLength = 0;
				long progress = 0;
				while ((length = fis.read(dbFileBytes, 0, dbFileBytes.length)) != -1) {
					dos.write(dbFileBytes, 0, dbFileLength);
					dos.flush();
					progress = dbFileLength;
					System.out.print("| " + (100 * progress / client.length()) + "% |");
				}
				
				System.out.println();
				System.out.println("======== 檔案傳輸成功 ========");
				
				fis.close();
				dos.close();
				dis.close();
				fos.close();
			}
		} catch (Exception e) {
			System.err.println("發生了以下錯誤 ：" + e.getMessage());
			System.err.println(s.getRemoteSocketAddress() + " 已離線");
		} finally {
			if (s.isConnected())
				try {
					s.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	private String getFormatFileSize(long length) {
		double size = ((double) length) / (1 << 30);
		if (size >= 1) {
			return df.format(size) + "GB";
		}
		size = ((double) length) / (1 << 20);
		if (size >= 1) {
			return df.format(size) + "MB";
		}
		size = ((double) length) / (1 << 10);
		if (size >= 1) {
			return df.format(size) + "KB";
		}
		return length + "B";
	}
}
