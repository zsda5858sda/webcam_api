package com.ubot.utils;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet Filter implementation class FileFilter
 */
public class FileFilter implements Filter {

	/**
	 * Default constructor.
	 */
	public FileFilter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String num1 = request.getParameter("content");
		String num2 = request.getParameter("fileName");
        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Strict-Transport-Security", "max-age=31622400; includeSubDomains");
		if (num1 != null && num2 != null && !num1.isEmpty() && !num2.isEmpty()) {
			chain.doFilter(request, response);
		} else {
			response.setContentType("text/plain; charset=UTF-8");
			PrintWriter printer = response.getWriter();
			System.out.println("欄位不能留空，請確實填入要運算的數字");
			printer.print("欄位不能留空，請確實填入要運算的數字");
		}

	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		System.out.println("I am filter");
	}

}
