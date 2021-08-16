package com.ubot.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ubot.db.dao.VSPFileDao;
import com.ubot.db.vo.VSPFile;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class FileService
 */
public class FileService extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final VSPFileDao vspFileDao;

	/**
	 * Default constructor.
	 */
	public FileService() {
		this.vspFileDao = new VSPFileDao();
		// TODO Auto-generated constructor stub
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
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode result = mapper.createObjectNode();
		try {
			String json = request.getReader().lines().collect(Collectors.joining());
			VSPFile vspFile = mapper.readValue(json, VSPFile.class);
			vspFileDao.updateQuery(vspFile);
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
		
		List<VSPFile> vspFileList = new ArrayList<VSPFile>();
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode result = mapper.createObjectNode();
		try {
			vspFileList = vspFileDao.selectQuery("select * from vspfile;");
			result.put("code", 0);
			result.put("message", "查詢成功");
			result.putPOJO("data", vspFileList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode result = mapper.createObjectNode();
		try {
			String json = request.getReader().lines().collect(Collectors.joining());
			VSPFile vspFile = mapper.readValue(json, VSPFile.class);
			vspFileDao.insertQuery(vspFile);
			result.put("code", 0);
			result.put("message", "新增成功");
		} catch (Exception e) {
			result.put("code", 1);
			result.put("message", "新增失敗");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print(mapper.writeValueAsString(result));
	}

}
