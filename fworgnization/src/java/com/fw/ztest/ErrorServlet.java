package com.fw.ztest;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jivesoftware.admin.AuthCheckFilter;

public class ErrorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String SERVICE_NAME = "fworgnization/*";

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO 自动生成的方法存根
		// super.doGet(req, resp);
		System.out.println("error servlet doget dispatcher!");
		
		
		response.sendRedirect("/plugins/fworgnization/error.jsp");
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO 自动生成的方法存根
		// super.doPost(req, resp);
		System.out.println("error servlet dopost dispatcher!");
		
		System.out.println(request.getRequestDispatcher("/error.jsp").FORWARD_PATH_INFO);
		System.out.println(request.getRequestDispatcher("/error.jsp").FORWARD_CONTEXT_PATH);
		
		request.getRequestDispatcher("/error.jsp").forward(request, response);
	}

	public void destroy() {
		// TODO 自动生成的方法存根
		super.destroy();
		AuthCheckFilter.removeExclude(SERVICE_NAME);
	}

	public void init() throws ServletException {
		// TODO 自动生成的方法存根
		super.init();
		AuthCheckFilter.addExclude(SERVICE_NAME);
	}
}
