package com.ubot.api;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ubot.db.vo.TextFile;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/uploadTxt")
public class UploadTxtServelet {
	private final ObjectMapper mapper;
	private final Logger logger;

	public UploadTxtServelet() {
		this.logger = LogManager.getLogger(this.getClass());
		this.mapper = new ObjectMapper();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON + " ;charset=UTF-8")
	@Consumes(MediaType.APPLICATION_JSON + " ;charset=UTF-8")
	public Response save(String requestJson) throws IOException {
		ObjectNode result = mapper.createObjectNode();
		String message = "";
		try {
			TextFile textFile = mapper.readValue(requestJson , TextFile.class);
			String fileName = textFile.getFileName();
			String content = textFile.getContent();
			String folderName = fileName.split("-")[0] + "-" + fileName.split("-")[1] + "-" + fileName.split("-")[2];
			java.nio.file.Path folderPath = Paths.get("/home/petersha/uploadFile/" + folderName);
			File file = new File("/home/peterhsa/uploadFile");
			boolean exists = file.exists();
			if (exists == true) {
				file.setExecutable(true);
				file.setReadable(true); // 獲取資料夾權限
				file.setWritable(true);

				System.out.println("File permissions changed.");
				System.out.println("Executable: " + file.canExecute());
				System.out.println("Readable: " + file.canRead());
				System.out.println("Writable: " + file.canWrite());
			}
			
			try {
				Files.createDirectory(folderPath); // 創建對保資料夾
			} catch (IOException e) {
				System.out.println(e);
			}
			
			String name = "/home/petersha/uploadFile/" + folderName;
			Files.list(new File(name).toPath()).forEach(path -> {
				if (path.toString().endsWith("webm")) {
					File deleteFile = new File(path.toString());
					deleteFile.delete();
				}
			});
			
			File myObj = new File(folderPath + "/" + fileName);

			if (myObj.createNewFile()) {
				System.out.println("File created: " + myObj.getName());
			} else {
				System.out.println("File already exists.");
				myObj.delete();
				myObj.createNewFile();
				System.out.println("File has been deleted and create again");
			}

			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(myObj));
				writer.write(content); // 寫入檔案
				writer.close();
				String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SS"));
				message = myObj.getAbsolutePath().toString() + "新增於" + time;
				logger.info(message);
				result.put("message", message);
				result.put("code", 0);
			} catch (IOException io) {
				message = String.format("新增檔案失敗原因： %s", io.getMessage());
				logger.error(message);
				result.put("message", message);
				result.put("code", 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			message = String.format("新增log失敗, 原因: %s", e.getMessage());
			result.put("message", message);
			result.put("code", 1);
		}
		return Response.status(200).entity(mapper.writeValueAsString(result)).build();
	}

}
