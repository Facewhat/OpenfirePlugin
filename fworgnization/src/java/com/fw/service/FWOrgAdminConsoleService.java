package com.fw.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONArray;
import com.csvreader.CsvReader;
import com.fw.orgnization.dao.FWOrgnizationDao;
import com.fw.orgnization.dao.FWOrgnizationDaoImpl;
import com.fw.orgnization.entity.FWGroup;
import com.fw.orgnization.entity.FWGroupUser;
import com.fw.util.FWJsonUtils;
import com.fw.util.FWStringUtils;
import com.fw.util.JsonResult;
import com.fw.util.OrgTreeNode;

public class FWOrgAdminConsoleService {
	private FWOrgnizationDao fwOrgnizationDao;

	public String addDepartmnet(FWGroup fwGroup) {
		// TODO Auto-generated method stub
		fwOrgnizationDao = new FWOrgnizationDaoImpl();
		JsonResult jsonResult = new JsonResult();
		
		try {
			fwOrgnizationDao.addDepartment(fwGroup);
			jsonResult.setResultCode("1");
			jsonResult.setInfo("add department success.");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			jsonResult.setResultCode("-1");
			jsonResult.setInfo("add department fail.please check the input.");
			
		}
		
		return JSONArray.fromObject(jsonResult).toString();
	}
	
	public String loadOrgTree() {

		fwOrgnizationDao = new FWOrgnizationDaoImpl();
		JsonResult jsonResult = new JsonResult();
		JSONArray jsonArray = null;
		try {
			List<FWGroup> fwGroups = fwOrgnizationDao.getAllDepartment();
			OrgTreeNode root = FWJsonUtils.FWGroupToOrgTreeNode(fwGroups);

			List<OrgTreeNode> groupNodes = root.getNodes();
			for (OrgTreeNode groupNode : groupNodes) {
				List<FWGroupUser> fwGroupUsers = fwOrgnizationDao.getUsersByDepartmentDisName(groupNode.getText());
				groupNode.setNodes(FWJsonUtils.FWUserToOrgTreeNodes(fwGroupUsers));
			}
			jsonArray = JSONArray.fromObject(root);
			jsonResult.setResultCode("1");
			jsonResult.setInfo("load orgnization success.");
			jsonResult.setOrgTreeJson(jsonArray.toString());
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			jsonResult.setResultCode("-1");
			jsonResult.setInfo("load orgnization fail.error info: unknown.");
		}
		return JSONArray.fromObject(jsonResult).toString();
	}
	
	public String getOrgTreeLevel(String disPlayName) {
		
		fwOrgnizationDao = new FWOrgnizationDaoImpl();
		JsonResult jsonResult = new JsonResult();
		String nodeLevel = fwOrgnizationDao.getGroupFatherId(disPlayName);
		if (FWStringUtils.isStringNullOrEmpty(nodeLevel)) {
			nodeLevel = "2";
		}
		jsonResult.setResultCode("1");
		jsonResult.setInfo("get org tree level success.");
		jsonResult.setNodeLevel(nodeLevel);
		
		return JSONArray.fromObject(jsonResult).toString();
	}

	public String reviseDepartment(FWGroup fwGroup, String groupDisplayName) {
		// TODO Auto-generated method stub
		fwOrgnizationDao = new FWOrgnizationDaoImpl();
		JsonResult jsonResult = new JsonResult();
		
		try {
			FWGroup oldFWGroup = fwOrgnizationDao.getDepartmentInfo(groupDisplayName);
			fwOrgnizationDao.reviseDepartmentUserGroupName(oldFWGroup.getGroupname(),fwGroup.getGroupname());
			fwOrgnizationDao.reviseDepartment(fwGroup,groupDisplayName);
			jsonResult.setResultCode("1");
			jsonResult.setInfo("revise department success.");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			jsonResult.setResultCode("-1");
			jsonResult.setInfo("revise department fail.please check the input.");
		}
		return JSONArray.fromObject(jsonResult).toString();
	}

	public String getDepartmentInfo(String groupDisplayName) {
		// TODO Auto-generated method stub
		fwOrgnizationDao = new FWOrgnizationDaoImpl();
		FWGroup fwGroup = null;
		List<Map<String, String>> groupInfoList = null;
		JsonResult jsonResult = new JsonResult();
		fwGroup = fwOrgnizationDao.getDepartmentInfo(groupDisplayName);
		if (fwGroup == null) {
			jsonResult.setResultCode("-1");
			jsonResult.setInfo("can't find the group whoes name is  [ " + groupDisplayName + " ] .");

		} else {
			jsonResult.setResultCode("1");
			jsonResult.setInfo("success to find the group: [ " + groupDisplayName + " ] .");

			Map<String, String> groupMap = new HashMap<String, String>();
			groupMap.put("groupName", fwGroup.getGroupname());
			groupMap.put("groupDisName", fwGroup.getDisplayname());
			groupInfoList = new ArrayList<Map<String, String>>();
			groupInfoList.add(groupMap);

		}
		jsonResult.setGroupInfoList(groupInfoList);
		return JSONArray.fromObject(jsonResult).toString();
	}

	public void deleteDepartment(String groupDisplayName) {
		// TODO Auto-generated method stub
		fwOrgnizationDao = new FWOrgnizationDaoImpl();
		FWGroup fwGroup = fwOrgnizationDao.getDepartmentInfo(groupDisplayName);
		fwOrgnizationDao.deleteDepartmentUsers(fwGroup.getGroupname());
		fwOrgnizationDao.deleteDepartment(groupDisplayName);
	}
	
	public String reviseUser(FWGroupUser fwGroupUser,String userNickName) {
		
		fwOrgnizationDao = new FWOrgnizationDaoImpl();
		JsonResult jsonResult = new JsonResult();
		
		try {
			fwOrgnizationDao.reviseUser(fwGroupUser, userNickName);
			jsonResult.setResultCode("1");
			jsonResult.setInfo("success to reviseUser.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			jsonResult.setResultCode("-1");
			jsonResult.setInfo("fali to reviseUser.please to check the input.");
		}
		
		
		return JSONArray.fromObject(jsonResult).toString();
		
	}

	public String getUserInfo(String userNickName) {
		
		fwOrgnizationDao = new FWOrgnizationDaoImpl();
		FWGroupUser fwGroupUser = fwOrgnizationDao.getUserInfo(userNickName);
		JsonResult jsonResult = new JsonResult();
		if (fwGroupUser == null) {

			jsonResult.setResultCode("-1");
			jsonResult.setInfo("can't find the user : [ " + userNickName + " ] .");

		} else {
			jsonResult.setResultCode("1");
			jsonResult.setInfo("success to find [ " + userNickName + " ] .");
			Map<String, String> userMap = new HashMap<String, String>();
			userMap.put("groupName", fwGroupUser.getGroupname());
			userMap.put("userName", fwGroupUser.getUsername());
			userMap.put("userNickName", fwGroupUser.getUsernickname());
			userMap.put("fullPinYin", fwGroupUser.getFullpinyin());
			userMap.put("shortPinYin", fwGroupUser.getShortpinyin());
			List<Map<String, String>> userList = new ArrayList<Map<String, String>>();
			userList.add(userMap);
			jsonResult.setUserInfoList(userList);

		}
		return JSONArray.fromObject(jsonResult).toString();
	}

	public String getDepartments(String treeNodeLevel) {
		// TODO Auto-generated method stub
		fwOrgnizationDao = new FWOrgnizationDaoImpl();
		List<FWGroup> groupList = null;
		groupList = fwOrgnizationDao.getDepartmentsInfo(treeNodeLevel);
		JsonResult jsonResult = new JsonResult();
		List<Map<String, String>> groupInfoList = null;
		Map<String, String> groupMap;
		if(groupList == null) {
			jsonResult.setResultCode("-1");
			jsonResult.setInfo("get dempartments fail.");
			
		}else {
			jsonResult.setResultCode("1");
			jsonResult.setInfo("get department success.");
			
			groupInfoList = new ArrayList<Map<String,String>>();
			
			for (FWGroup fwGroup : groupList) {
				groupMap = new HashMap<String,String>();
				groupMap.put("groupName", fwGroup.getGroupname());
				groupMap.put("groupDisplayName", fwGroup.getDisplayname());
				
				groupInfoList.add(groupMap);
			}
			
			jsonResult.setGroupInfoList(groupInfoList);
		}
		
		return JSONArray.fromObject(jsonResult).toString();
	}

	public void moveUser(String toGroupDisplayName, String groupDisplayName, String userNickName) {
		// TODO Auto-generated method stub
		fwOrgnizationDao = new FWOrgnizationDaoImpl();
		FWGroup toFWGroup = fwOrgnizationDao.getDepartmentInfo(toGroupDisplayName);
		FWGroupUser fwGroupUser = null; 
		fwGroupUser = fwOrgnizationDao.getUserInfo(toFWGroup.getGroupname(), userNickName);
		if(fwGroupUser == null) 
		{
			fwOrgnizationDao.moveUser(toFWGroup.getGroupname(),userNickName);
		}
		else {
			if(toGroupDisplayName.equals(groupDisplayName))
			{
				return ;
			}
			else {
				fwOrgnizationDao.deleteUser(groupDisplayName,userNickName);
			}
		}
	}

	public void deleteUser(String userNickName) {
		// TODO Auto-generated method stub
		fwOrgnizationDao = new FWOrgnizationDaoImpl();
		fwOrgnizationDao.deleteUser(userNickName);
	}
	
	public String addUser(FWGroupUser fwGroupUser,String groupDisplayName) {
		// TODO Auto-generated method stub
		fwOrgnizationDao = new FWOrgnizationDaoImpl();
		
		JsonResult jsonResult = new JsonResult();
		FWGroupUser temp = fwOrgnizationDao.getUserInfo(groupDisplayName,fwGroupUser.getUsernickname());
		if(temp == null) {
			FWGroup fwGroup = fwOrgnizationDao.getDepartmentInfo(groupDisplayName);
			fwGroupUser.setGroupname(fwGroup.getGroupname());
			try {
				fwOrgnizationDao.addUser(fwGroupUser);
				jsonResult.setResultCode("1");
				jsonResult.setInfo("success to add user .");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				jsonResult.setResultCode("-1");
				jsonResult.setInfo("fail to add user .");
			}
		}
		else {
			jsonResult.setResultCode("-1");
			jsonResult.setInfo("fail to add user .user has existed .");
		}
		
		return JSONArray.fromObject(jsonResult).toString();
	}
	
	public String searchUserOrGroup(String searchCondition) {
		List<Map<String, String>> groupAndUserInfo = null;
		JsonResult jsonResult = new JsonResult();
		fwOrgnizationDao = new FWOrgnizationDaoImpl();
		groupAndUserInfo = fwOrgnizationDao.searchUserOrGroup(searchCondition);
		
		if(groupAndUserInfo == null) {
			jsonResult.setResultCode("-1");
			jsonResult.setInfo("can't find result.please check the search condition [ " + searchCondition + " ] .");
			
		}
		else {
			
			jsonResult.setResultCode("1");
			jsonResult.setInfo("success to get search result.");
			jsonResult.setGroupAndUserInfo(groupAndUserInfo);
		}
		return JSONArray.fromObject(jsonResult).toString();
	}
	
	public String downloadCSV() {
		fwOrgnizationDao = new FWOrgnizationDaoImpl();
		
		return fwOrgnizationDao.downloadCSV();
	}
	
	public String uploadCSV(CsvReader csvReader) {
		fwOrgnizationDao = new FWOrgnizationDaoImpl();
		fwOrgnizationDao.deleteUserSQLData();
		fwOrgnizationDao.deleteGroupSQLData();
		JsonResult jsonResult = new JsonResult();
		try {
			csvReader.readHeaders();
			while (csvReader.readRecord()){
			    if(csvReader.get("isorgnization").equals("1")) {
			    	FWGroup fwGroup = new FWGroup();
			    	fwGroup.setGroupname(csvReader.get("groupname"));
			    	fwGroup.setDisplayname(csvReader.get("displayname"));
			    	fwGroup.setGroupfathername(csvReader.get("groupfathername"));
			    	fwGroup.setIsorgnization(true);
			    	fwOrgnizationDao.addDepartment(fwGroup);
			    }
			    	
			    FWGroupUser fwGroupUser = new FWGroupUser();
			    fwGroupUser.setGroupname(csvReader.get("usergroupname"));
			    fwGroupUser.setUsername(csvReader.get("username"));
			    fwGroupUser.setUsernickname(csvReader.get("usernickname"));
			    fwGroupUser.setFullpinyin(csvReader.get("fullpinyin"));
			    fwOrgnizationDao.addUser(fwGroupUser);
			}
			jsonResult.setResultCode("1");
			jsonResult.setInfo("success to upload csv file .");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			jsonResult.setResultCode("-1");
			jsonResult.setInfo("fail to upload csv file. from csv reader format error .");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			jsonResult.setResultCode("-1");
			jsonResult.setInfo("fail to upload csv file.from SQL add error .");
		}
		
		return JSONArray.fromObject(jsonResult).toString();
	}
}
