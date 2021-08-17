package com.ubot.api;

import java.io.IOException;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ubot.db.dao.VSPFileDao;
import com.ubot.db.vo.VSPFile;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FileService extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final VSPFileDao vspFileDao;
	private final ObjectMapper mapper;
	private final Logger logger;

	public FileService() {
		this.vspFileDao = new VSPFileDao();
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
		VSPFile vspFile = mapper.readValue(json, VSPFile.class);
		logger.info(json);
		try {
			vspFileDao.updateQuery(vspFile);
			message = String.format("檔案更新成功");
			logger.info(message);
			result.put("code", 0);
			result.put("message", message);
		} catch (Exception e) {
			message = String.format("檔案更新失敗, 原因: %s", e.getMessage());
			logger.error(message);
			result.put("code", 1);
			result.put("message", message);
			e.printStackTrace();
		}
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print(mapper.writeValueAsString(result));
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ObjectNode result = mapper.createObjectNode();
		String json = request.getReader().lines().collect(Collectors.joining());
		String message = "";
		logger.info(json);
		VSPFile vspFile = mapper.readValue(json, VSPFile.class);
		try {
			message = "檔案新增成功";
			vspFileDao.insertQuery(vspFile);
			logger.info(message);
			result.put("code", 0);
			result.put("message", message);
		} catch (Exception e) {
			message = "檔案新增失敗";
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
