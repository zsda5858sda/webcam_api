package com.ubot.api;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "FileUploadServlet", urlPatterns = { "/fileuploadservlet" })
@MultipartConfig(
  fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
  maxFileSize = 1024 * 1024 * 10,      // 10 MB
  maxRequestSize = 1024 * 1024 * 100   // 100 MB
)
public class UploadServlet extends HttpServlet {

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    /* Receive file uploaded to the Servlet from the HTML5 form */
    Part filePart = request.getPart("file");
    String fileName = filePart.getSubmittedFileName();
    String folderName = fileName.split("-")[0]+"-"+fileName.split("-")[1];
	Path folderPath = Paths.get("/home/petersha/uploadFile/" + folderName);
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
	try (InputStream fin = filePart.getInputStream();
			FileOutputStream fout = new FileOutputStream(myObj);) {

		byte[] buffer = new byte[8192];

		while (fin.available() != 0) {
			fout.write(buffer, 0, fin.read(buffer));
		}

	} catch (Exception e) {
		System.out.println("Fail to create video ! Reason: " + e.getMessage());
	}
    response.getWriter().print("The file uploaded sucessfully.");
  }

}