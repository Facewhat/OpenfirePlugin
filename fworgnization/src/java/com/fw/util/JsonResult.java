package com.fw.util;

import java.util.List;
import java.util.Map;

import com.fw.orgnization.entity.FWGroup;

public class JsonResult {
	private String resultCode;
	
	private String info;
	
	private List<Map<String,String>> userInfoList;

	private List<Map<String,String>> groupInfoList;
	
	private String orgTreeJson;
	
	private String nodeLevel;
	
    private List<Map<String, String>> groupAndUserInfo;
    
	public String getNodeLevel() {
		return nodeLevel;
	}

	public void setNodeLevel(String nodeLevel) {
		this.nodeLevel = nodeLevel;
	}

	public String getOrgTreeJson() {
		return orgTreeJson;
	}

	public void setOrgTreeJson(String orgTreeJson) {
		this.orgTreeJson = orgTreeJson;
	}

	public List<Map<String, String>> getGroupInfoList() {
		return groupInfoList;
	}

	public void setGroupInfoList(List<Map<String, String>> groupInfoList) {
		this.groupInfoList = groupInfoList;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public List<Map<String, String>> getUserInfoList() {
		return userInfoList;
	}

	public void setUserInfoList(List<Map<String, String>> userInfoList) {
		this.userInfoList = userInfoList;
	}

	public List<Map<String, String>> getGroupAndUserInfo() {
		return groupAndUserInfo;
	}

	public void setGroupAndUserInfo(List<Map<String, String>> groupAndUserInfo) {
		this.groupAndUserInfo = groupAndUserInfo;
	}
	
	
}
