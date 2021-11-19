package com.ubot.db.vo;

//僅打AD驗證時轉換用VO，與DB無實際關聯
public class EaiVO {
	private String loginId;
	private String loginP_ss;
	private String fileName;
	private String content;

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getLoginP_ss() {
		return loginP_ss;
	}

	public void setLoginP_ss(String loginP_ss) {
		this.loginP_ss = loginP_ss;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
