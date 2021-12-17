package com.ubot.db.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ubot.db.vo.VSPBranch;

// 有關對vspbranch表的CRUD
public class VSPBranchDao extends BaseDao {
	private final Logger logger = LogManager.getLogger(this.getClass());

	public List<VSPBranch> selectQuery(String sql) throws Exception {
		List<VSPBranch> result = new ArrayList<VSPBranch>();
		Connection conn = getConnection();
		try (Statement stat = conn.createStatement(); ResultSet resultSet = stat.executeQuery(sql);) {

			logger.info(sql);

			StringBuilder builder = new StringBuilder();

			while (resultSet.next()) {
				VSPBranch branch = new VSPBranch();

				String branchCode = resultSet.getString("BRANCHCODE");
				String branchName = resultSet.getString("BRANCHNAME");

				builder.append("\n");
				builder.append(branchCode);
				builder.append("  |  ");
				builder.append(branchName);

				branch.setBranchCode(branchCode);
				branch.setBranchName(branchName);
				result.add(branch);
			}

			logger.info(builder.toString());
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		} finally {
			conn.close();
		}
		return result;
	}
}
