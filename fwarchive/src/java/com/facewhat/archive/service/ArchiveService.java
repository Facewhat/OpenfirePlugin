package com.facewhat.archive.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facewhat.archive.manager.PersistenceManager;
import com.facewhat.archive.manager.PersistenceManagerImpl;
import com.facewhat.archive.model.Conversation;
import com.facewhat.archive.util.JsonResult;
import com.facewhat.archive.util.Tool;

import net.sf.json.JSONArray;

public class ArchiveService {
	private PersistenceManager persistenceManager;
	
	public String searchArchiveConversation(String sender,String receiver,String startDateStr,String endDateStr) {
		
		persistenceManager = new PersistenceManagerImpl();
		JsonResult jsonResult = new JsonResult();
		List<Conversation> conversationList = null;
		List<Map<String, String>> conversationMapList = null;
		Date startDate = null;
		Date endDate = null;
		Map<String, String> conversationMap = null;
		if(Tool.isStringNullOrEmpty(startDateStr))
			startDateStr = "1999-01-01 00:00:00";
		if(Tool.isStringNullOrEmpty(endDateStr))
			endDateStr = "2030-01-01 23:59:59";
		SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			if(startDateStr != null) {
				startDateStr = startDateStr + " 00:00:00";
				startDate = simpleDateFormat.parse(startDateStr);
			}
			if(endDateStr != null) {
				endDateStr = endDateStr + " 23:59:59";
				endDate = simpleDateFormat.parse(endDateStr);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		conversationList = persistenceManager.searchArchiveConversation(sender,receiver,startDate,endDate);
		
		if(conversationList != null) {
			conversationMapList = new ArrayList<Map<String, String>>();
			
			for (Conversation  conversation: conversationList) {
				conversationMap = new HashMap<String, String>();
				conversationMap.put("id", conversation.getId().toString());
				conversationMap.put("to", conversation.getOwnerJid());
				conversationMap.put("from", conversation.getWithJid());
				conversationMap.put("start", simpleDateFormat.format(conversation.getStart().getTime()));
				conversationMap.put("end", simpleDateFormat.format(conversation.getEnd()));
				conversationMap.put("resource", conversation.getOwnerResource());
				conversationMapList.add(conversationMap);
			}
			jsonResult.setResultCode("1");
			jsonResult.setInfo("success to search archive conversation .");
			jsonResult.setConversationMapList(conversationMapList);
		}
		else {
			jsonResult.setResultCode("1");
			jsonResult.setInfo("fail to search archive conversation . conversationList is empty. ");
		}
		String json = null;
		try {
			json = JSONArray.fromObject(jsonResult).toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return json;
	}
	
	public String getMessage(String id) {
		persistenceManager = new PersistenceManagerImpl();
		JsonResult jsonResult = new JsonResult();
		List<Map<String, String>> messageList = null;
		messageList = persistenceManager.getMessageByConversation(id);
		if(messageList != null) {
			jsonResult.setResultCode("1");
			jsonResult.setInfo("success get message.");
			jsonResult.setMessageList(messageList);
		}
		else {
			jsonResult.setResultCode("-1");
			jsonResult.setInfo("get message error .");
		}
		String json = null;
		try {
		json = JSONArray.fromObject(jsonResult).toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return json;
	}
	
	public String deleteMessage(String id) {
		persistenceManager = new PersistenceManagerImpl();
		persistenceManager.deleteMessage(id);
		JsonResult jsonResult = new JsonResult();
		jsonResult.setResultCode("1");
		jsonResult.setInfo("success to delete message.");
		
		String json = null;
		try {
			json = JSONArray.fromObject(jsonResult).toString();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		return json;
	}
	
	public String searchMessageByKeyword(String conversationId , String keyword) {
		persistenceManager = new PersistenceManagerImpl();
		
		JsonResult jsonResult = new JsonResult();
		List<Map<String, String>> messageList = null;
		messageList  = persistenceManager.searchMessageByKeyword(conversationId,keyword);
		if(messageList != null) {
			jsonResult.setResultCode("1");
			jsonResult.setInfo("success get message.");
			jsonResult.setMessageList(messageList);
		}
		else {
			jsonResult.setResultCode("-1");
			jsonResult.setInfo("get message error .");
		}
		String json = null;
		try {
		json = JSONArray.fromObject(jsonResult).toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return json;
	}
	
	public void test() {
		
		System.out.println("test");
		persistenceManager = new PersistenceManagerImpl();
		Long s , e;
		s = 1524464821740L;
		e = 1524465536531L;
		Date startTime = new Date(s);
		Date endTime = new Date(e);
		
	}
	public static void main(String[] args) {
		
	}
}
