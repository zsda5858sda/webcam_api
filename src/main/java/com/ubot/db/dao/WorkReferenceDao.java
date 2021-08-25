package com.ubot.db.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ubot.db.vo.WorkReference;

public class WorkReferenceDao extends BaseDao {
	private final Logger logger = LogManager.getLogger(this.getClass());

	public List<WorkReference> selectQuery(String sql) throws SQLException {
		List<WorkReference> result = new ArrayList<WorkReference>();
		Connection conn = getConnection();
		Statement stat = conn.createStatement();
		logger.info(sql);
		ResultSet resultSet = stat.executeQuery(sql);
		StringBuilder builder = new StringBuilder();

		while (resultSet.next()) {
			WorkReference workReference = new WorkReference();

			String workName = resultSet.getString("WORKNAME");
			String workType = resultSet.getString("WORKTYPE");

			builder.append("\n");
			builder.append(workName);
			builder.append("  |  ");
			builder.append(workType);

			workReference.setWorkName(workName);
			workReference.setWorkType(workType);

			result.add(workReference);
		}

		System.out.println(builder.toString());
		stat.close();
		resultSet.close();
		conn.close();
		return result;
	}
}