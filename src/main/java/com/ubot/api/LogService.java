package com.ubot.api;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ubot.db.dao.LogDao;
import com.ubot.db.vo.Log;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/Log")
public class LogService {
	private final Logger logger;
	private final ObjectMapper mapper;
	private final LogDao logDao;
	
    public LogService() {
		this.logger = LogManager.getLogger(this.getClass());
		this.mapper = new ObjectMapper();
		this.logDao = new LogDao();
    }

    @POST
	@Produces(MediaType.APPLICATION_JSON + " ;charset=UTF-8")
	@Consumes(MediaType.APPLICATION_JSON + " ;charset=UTF-8")
	public Response save(String requestJson) throws IOException {
		ObjectNode result = mapper.createObjectNode();
		Log log = mapper.readValue(requestJson, Log.class);
		String message = "";
		try {
			log.setCreateDatetime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSS")));
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
		return Response.status(200).entity(mapper.writeValueAsString(result)).build();
	}

}
