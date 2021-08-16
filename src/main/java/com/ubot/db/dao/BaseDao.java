package com.ubot.db.dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class BaseDao {
	private static String driverName = "com.mysql.cj.jdbc.Driver";
//	private static String dbURL = "jdbc:mysql://192.168.198.130:3306/ubot";
//	private static String userName = "centos";
//	private static String password = "password";
	//server db
	private static String dbURL = "jdbc:mysql://172.16.45.245:3306/vsp";
	private static String userName = "apuser";
	private static String password = "apuser";

	public Connection getConnection() {
		Connection conn = null;
		try {
			Class.forName(driverName);
			conn = DriverManager.getConnection(dbURL, userName, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
}
