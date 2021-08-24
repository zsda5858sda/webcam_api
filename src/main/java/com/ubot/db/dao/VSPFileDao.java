package com.ubot.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.ubot.db.vo.VSPFile;

public class VSPFileDao extends BaseDao {

	public void insertQuery(VSPFile vspFile) throws Exception {
		Connection conn = getConnection();
		String sql = "insert into vspfile(FILENAME, FILEPATH, WORKTYPE) values(?,?,?)";
		PreparedStatement ps = conn.prepareStatement(sql);

		ps.setString(1, vspFile.getFileName());
		ps.setString(2, vspFile.getFilePath());
		ps.setString(3, vspFile.getWorkType());

		ps.execute();
		ps.close();
		conn.close();
	}

	public void updateQuery(VSPFile vspFile) throws Exception {
		Connection conn = getConnection();
		String sql = "update vspfile set FILEPATH = ?, WORKTYPE = ? where FILENAME = ?";
		PreparedStatement ps = conn.prepareStatement(sql);

		ps.setString(1, vspFile.getFilePath());
		ps.setString(2, vspFile.getWorkType());
		ps.setString(3, vspFile.getFileName());

		ps.execute();
		ps.close();
		conn.close();
	}
}
