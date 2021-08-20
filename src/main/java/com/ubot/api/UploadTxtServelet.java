package com.ubot.api;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "FileTxtUploadServlet", urlPatterns = { "/uploadTxt" })
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
		maxFileSize = 1024 * 1024 * 10, // 10 MB
		maxRequestSize = 1024 * 1024 * 100 // 100 MB
)
public class UploadTxtServelet extends HttpServlet {

	private final ObjectMapper mapper;
	private final Logger logger;

	public UploadTxtServelet() {
		this.mapper = new ObjectMapper();
		this.logger = LogManager.getLogger(this.getClass());
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		/* Receive file uploaded to the Servlet from the HTML5 form */
		ObjectNode result = mapper.createObjectNode();
		String fileName = request.getParameter("fileName");
		request.setAttribute("fileName", fileName);
		String content = request.getParameter("content");
		request.setAttribute("content", content); // 設定請求屬性
		String test = fileName.split("-")[3];
		System.out.println(test);
		String folderName = fileName.split("-")[0] + "-" + fileName.split("-")[1] + "-"+fileName.split("-")[2];
		Path folderPath = Paths.get("/home/petersha/uploadFile/" + folderName);
		File file = new File("/home/petersha/uploadFile");
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
		String message = "";

		try {
			Files.createDirectory(folderPath); // 創建對保資料夾
		} catch (IOException e) {
			System.out.println(e);
		}

		String name = "/home/petersha/uploadFile/" + folderName;
		List fileCount = new ArrayList();
		Files.list(new File(name).toPath()).forEach(path -> {
			if(path.toString().endsWith("webm")) {
				File deleteFile = new File(path.toString());
				deleteFile.delete();
			}
		});
		
		String finalFileName = "";
		if(fileName.endsWith("webm")) {
			finalFileName = fileName.split("-")[0]+"-"+fileName.split("-")[1]+"-"+fileName.split("-")[3]+"-"+fileName.split("-")[4];
		} else {
			finalFileName = fileName.split("-")[0]+"-"+fileName.split("-")[1]+"-"+fileName.split("-")[3];
		}
		
		File myObj = new File(folderPath + "/" + finalFileName);

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
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print(mapper.writeValueAsString(result));
	}

}