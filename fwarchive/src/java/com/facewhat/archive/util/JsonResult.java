package com.facewhat.archive.util;

import java.util.List;
import java.util.Map;

import com.facewhat.archive.model.Conversation;



public class JsonResult {
	
	private String resultCode;
	
	private String info;
	
	private List<Map<String,String>> messageList;
	
	private List<Map<String,String>> conversationMapList;
	
	
	
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

	public List<Map<String, String>> getMessageList() {
		return messageList;
	}

	public void setMessageList(List<Map<String, String>> messageList) {
		this.messageList = messageList;
	}

	public List<Map<String,String>> getConversationMapList() {
		return conversationMapList;
	}

	public void setConversationMapList(List<Map<String,String>> conversationMapList) {
		this.conversationMapList = conversationMapList;
	}

}
