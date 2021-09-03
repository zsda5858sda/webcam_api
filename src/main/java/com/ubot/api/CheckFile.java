package com.ubot.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CheckFile extends Thread {
	public CheckFile() {
	}

	@Override
	public void run() {
		try {
			while (true) {
				String dirName = "/home/petersha/uploadFile";
				String zipName = "/home/petersha/zipFile/";
				List<String> filePaths = new ArrayList<String>();
				try (Stream<Path> stream = Files.list(new File(dirName).toPath())) {
					stream.map(p -> p.toAbsolutePath().toString()).sequential().forEach(filePaths::add);
				}
				if (filePaths.size() != 0) {
					System.out.println("現在待處理的資料夾數量為："+filePaths.size());
					for (int i1 = 0; i1 < filePaths.size(); i1++) { // 取出arrarList裡的檔案並逐一檢查是否到達上傳標準
						String webcamFolderName = filePaths.get(i1);
						File isF = new File(webcamFolderName);
						if (isF.isDirectory()) {
							List<String> fileCount = new ArrayList<String>();
							try (Stream<Path> stream = Files.list(new File(webcamFolderName).toPath())) {
								stream.map(p -> p.toAbsolutePath().toString()).sequential().forEach(fileCount::add);
							}
							if (fileCount.size() >= 8) {
								Boolean canUpload = false;
								int count = 0;
								for (int i = 0; i < fileCount.size(); i++) {
									File f = new File(fileCount.toArray()[i].toString());
									long fileSize = f.length();
									System.out.println(fileSize);
									if (fileSize == 0) {
										canUpload = false;
										System.out.println("發現" + fileCount.get(i) + "的大小為0");
									}
									if(fileCount.get(i).contains("customerFileCount") || fileCount.get(i).contains("agentFileCount")) {
										count++;
									}
								}
								if(count >= 2) {
									canUpload = true;
//									System.out.println("檔案數量符合規定");
								}
								if (canUpload) {
									System.out.println(webcamFolderName + "可以上傳摟");
									String newFileName = webcamFolderName.split("uploadFile/")[1];
									Path sourceFolderPath = Paths.get(webcamFolderName);
									Path zipPath = Paths.get(zipName + newFileName + ".zip");
									ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()));
									Files.walkFileTree(sourceFolderPath, new SimpleFileVisitor<Path>() { // 將要上傳的資料夾壓縮
										public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
												throws IOException {
											zos.putNextEntry(
													new ZipEntry(sourceFolderPath.relativize(file).toString()));
											Files.copy(file, zos);
											zos.closeEntry();
											return FileVisitResult.CONTINUE;
										}
									});
									zos.close();
									File uploadFile = new File(zipName + newFileName + ".zip");
									FileInputStream input = new FileInputStream(uploadFile);
									byte[] content = null;
									try {
										content = Files.readAllBytes(zipPath);
									} catch (final IOException e) {
									}
									ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
									Future future = executor.submit(new ClientThreadCode(uploadFile));
									executor.schedule(new Runnable() {
										public void run() {
											future.cancel(true);
											System.out.println("上傳終止");
										}
									}, 30000, TimeUnit.MILLISECONDS); // 設置上傳時數限制，若超出則強致中斷
									executor.shutdown();
									CheckFile checkFile = new CheckFile();
									checkFile.join();
									File folderToBeDelete = new File(webcamFolderName);
									File zipToBeDelete = new File("/home/petersha/zipFile");// file to be
																										// delete
									String[] entries = folderToBeDelete.list();
									for (String s : entries) {
										File currentFile = new File(folderToBeDelete.getPath(), s);
										currentFile.delete(); // 開始刪除檔案
									}
									try {
										if (folderToBeDelete.delete() && zipToBeDelete.delete()) // returns Boolean
																									// value
										{
											System.out.println(folderToBeDelete.getName() + " deleted"); // getting and
																											// printing
											System.out.println(zipToBeDelete.getName() + " deleted"); // getting and
																										// printing
										} else {
											System.out.println("failed");
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else {
									System.out.println("有檔案為空擋，不能上傳");
								}
							} else {
								System.out.println("檔案數量尚未到齊");
								System.out.println(fileCount.size());
								File myfile = new File(webcamFolderName);
								Path path = myfile.toPath();
								BasicFileAttributes fatr = Files.readAttributes(path, BasicFileAttributes.class);
								Clock clock = Clock.systemUTC();
								Instant instant = clock.instant();
								long duration = ChronoUnit.MINUTES.between(fatr.lastModifiedTime().toInstant(),
										instant);
								if (duration > 720) {
									System.out.println("時間上線已到，即將進行檔案刪除");
									String[] entries = myfile.list();
									for (String s : entries) {
										File currentFile = new File(myfile.getPath(), s);
										currentFile.delete();
									}
									try {
										if (myfile.delete()) // returns Boolean value
										{
											System.out.println(myfile.getName() + " deleted"); // getting and printing
																								// the file
										} else {
											System.out.println("failed");
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else {
									System.out.println("暫時不做處理");
								}
							}
							fileCount.clear();
						} else {
							System.out.println(isF.getName()+"不是資料夾");
						}

					}
				} else {
					System.out.println(dirName + "目前為空的");
				}
				Thread.sleep(3000);
			}
		} catch (Exception e) {
			System.out.println("執行緒中斷");
			System.out.println(e);
		}
	}
}