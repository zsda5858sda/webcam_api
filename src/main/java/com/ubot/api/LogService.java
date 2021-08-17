package com.ubot.api;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ubot.db.dao.LogDao;
import com.ubot.db.vo.Log;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LogService extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final Logger logger;
	private final ObjectMapper mapper;
	private final LogDao logDao;
	
    public LogService() {
		this.logger = LogManager.getLogger(this.getClass());
		this.mapper = new ObjectMapper();
		this.logDao = new LogDao();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ObjectNode result = mapper.createObjectNode();
		String json = request.getReader().lines().collect(Collectors.joining());
		Log log = mapper.readValue(json, Log.class);
		String message = "";
		try {
			log.setCreateDatetime(LocalDateTime.now());
			logDao.insertQuery(log);
			message = "新增log成功";
			logger.info(message);
			result.put("message", message);
			result.put("code", 0);
		} catch (SQLException e) {
			e.printStackTrace();
			message = String.format("新增log失敗, 原因: %s", e.getMessage());
			logger.error(message);
			result.put("message", message);
			result.put("code", 1);
		}
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print(mapper.writeValueAsString(result));
	}

}
