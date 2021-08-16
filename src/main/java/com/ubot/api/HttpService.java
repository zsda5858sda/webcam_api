package com.ubot.api;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

/**
 * Servlet implementation class HttpService
 */
public class HttpService extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public HttpService() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.getWriter().append("hello world");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String json = request.getReader().lines().collect(Collectors.joining());
		StringBuffer strUrl = new StringBuffer("http://172.16.45.135:8080/EaiHub/resCommon/getAd01");
		HttpPost post = new HttpPost(strUrl.toString());
		post.setHeader("Content-type", "application/json");
		
		BasicCredentialsProvider provider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("webrtc",
				"webrtc".toCharArray());
		provider.setCredentials(new AuthScope("172.16.45.135", 8080), credentials);

		CloseableHttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
		post.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
		ClassicHttpResponse clientResponse = (ClassicHttpResponse) client.execute(post);
		if (clientResponse.getCode() == 200) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				HttpEntity entity = clientResponse.getEntity();
				String responseString = EntityUtils.toString(entity, "UTF-8");
				ObjectNode result = mapper.createObjectNode();
				Map<String, String> jsonResult = mapper.readValue(responseString, Map.class);
				String rc2 = jsonResult.get("rc2");
				System.out.println(rc2);
				if (rc2.equals("M000")) {
					result.put("message", "登入成功");
					result.put("code", "0");
				} else {
					result.put("message", "登入失敗");
					result.put("code", "1");
					result.put("reason", jsonResult.get("msg2"));
				}
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().print(mapper.writeValueAsString(result));
			} catch (ParseException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("response state: " + clientResponse.getCode());
			response.getWriter().print("{\"message\": \"請求失敗\"}");
		}
	}

}
