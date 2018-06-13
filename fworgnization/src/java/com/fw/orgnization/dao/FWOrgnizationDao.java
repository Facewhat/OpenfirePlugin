package com.fw.orgnization.dao;

import java.util.List;
import java.util.Map;

import org.xmpp.packet.Message;

import com.fw.orgnization.entity.FWGroup;
import com.fw.orgnization.entity.FWGroupMessageHistory;
import com.fw.orgnization.entity.FWGroupUser;

public interface FWOrgnizationDao {
	public void saveMessage(FWGroupMessageHistory message) throws Exception;
	public void addMessage(Message message);
	// 得到所有的部门
		
	// 根据部门名称获得部门的人，可能会重复。A部门有张三，B部门可能也会有张三
	public List<FWGroupUser> getDepartmentUser(String groupname) throws Exception;
	
	public List<FWGroupUser> getUsersByDepartmentDisName(String disName) ;
	
	// 得到企业通讯录下所有的用户，不会重复。
	public List<FWGroupUser> getAllGroupUser() throws Exception;

	
	public void deleteUserSQLData();
	public void deleteGroupSQLData();
	public String downloadCSV();
	public List<FWGroup> getAllDepartment() throws Exception;
	public List<Map<String, String>> searchUserOrGroup(String searchCondition);
	public FWGroupUser getUserInfo(String groupName,String userNickName);
	public FWGroupUser getUserInfo(String userNickName);
	public String reviseUser(FWGroupUser fwGroupUser,String userNickName)throws Exception;
	public void addDepartment(FWGroup fwGroup) throws Exception;
	public void addUser(FWGroupUser fwGroupUser) throws Exception;
	public void reviseDepartment(FWGroup fwGroup, String groupDisplayName)throws Exception;
	public FWGroup getDepartmentInfo(String groupDisplayName);
	public void deleteDepartment(String groupDisplayName);
	public void deleteDepartmentUsers(String groupname);
	public List<FWGroup> getDepartmentsInfo(String treeNodeLevel);
	public String getGroupFatherId(String groupdisplayName);
	public void moveUser(String toGroupDisplayName, String userNickName);
	public void deleteUser(String groupDisplayName, String userNickName);
	public void deleteUser(String userNickName);
	public void reviseDepartmentUserGroupName(String oldGroupName, String newGroupName);
	
}
