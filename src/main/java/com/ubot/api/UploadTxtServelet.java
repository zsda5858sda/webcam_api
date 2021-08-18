package com.ubot.api;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "FileTxtUploadServlet", urlPatterns = { "/uploadTxt" })
@MultipartConfig(
  fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
  maxFileSize = 1024 * 1024 * 10,      // 10 MB
  maxRequestSize = 1024 * 1024 * 100   // 100 MB
)
public class UploadTxtServelet extends HttpServlet {

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    /* Receive file uploaded to the Servlet from the HTML5 form */
	String fileName = request.getParameter("fileName");
    request.setAttribute("fileName", fileName);
    String content = request.getParameter("content");
    request.setAttribute("content", content);   // 設定請求屬性
    String folderName = "/";
	for (int i = 0; i < 18; i++) {
		folderName += fileName.charAt(i);
	}
	Path folderPath = Paths.get("/Users/yangzhelun/Desktop/uploadFile/" + folderName);
	try {
		Files.createDirectory(folderPath);
	} catch (IOException e) {
		System.err.println("Folder already exists!! : " + e.getMessage());
	}
	File myObj = new File(folderPath + "/" + fileName);
	if (myObj.createNewFile()) {
		System.out.println("File created: " + myObj.getName());
	} else {
		System.out.println("File already exists.");
		myObj.delete();
		myObj.createNewFile();
		System.out.println("File has been deleted and create again");
	}
	BufferedWriter writer = new BufferedWriter(new FileWriter(myObj));
	writer.write(content);
	writer.close();
  }

}