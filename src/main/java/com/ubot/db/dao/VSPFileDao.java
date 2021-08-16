package com.ubot.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ubot.db.vo.VSPFile;

public class VSPFileDao extends BaseDao {

	public List<VSPFile> selectQuery(String sql) throws Exception {
		List<VSPFile> result = new ArrayList<VSPFile>();
		Connection conn = getConnection();
		Statement stat = conn.createStatement();
		ResultSet resultSet = stat.executeQuery(sql);

		while (resultSet.next()) {
			VSPFile vspFile = new VSPFile();
			String fileName = resultSet.getString("FILENAME");
			String filePath = resultSet.getString("FILEPATH");
			String workType = resultSet.getString("WORKTYPE");

			vspFile.setFileName(fileName);
			vspFile.setFilePath(filePath);
			vspFile.setWorkType(workType);

			System.out.println("================================");
			System.out.println(fileName);
			System.out.println(filePath);
			System.out.println(workType);
			System.out.println("================================");

			result.add(vspFile);
		}
		stat.close();
		resultSet.close();
		conn.close();
		return result;
	}

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
