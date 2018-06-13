package com.fw.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.jivesoftware.admin.AuthCheckFilter;

import com.csvreader.CsvReader;
import com.fw.orgnization.entity.FWGroup;
import com.fw.orgnization.entity.FWGroupUser;
import com.fw.service.FWOrgAdminConsoleService;
import com.fw.util.FWStringUtils;
import com.fw.util.JsonResult;

import net.sf.json.JSONArray;

public class OrgnizationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String SERVICE_NAME = "fworgnization/*";
	private FWOrgAdminConsoleService fwOrgAdminConsoleService;
	private String requestMethod;
	

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if(request.getMethod().equals("GET"))
		{
			this.doPost(request, response);
		}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO 自动生成的方法存根
		requestMethod = request.getParameter("requestMethod");
		fwOrgAdminConsoleService = new FWOrgAdminConsoleService();
		if(!FWStringUtils.isStringNullOrEmpty(requestMethod) && requestMethod.equals("loadOrgTree")) 
		{
			response.setCharacterEncoding("UTF-8"); 
			response.getWriter().print(fwOrgAdminConsoleService.loadOrgTree());
		}
		else if(!FWStringUtils.isStringNullOrEmpty(requestMethod) && requestMethod.equals("getOrgTreeLevel"))
		{
			String nodeName = request.getParameter("nodeName");
			response.setCharacterEncoding("UTF-8"); 
			response.getWriter().print(fwOrgAdminConsoleService.getOrgTreeLevel(nodeName));
		}
		else if(!FWStringUtils.isStringNullOrEmpty(requestMethod) && requestMethod.equals("addDepartment"))
		{
			String groupdisplayName = request.getParameter("groupdisplayName");
			String groupName = request.getParameter("groupName");
			FWGroup fwGroup = new FWGroup();
			fwGroup.setDisplayname(groupdisplayName);
			fwGroup.setGroupname(groupName);
			fwGroup.setGroupfathername("1");
			fwGroup.setIsorgnization(true);
		
			response.setCharacterEncoding("UTF-8"); 
			response.getWriter().print(fwOrgAdminConsoleService.addDepartmnet(fwGroup));
		}else if(!FWStringUtils.isStringNullOrEmpty(requestMethod) && requestMethod.equals("reviseDepartment"))
		{
			String groupDisplayName  = request.getParameter("groupDisplayName");
			String newGroupdisplayName = request.getParameter("newGroupdisplayName");
			String newGroupName = request.getParameter("newGroupName");
			FWGroup fwGroup = new FWGroup();
			fwGroup.setDisplayname(newGroupdisplayName);
			fwGroup.setGroupname(newGroupName);
		
			response.setCharacterEncoding("UTF-8"); 
			response.getWriter().print(fwOrgAdminConsoleService.reviseDepartment(fwGroup,groupDisplayName));
		}else if(!FWStringUtils.isStringNullOrEmpty(requestMethod) && requestMethod.equals("getDepartmentInfo"))
		{
			String groupDisplayName  = request.getParameter("groupDisplayName");
			
			response.setCharacterEncoding("UTF-8"); 
			response.getWriter().print(fwOrgAdminConsoleService.getDepartmentInfo(groupDisplayName));
		}else if(!FWStringUtils.isStringNullOrEmpty(requestMethod) && requestMethod.equals("deleteDepartment"))
		{
			String groupDisplayName  = request.getParameter("groupDisplayName");
			fwOrgAdminConsoleService.deleteDepartment(groupDisplayName);
			response.setCharacterEncoding("UTF-8"); 
			response.sendRedirect("/plugins/fworgnization/org_view.jsp");
		}else if(!FWStringUtils.isStringNullOrEmpty(requestMethod) && requestMethod.equals("reviseUser"))
		{
			
			String newUserNickName = request.getParameter("newUserNickName");
			String newShortPinYin = request.getParameter("newShortPinYin");
			String newFullPinYin = request.getParameter("newFullPinYin");
			String userNickName = request.getParameter("userNickName");
			FWGroupUser fwGroupUser = new FWGroupUser();
			
			fwGroupUser.setUsernickname(newUserNickName);
			fwGroupUser.setShortpinyin(newShortPinYin);
			fwGroupUser.setFullpinyin(newFullPinYin);
			fwGroupUser.setUsername(newShortPinYin);
			
			response.setCharacterEncoding("UTF-8"); 
			response.getWriter().print(fwOrgAdminConsoleService.reviseUser(fwGroupUser,userNickName));
		}else if(!FWStringUtils.isStringNullOrEmpty(requestMethod) && requestMethod.equals("getUserInfo"))
		{
			String userNickName = request.getParameter("userNickName");
			response.setCharacterEncoding("UTF-8"); 
			response.getWriter().print(fwOrgAdminConsoleService.getUserInfo(userNickName));
		}else if(!FWStringUtils.isStringNullOrEmpty(requestMethod) && requestMethod.equals("getDepartments"))
		{
			String treeNodeLevel = request.getParameter("treeNodeLevel");
			response.setCharacterEncoding("UTF-8"); 
			response.getWriter().print(fwOrgAdminConsoleService.getDepartments(treeNodeLevel));
		}else if(!FWStringUtils.isStringNullOrEmpty(requestMethod) && requestMethod.equals("moveUser"))
		{
			String toGroupDisplayName = request.getParameter("toGroupDisplayName");
			String groupDisplayName = request.getParameter("groupDisplayName");
			String userNickName = request.getParameter("userNickName");
			fwOrgAdminConsoleService.moveUser(toGroupDisplayName,groupDisplayName,userNickName);
			response.setCharacterEncoding("UTF-8"); 
			response.sendRedirect("/plugins/fworgnization/org_view.jsp");
		}else if(!FWStringUtils.isStringNullOrEmpty(requestMethod) && requestMethod.equals("deleteUser"))
		{
			String fromWhere = request.getParameter("fromUserJsp");
			response.setCharacterEncoding("UTF-8"); 
			if(!FWStringUtils.isStringNullOrEmpty(fromWhere)) {
				String userNickName = request.getParameter("userNickName");
				JsonResult jsonResult = new JsonResult();
				if(!FWStringUtils.isStringNullOrEmpty(userNickName)) {
					fwOrgAdminConsoleService.deleteUser(userNickName);
					jsonResult.setResultCode("1");
					jsonResult.setInfo("success delete user [ " + userNickName + " ] .");
				}
				else {
					jsonResult.setResultCode("-1");
					jsonResult.setInfo("fail to delete user [ " + userNickName + " ] . user's name is null. ");
				}
				response.getWriter().print(JSONArray.fromObject(jsonResult).toString());
			}else {
				String userNickName = request.getParameter("userNickName");
				fwOrgAdminConsoleService.deleteUser(userNickName);
				response.sendRedirect("/plugins/fworgnization/org_view.jsp");
			}
			
		}else if(!FWStringUtils.isStringNullOrEmpty(requestMethod) && requestMethod.equals("addUser"))
		{

			FWGroupUser fwGroupUser = new FWGroupUser();
			fwGroupUser.setUsernickname(request.getParameter("nickName"));
			fwGroupUser.setShortpinyin(request.getParameter("shortSpelling"));
			fwGroupUser.setFullpinyin(request.getParameter("fullSpelling"));
			fwGroupUser.setUsername(request.getParameter("shortSpelling"));
			response.setCharacterEncoding("UTF-8"); 
			System.out.println("servlet .");
			response.getWriter().print(fwOrgAdminConsoleService.addUser(fwGroupUser,request.getParameter("displayname")));
		}else if(!FWStringUtils.isStringNullOrEmpty(requestMethod) && requestMethod.equals("searchGroupOrUser"))
		{
			String searchCondition = request.getParameter("searchCondition");
			response.setCharacterEncoding("UTF-8"); 
			response.getWriter().print(fwOrgAdminConsoleService.searchUserOrGroup(searchCondition));
		}else if(!FWStringUtils.isStringNullOrEmpty(requestMethod) && requestMethod.equals("uploadCSV"))
		{
			
			JsonResult jsonResult = new JsonResult();
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setSizeMax(8*1024*1024);
            upload.setHeaderEncoding("UTF-8");
            try {
				@SuppressWarnings("unchecked")
				List<FileItem> list = upload.parseRequest(request);
				for (FileItem fileItem : list) {
					if(fileItem.getName()!=null) {
						Reader reader = null;
						reader = new InputStreamReader(fileItem.getInputStream(),Charset.forName("GBK"));
						CsvReader csvReader  = new CsvReader(reader);
						response.setCharacterEncoding("UTF-8"); 
			            response.getWriter().print(fwOrgAdminConsoleService.uploadCSV(csvReader));
					}
					
				}
			} catch (FileUploadException e) {
				e.printStackTrace();
				jsonResult.setResultCode("-1");
				jsonResult.setInfo("fail to upload csv file. from servlet parse error.");
				response.setCharacterEncoding("UTF-8"); 
				response.getWriter().print(JSONArray.fromObject(jsonResult).toString());
			}
            
		}else if(!FWStringUtils.isStringNullOrEmpty(requestMethod) && requestMethod.equals("downloadCSV"))
		{
			
			File file = new File(fwOrgAdminConsoleService.downloadCSV());
			response.setContentType("application/csv;charset=gbk");
	        response.setHeader("Content-Disposition", "attachment;filename="+file.getName());  
	        InputStream in = new FileInputStream(file);  
	        OutputStream out = response.getOutputStream();  
	        
	        int byteData;  
	        while((byteData=in.read())!= -1)  
	        {  
	            out.write(byteData);  
	        }  
	        in.close();  
	        out.close(); 
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
