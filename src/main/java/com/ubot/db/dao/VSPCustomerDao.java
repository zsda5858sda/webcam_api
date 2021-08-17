package com.ubot.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ubot.db.vo.VSPCustomer;

public class VSPCustomerDao extends BaseDao {
	private final Logger logger = LogManager.getLogger(this.getClass());

	public List<VSPCustomer> selectQuery(String sql) throws Exception {
		List<VSPCustomer> result = new ArrayList<VSPCustomer>();
		Connection conn = getConnection();
		Statement stat = conn.createStatement();
		ResultSet resultSet = stat.executeQuery(sql);
		StringBuilder builder = new StringBuilder();
		
		while (resultSet.next()) {
			VSPCustomer vspCustomer = new VSPCustomer();

			String customerId = resultSet.getString("CUSTOMERID");
			String customerPhone = resultSet.getString("CUSTOMERPHONE");
			String token = resultSet.getString("TOKEN");

			vspCustomer.setCustomerId(customerId);
			vspCustomer.setCustomerPhone(customerPhone);
			vspCustomer.setToken(token);

			builder.append("\n");
			builder.append(customerPhone);
			builder.append("  |  ");
			builder.append(customerId);
			builder.append("  |  ");
			builder.append(token);

			result.add(vspCustomer);
		}
		
		System.out.println(builder.toString());
		stat.close();
		resultSet.close();
		conn.close();
		logger.info("客戶資料查詢");
		return result;
	}

	public void insertQuery(VSPCustomer vspCustomer) throws Exception {
		Connection conn = getConnection();
		String sql = "insert into vspcustomer(CUSTOMERID, CUSTOMERPHONE, TOKEN) values(?,?,?)";
		PreparedStatement ps = conn.prepareStatement(sql);

		ps.setString(1, vspCustomer.getCustomerId());
		ps.setString(2, vspCustomer.getCustomerPhone());
		ps.setString(3, vspCustomer.getToken());

		logger.info(ps.toString());
		ps.execute();
		ps.close();
		conn.close();
	}

	public void updateQuery(VSPCustomer vspCustomer) throws Exception {
		Connection conn = getConnection();
		VSPCustomer entity = findById(vspCustomer.getCustomerPhone()).orElseThrow(() -> new Exception("此ID尚未註冊"));
		if (vspCustomer.getToken() != null) {
			entity.setToken(vspCustomer.getToken());
		}
		if (vspCustomer.getCustomerId() != null) {
			entity.setCustomerId(vspCustomer.getCustomerId());
		}
		String sql = "update vspcustomer set CUSTOMERID = ?, TOKEN = ? where CUSTOMERPHONE = ?";
		PreparedStatement ps = conn.prepareStatement(sql);

		ps.setString(1, entity.getCustomerId());
		ps.setString(2, entity.getToken());
		ps.setString(3, entity.getCustomerPhone());

		logger.info(ps.toString());
		ps.execute();
		ps.close();
		conn.close();
	}

	public Optional<VSPCustomer> findById(String id) throws Exception {
		Connection conn = getConnection();
		VSPCustomer vspCustomer = new VSPCustomer();
		String sql = "select * from vspcustomer where CUSTOMERPHONE = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, id);
		
		ResultSet resultSet = ps.executeQuery();
		StringBuilder builder = new StringBuilder();
		
		while (resultSet.next()) {
			String customerId = resultSet.getString("CUSTOMERID");
			String customerPhone = resultSet.getString("CUSTOMERPHONE");
			String token = resultSet.getString("TOKEN");

			vspCustomer.setCustomerId(customerId);
			vspCustomer.setCustomerPhone(customerPhone);
			vspCustomer.setToken(token);

			builder.append("\n");
			builder.append(customerPhone);
			builder.append("  |  ");
			builder.append(customerId);
			builder.append("  |  ");
			builder.append(token);
		}
		
		System.out.println(builder.toString());
		logger.info(ps.toString());
		ps.close();
		resultSet.close();
		conn.close();
		return vspCustomer.getCustomerPhone() == null ? Optional.empty() : Optional.of(vspCustomer);
	}
}
