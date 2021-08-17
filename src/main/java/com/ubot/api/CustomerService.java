package com.ubot.api;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ubot.db.dao.CustomerDao;
import com.ubot.db.vo.Customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomerService extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final CustomerDao customerDao;
	private final ObjectMapper mapper;
	private final Logger logger;

	public CustomerService() {
		this.customerDao = new CustomerDao();
		this.mapper = new ObjectMapper();
		this.logger = LogManager.getLogger(this.getClass());
	}

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (request.getMethod().equalsIgnoreCase("PATCH")) {
			doPatch(request, response);
		} else {
			super.service(request, response);
		}
	}

	protected void doPatch(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ObjectNode result = mapper.createObjectNode();
		String message = "";
		String json = request.getReader().lines().collect(Collectors.joining());
		Customer customer = mapper.readValue(json, Customer.class);
		logger.info(json);
		try {
			customerDao.updateQuery(customer);
			message = String.format("%s 客戶更新成功", customer.getCustomerPhone());
			logger.info(message);
			result.put("code", 0);
			result.put("message", message);
		} catch (Exception e) {
			message = String.format("%s 客戶更新失敗, 原因: %s", customer.getCustomerPhone(), e.getMessage());
			logger.error(message);
			result.put("code", 1);
			result.put("message", message);
			e.printStackTrace();
		}
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print(mapper.writeValueAsString(result));
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ObjectNode result = mapper.createObjectNode();
		String pathInfo = request.getPathInfo();
		String message = "";
		if (pathInfo.equalsIgnoreCase("/token")) {
			try {
				Customer customer = customerDao.findById(request.getParameter("customerPhone"))
						.orElseThrow(() -> new Exception("此ID尚未註冊"));

				message = "查詢客戶token成功";
				logger.info(message);
				result.put("data", customer.getToken());
				result.put("code", 0);
				result.put("message", message);
			} catch (Exception e) {
				e.printStackTrace();
				message = String.format("查詢客戶token失敗, 原因: %s", e.getMessage());
				logger.info(message);
				result.put("code", 1);
				result.put("message", message);
			}
		}
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print(mapper.writeValueAsString(result));
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ObjectNode result = mapper.createObjectNode();
		String json = request.getReader().lines().collect(Collectors.joining());
		logger.info(json);
		Customer customer = mapper.readValue(json, Customer.class);
		String message = "";
		try {
			try {
				customerDao.insertQuery(customer);
			} catch (SQLIntegrityConstraintViolationException sqlcve) {
				customerDao.updateQuery(customer);
			}
			message = String.format("%s 客戶註冊成功", customer.getCustomerPhone());

			logger.info(message);
			result.put("code", 0);
			result.put("message", message);
		} catch (Exception e) {
			message = String.format("%s 客戶註冊錯誤, 原因: %s", customer.getCustomerPhone(), e.getMessage());
			logger.error(message);
			result.put("code", 1);
			result.put("message", message);
			e.printStackTrace();
		}
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print(mapper.writeValueAsString(result));
	}

}
