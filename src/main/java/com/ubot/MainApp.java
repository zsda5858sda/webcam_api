package com.ubot;

import org.glassfish.jersey.server.ResourceConfig;

import com.ubot.utils.XSSFilter;

import jakarta.ws.rs.ApplicationPath;

@ApplicationPath("/*")
public class MainApp extends ResourceConfig {
	
	public MainApp() {
		packages("com.ubot.api");
		register(XSSFilter.class);
	}

}
