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

import com.ubot.db.vo.Customer;

//有關對customer表的CRUD
public class CustomerDao extends BaseDao {
	private final Logger logger = LogManager.getLogger(this.getClass());

	public List<Customer> selectQuery(String sql) throws Exception {
		List<Customer> result = new ArrayList<Customer>();
		Connection conn = getConnection();
		Statement stat = conn.createStatement();
		ResultSet resultSet = stat.executeQuery(sql);
		StringBuilder builder = new StringBuilder();

		while (resultSet.next()) {
			Customer customer = new Customer();

			String customerId = resultSet.getString("CUSTOMERID");
			String customerPhone = resultSet.getString("CUSTOMERPHONE");
			String token = resultSet.getString("TOKEN");

			customer.setCustomerId(customerId);
			customer.setCustomerPhone(customerPhone);
			customer.setToken(token);

			builder.append("\n");
			builder.append(customerPhone);
			builder.append("  |  ");
			builder.append(customerId);
			builder.append("  |  ");
			builder.append(token);

			result.add(customer);
		}

		logger.info(builder.toString());
		stat.close();
		resultSet.close();
		conn.close();
		return result;
	}

	public void insertQuery(Customer customer) throws Exception {
		Connection conn = getConnection();
		String sql = "insert into customer(CUSTOMERID, CUSTOMERPHONE, TOKEN) values(?,?,?)";
		PreparedStatement ps = conn.prepareStatement(sql);

		ps.setString(1, customer.getCustomerId());
		ps.setString(2, customer.getCustomerPhone());
		ps.setString(3, customer.getToken());

		logger.info(ps.toString());
		ps.execute();
		ps.close();
		conn.close();
	}

	public void updateQuery(Customer customer) throws Exception {
		Connection conn = getConnection();
		Customer entity = findById(customer.getCustomerPhone()).orElseThrow(() -> new Exception("此ID尚未註冊"));
		if (customer.getToken() != null) {
			entity.setToken(customer.getToken());
		}
		if (customer.getCustomerId() != null) {
			entity.setCustomerId(customer.getCustomerId());
		}
		String sql = "update customer set CUSTOMERID = ?, TOKEN = ? where CUSTOMERPHONE = ?";
		PreparedStatement ps = conn.prepareStatement(sql);

		ps.setString(1, entity.getCustomerId());
		ps.setString(2, entity.getToken());
		ps.setString(3, entity.getCustomerPhone());

		logger.info(ps.toString());
		ps.execute();
		ps.close();
		conn.close();
	}

	public Optional<Customer> findById(String id) throws Exception {
		Connection conn = getConnection();
		Customer customer = new Customer();
		String sql = "select * from customer where CUSTOMERPHONE = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, id);

		logger.info(ps.toString());
		ResultSet resultSet = ps.executeQuery();
		StringBuilder builder = new StringBuilder();

		while (resultSet.next()) {
			String customerId = resultSet.getString("CUSTOMERID");
			String customerPhone = resultSet.getString("CUSTOMERPHONE");
			String token = resultSet.getString("TOKEN");

			customer.setCustomerId(customerId);
			customer.setCustomerPhone(customerPhone);
			customer.setToken(token);

			builder.append("\n");
			builder.append(customerPhone);
			builder.append("  |  ");
			builder.append(customerId);
			builder.append("  |  ");
			builder.append(token);
		}

		logger.info(builder.toString());
		ps.close();
		resultSet.close();
		conn.close();
		return customer.getCustomerPhone() == null ? Optional.empty() : Optional.of(customer);
	}
}
