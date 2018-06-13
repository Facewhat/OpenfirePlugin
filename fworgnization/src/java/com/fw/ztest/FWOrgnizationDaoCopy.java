package com.fw.ztest;

import java.util.List;

import org.xmpp.packet.Message;

import com.fw.orgnization.entity.FWGroup;
import com.fw.orgnization.entity.FWGroupMessageHistory;
import com.fw.orgnization.entity.FWGroupUser;

public interface FWOrgnizationDaoCopy {
	public void saveMessage(FWGroupMessageHistory message) throws Exception;
	public void addMessage(Message message);
	// 得到所有的部门
	public List<FWGroup> getAllDepartment() throws Exception;
		
	// 根据部门名称获得部门的人，可能会重复。A部门有张三，B部门可能也会有张三
	public List<FWGroupUser> getDepartmentUser(String groupname) throws Exception;
	
	public List<FWGroupUser> getUsersByDepartmentDisName(String disName) ;
	
	// 得到企业通讯录下所有的用户，不会重复。
	public List<FWGroupUser> getAllGroupUser() throws Exception;

	public String getGroupFatherId(String disName);
	
	public FWGroupUser getUserInfoByDisName(String disName);
	public String reviseUserInfo(FWGroupUser fwGroupUser,String disName);
	public FWGroup getDepartmentInfoByDisGroupName(String disGroupName);
	public void deleteUserByUserNickName(String userNickName);
	public String reviseGroupInfoByDisName(FWGroup fwGroup, String disName);
	public void reviseGroupNameOfUserByGroupName(String groupName,String disName);	
	
	public void addDepartment(FWGroup fwGroup) throws Exception;
}
