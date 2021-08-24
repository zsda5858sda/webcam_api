package com.ubot.api;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ubot.db.dao.VSPFileDao;
import com.ubot.db.vo.VSPFile;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/File")
public class FileService {
	private final VSPFileDao vspFileDao;
	private final ObjectMapper mapper;
	private final Logger logger;

	public FileService() {
		this.vspFileDao = new VSPFileDao();
		this.mapper = new ObjectMapper();
		this.logger = LogManager.getLogger(this.getClass());
	}

	@PATCH
	@Produces(MediaType.APPLICATION_JSON + " ;charset=UTF-8")
	@Consumes(MediaType.APPLICATION_JSON + " ;charset=UTF-8")
	public Response update(String requestJson) throws IOException {
		ObjectNode result = mapper.createObjectNode();
		String message = "";
		VSPFile vspFile = mapper.readValue(requestJson, VSPFile.class);
		logger.info(requestJson);
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
		}
		return Response.status(200).entity(mapper.writeValueAsString(result)).build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON + " ;charset=UTF-8")
	@Consumes(MediaType.APPLICATION_JSON + " ;charset=UTF-8")
	public Response save(String requestJson) throws IOException {
		ObjectNode result = mapper.createObjectNode();
		String message = "";
		VSPFile vspFile = mapper.readValue(requestJson, VSPFile.class);
		logger.info(requestJson);
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
		}
		return Response.status(200).entity(mapper.writeValueAsString(result)).build();
	}

}
