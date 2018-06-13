package com.facewhat.archive.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jivesoftware.admin.AuthCheckFilter;

import com.facewhat.archive.service.ArchiveService;
import com.fw.util.FWStringUtils;

public class ArchiveServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String SERVICE_NAME = "fwarchive/*";
	private String requestMethod;
	private ArchiveService archiveService;
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO 自动生成的方法存根
		if(request.getMethod().equals("GET"))
		{
			this.doPost(request, response);
		}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO 自动生成的方法存根
		requestMethod = request.getParameter("requestMethod");
		archiveService = new ArchiveService();
		
		if(!FWStringUtils.isStringNullOrEmpty(requestMethod) && requestMethod.equals("searchArchive")) 
		{
			String sender = request.getParameter("Sender");
			String receiver  = request.getParameter("Receiver");
			String startDateStr = request.getParameter("startDate");
			String endDateStr = request.getParameter("endDate");
			
			response.setCharacterEncoding("UTF-8"); 
            response.getWriter().print(archiveService.searchArchiveConversation(sender, receiver, startDateStr, endDateStr));
		}
		else if(!FWStringUtils.isStringNullOrEmpty(requestMethod) && requestMethod.equals("getMessage")) 
		{
			String id = request.getParameter("conversationId");

			response.setCharacterEncoding("UTF-8"); 
            response.getWriter().print(archiveService.getMessage(id));
		}else if(!FWStringUtils.isStringNullOrEmpty(requestMethod) && requestMethod.equals("deleteMessage")) 
		{
			String id = request.getParameter("messageId");

			response.setCharacterEncoding("UTF-8"); 
            response.getWriter().print(archiveService.deleteMessage(id));
		}else if(!FWStringUtils.isStringNullOrEmpty(requestMethod) && requestMethod.equals("searchKeyword")) 
		{
			String conversationId = request.getParameter("conversationId");
			String keyword = request.getParameter("keyword");
			response.setCharacterEncoding("UTF-8"); 
            response.getWriter().print(archiveService.searchMessageByKeyword(conversationId, keyword));
		}else if(!FWStringUtils.isStringNullOrEmpty(requestMethod) && requestMethod.equals("archiveAudit")) 
		{
			String id = request.getParameter("messageId");

			response.setCharacterEncoding("UTF-8"); 
            response.getWriter().print(archiveService.deleteMessage(id));
		}
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
