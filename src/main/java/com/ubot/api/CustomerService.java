package com.ubot.api;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ubot.db.dao.CustomerDao;
import com.ubot.db.vo.Customer;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/Customer")
public class CustomerService {
	private final CustomerDao customerDao;
	private final ObjectMapper mapper;
	private final Logger logger;

	public CustomerService() {
		this.customerDao = new CustomerDao();
		this.mapper = new ObjectMapper();
		this.logger = LogManager.getLogger(this.getClass());
	}

	@PATCH
	@Produces(MediaType.APPLICATION_JSON + " ;charset=UTF-8")
	@Consumes(MediaType.APPLICATION_JSON + " ;charset=UTF-8")
	public Response update(String requestJson) throws IOException {
		ObjectNode result = mapper.createObjectNode();
		String message = "";

		Customer customer = mapper.readValue(requestJson, Customer.class);
		logger.info(requestJson);
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
		}
		return Response.status(200).entity(mapper.writeValueAsString(result)).build();
	}

	@GET
	@Path("/token")
	@Produces(MediaType.APPLICATION_JSON + " ;charset=UTF-8")
	public Response getById(@QueryParam("customerPhone") String customerPhone) throws IOException {
		ObjectNode result = mapper.createObjectNode();
		String message = "";

		try {
			Customer customer = customerDao.findById(customerPhone).orElseThrow(() -> new Exception("此ID尚未註冊"));

			message = "查詢客戶token成功";
			logger.info(message);
			result.put("data", customer.getToken());
			result.put("code", 0);
			result.put("message", message);
		} catch (Exception e) {
			message = String.format("查詢客戶token失敗, 原因: %s", e.getMessage());
			logger.info(message);
			result.put("code", 1);
			result.put("message", message);
		}
		return Response.status(200).entity(mapper.writeValueAsString(result)).build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON + " ;charset=UTF-8")
	@Consumes(MediaType.APPLICATION_JSON + " ;charset=UTF-8")
	public Response save(String requestJson) throws IOException {
		ObjectNode result = mapper.createObjectNode();

		Customer customer = mapper.readValue(requestJson, Customer.class);
		logger.info(requestJson);
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
		}
		return Response.status(200).entity(mapper.writeValueAsString(result)).build();
	}
}
