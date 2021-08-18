package com.ubot.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.ubot.db.vo.Log;

public class LogDao extends BaseDao {
	
	public void insertQuery(Log log) throws SQLException {
		Connection conn = getConnection();
		String sql = "insert into log(USERID, USERTYPE, CREATEDATETIME, ACTION, IP) values(?,?,?,?,?)";
		PreparedStatement ps = conn.prepareStatement(sql);
		
		ps.setString(1, log.getUserId());
		ps.setString(2, log.getUserType());
		ps.setString(3, log.getCreateDatetime());
		ps.setString(4, log.getAction());
		ps.setString(5, log.getIp());

		ps.execute();
		ps.close();
		conn.close();
	}
}