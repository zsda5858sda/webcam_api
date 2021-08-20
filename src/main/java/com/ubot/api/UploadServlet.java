package com.ubot.api;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.FileUploadException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "FileUploadServlet", urlPatterns = { "/fileuploadservlet" })
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
		maxFileSize = 1024 * 1024 * 1024, // 1 GB
		maxRequestSize = 1024 * 1024 * 1024 // 1 GB
)
public class UploadServlet extends HttpServlet {
	private final ObjectMapper mapper;
	private final Logger logger;

	public UploadServlet() {
		this.mapper = new ObjectMapper();
		this.logger = LogManager.getLogger(this.getClass());
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		/* Receive file uploaded to the Servlet from the HTML5 form */
		Part filePart = request.getPart("file");
		ObjectNode result = mapper.createObjectNode();
		String fileName = filePart.getSubmittedFileName();
		String folderName = fileName.split("-")[0] + "-" + fileName.split("-")[1] + "-" + fileName.split("-")[2];
		Path folderPath = Paths.get("/home/petersha/uploadFile/" + folderName);
		File file = new File("/home/petersha/uploadFile");
		String message = "";

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
			Files.createDirectory(folderPath);
		} catch (FileAlreadyExistsException e) {
			System.out.println(e);
		}
		String finalFileName = "";
		if (fileName.endsWith("webm") && fileName.split("-").length == 6) {
			finalFileName = fileName.split("-")[0] + "-" + fileName.split("-")[1] + "-" + fileName.split("-")[3] + "-"
					+ fileName.split("-")[4] + "-" + fileName.split("-")[5];
		} else if (fileName.endsWith("webm") && fileName.split("-").length == 5) {
			finalFileName = fileName.split("-")[0] + "-" + fileName.split("-")[1] + "-" + fileName.split("-")[3] + "-"
					+ fileName.split("-")[4];
		} else {
			finalFileName = fileName.split("-")[0] + "-" + fileName.split("-")[1] + "-" + fileName.split("-")[3];
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
		try (InputStream fin = filePart.getInputStream(); FileOutputStream fout = new FileOutputStream(myObj);) {

			byte[] buffer = new byte[8192];

			while (fin.available() != 0) {
				fout.write(buffer, 0, fin.read(buffer));
			}
			fin.close();
			fout.close();
			String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSS"));
			message = myObj.getAbsolutePath().toString() + "新增於" + time;
			logger.info(message);
			result.put("message", message);
			result.put("code", 0);

		} catch (FileUploadException e) {
			message = String.format("新增檔案失敗原因： %s", e.getMessage());
			logger.error(message);
			result.put("message", message);
			result.put("code", 1);
		}
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print(mapper.writeValueAsString(result));
	}

}