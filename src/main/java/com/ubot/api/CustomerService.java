package com.ubot.api;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ubot.db.dao.VSPCustomerDao;
import com.ubot.db.vo.VSPCustomer;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CustomerService
 */
public class CustomerService extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final VSPCustomerDao vspCustomerDao;
	private final ObjectMapper mapper = new ObjectMapper();

	/**
	 * Default constructor.
	 */
	public CustomerService() {
		this.vspCustomerDao = new VSPCustomerDao();
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
		try {
			String json = request.getReader().lines().collect(Collectors.joining());
			VSPCustomer vspCustomer = mapper.readValue(json, VSPCustomer.class);
			vspCustomerDao.updateQuery(vspCustomer);
			result.put("code", 0);
			result.put("message", "更新成功");
		} catch (Exception e) {
			result.put("code", 1);
			result.put("message", "更新失敗");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print(mapper.writeValueAsString(result));
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ObjectNode result = mapper.createObjectNode();
		String pathInfo = request.getPathInfo();

		if (pathInfo.equalsIgnoreCase("/token")) {
			try {
				result.put("data", vspCustomerDao.findById(request.getParameter("customerPhone")).getToken());
				result.put("code", 0);
				result.put("message", "查詢成功");
			} catch (Exception e) {
				e.printStackTrace();
				result.put("code", 1);
				result.put("message", "查詢失敗");
			}
		}
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print(mapper.writeValueAsString(result));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ObjectNode result = mapper.createObjectNode();

		String json = request.getReader().lines().collect(Collectors.joining());
		VSPCustomer vspCustomer = mapper.readValue(json, VSPCustomer.class);
		try {
			try {
				vspCustomerDao.insertQuery(vspCustomer);
			} catch (SQLIntegrityConstraintViolationException sqlcve) {
				vspCustomerDao.updateQuery(vspCustomer);
			}
			result.put("code", 0);
			result.put("message", "新增成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			result.put("code", 1);
			result.put("message", "新增失敗");
			e.printStackTrace();
		}
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print(mapper.writeValueAsString(result));
	}

}
