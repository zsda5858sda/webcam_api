package com.ubot.api;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.hc.client5.http.HttpHostConnectException;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HttpService extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final ObjectMapper mapper;
	private final Logger logger;

	public HttpService() {
		this.mapper = new ObjectMapper();
		this.logger = LogManager.getLogger(this.getClass());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("infohelloworld");
		response.getWriter().append("hello world");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ObjectNode result = mapper.createObjectNode();
		String json = request.getReader().lines().collect(Collectors.joining());
		logger.info(json);

		String message = "AD驗證登入成功";
		String errMessage = "";
		StringBuffer errMessageBuffer = new StringBuffer("AD驗證登入失敗, 原因: ");
		try {
			ClassicHttpResponse clientResponse = sendToAdHub(json);
			Thread.sleep(100);
			if (clientResponse.getCode() == 200) {
				try {
					HttpEntity entity = clientResponse.getEntity();
					String responseString = EntityUtils.toString(entity, "UTF-8");
					Map<String, String> jsonResult = mapper.readValue(responseString, Map.class);
					String rc2 = jsonResult.get("rc2");

					if (rc2.equals("M000")) {
						logger.info(message);
						result.put("message", message);
						result.put("code", "0");
					} else {
						errMessage = errMessageBuffer.append(String.format("%s", jsonResult.get("msg2"))).toString();
						logger.error(errMessage);
						result.put("message", errMessage);
						result.put("code", "1");
						result.put("reason", jsonResult.get("msg2"));
					}

				} catch (ParseException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				errMessage = errMessageBuffer.append(String.format("http status code %d", clientResponse.getCode()))
						.toString();

				logger.error(errMessage);
				result.put("code", 1);
				result.put("message", errMessage);

			}
		} catch (HttpHostConnectException | InterruptedException e) {
			errMessage = errMessageBuffer.append(String.format("%s", e.getMessage())).toString();

			logger.error(errMessage);
			result.put("code", 1);
			result.put("message", errMessage);
			e.printStackTrace();
		}
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print(mapper.writeValueAsString(result));
	}

	// 發送至AD之設定
	private ClassicHttpResponse sendToAdHub(String entity) throws IOException {

		StringBuffer strUrl = new StringBuffer("http://172.16.45.135:8080/EaiHub/resCommon/getAd01");
		HttpPost post = new HttpPost(strUrl.toString());
		post.setHeader("Content-type", "application/json");

		BasicCredentialsProvider provider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("webrtc", "webrtc".toCharArray());
		provider.setCredentials(new AuthScope("172.16.45.135", 8080), credentials);

		CloseableHttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
		post.setEntity(new StringEntity(entity, ContentType.APPLICATION_JSON));
		return (ClassicHttpResponse) client.execute(post);
	}

}
