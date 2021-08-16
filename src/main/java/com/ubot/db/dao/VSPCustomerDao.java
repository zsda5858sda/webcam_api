package com.ubot.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ubot.db.vo.VSPCustomer;

public class VSPCustomerDao extends BaseDao {

	public List<VSPCustomer> selectQuery(String sql) throws Exception {
		List<VSPCustomer> result = new ArrayList<VSPCustomer>();
		Connection conn = getConnection();
		Statement stat = conn.createStatement();
		ResultSet resultSet = stat.executeQuery(sql);

		while (resultSet.next()) {
			VSPCustomer vspCustomer = new VSPCustomer();

			String customerId = resultSet.getString("CUSTOMERID");
			String customerPhone = resultSet.getString("CUSTOMERPHONE");
			String token = resultSet.getString("TOKEN");

			vspCustomer.setCustomerId(customerId);
			vspCustomer.setCustomerPhone(customerPhone);
			vspCustomer.setToken(token);

			System.out.println("================================");
			System.out.println(customerId);
			System.out.println(customerPhone);
			System.out.println(token);
			System.out.println("================================");

			result.add(vspCustomer);
		}
		stat.close();
		resultSet.close();
		conn.close();
		return result;
	}

	public void insertQuery(VSPCustomer vspCustomer) throws Exception {
		Connection conn = getConnection();
		String sql = "insert into vspcustomer(CUSTOMERID, CUSTOMERPHONE, TOKEN) values(?,?,?)";
		PreparedStatement ps = conn.prepareStatement(sql);

		ps.setString(1, vspCustomer.getCustomerId());
		ps.setString(2, vspCustomer.getCustomerPhone());
		ps.setString(3, vspCustomer.getToken());

		ps.execute();
		ps.close();
		conn.close();
	}

	public void updateQuery(VSPCustomer vspCustomer) throws Exception {
		Connection conn = getConnection();
		VSPCustomer entity = findById(vspCustomer.getCustomerPhone());
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

		ps.execute();
		ps.close();
		conn.close();
	}
	
	public VSPCustomer findById(String id) throws Exception {
		Connection conn = getConnection();
		VSPCustomer vspCustomer = new VSPCustomer();
		Statement stat = conn.createStatement();
		ResultSet resultSet = stat.executeQuery(String.format("select * from vspcustomer where CUSTOMERPHONE = %s", id));

		while (resultSet.next()) {

			String customerId = resultSet.getString("CUSTOMERID");
			String customerPhone = resultSet.getString("CUSTOMERPHONE");
			String token = resultSet.getString("TOKEN");

			vspCustomer.setCustomerId(customerId);
			vspCustomer.setCustomerPhone(customerPhone);
			vspCustomer.setToken(token);

			System.out.println("================================");
			System.out.println(customerId);
			System.out.println(customerPhone);
			System.out.println(token);
			System.out.println("================================");

		}
		stat.close();
		resultSet.close();
		conn.close();
		return vspCustomer;
	}
}
