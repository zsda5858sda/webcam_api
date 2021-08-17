package com.ubot.api;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class ContextInitListener extends Thread implements ServletContextListener {
	public ContextInitListener() {
		
	}
	@Override
	public void contextInitialized(ServletContextEvent ev) {
		System.out.println("System start");
		CheckFile checkFile = new CheckFile();
		checkFile.start();
		try {
			new ContextInitListener().join();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}

