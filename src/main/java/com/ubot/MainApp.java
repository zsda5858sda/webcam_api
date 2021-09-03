package com.ubot;

import org.glassfish.jersey.server.ResourceConfig;

import com.ubot.config.XSSFilter;

import jakarta.ws.rs.ApplicationPath;

//主函式
@ApplicationPath("/*")
public class MainApp extends ResourceConfig {

	public MainApp() {
		// 用來掃描該package裡所有@Path並註冊
		packages("com.ubot.api");
		register(XSSFilter.class);
	}

}
