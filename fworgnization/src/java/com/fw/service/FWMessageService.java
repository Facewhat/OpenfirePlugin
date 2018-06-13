package com.fw.service;

import java.util.Calendar;
import java.util.List;

import org.jivesoftware.openfire.MessageRouter;
import org.jivesoftware.openfire.PacketRouter;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.xmpp.packet.Message;
import org.xmpp.packet.PacketError.Condition;

import com.fw.orgnization.entity.FWGroupMessageHistory;
import com.fw.orgnization.entity.FWGroupUser;
import com.fw.util.FWStringUtils;

public class FWMessageService {
	private FWOrgModuleService fwOrgModuleService = null;
	
	// 专门用来发送消息的
	private MessageRouter messageRouter = null;
	// 
	private PacketRouter router = null;
	
	
	public FWMessageService(FWOrgModuleService fwOrgModuleService) {
		this.fwOrgModuleService = fwOrgModuleService;
		messageRouter = XMPPServer.getInstance().getMessageRouter();
		router = XMPPServer.getInstance().getPacketRouter();
		
	}
	
	public void sendMessageError(Message message, Condition c) {
		 Message errorResponse = message.createCopy();
         errorResponse.setError(c);
         errorResponse.setFrom(message.getTo());
         errorResponse.setTo(message.getFrom());
         // Send the response
         router.route(errorResponse);
	}
	
	// 私聊
	public void sendMessageToUser(Message message) {
		// <message from="lxy@openfire/res" to="kfb@fwgroup.openfire/lp"/>
		// <message from="kfb@fwgroup.openfire/lxy" to="lp@openfire"/>
		
		message.setFrom(message.getTo().toBareJID() + "/" + message.getFrom().getNode());
		String username = message.getTo().getResource();
		// sendPacketToUsername(message.getTo().getResource(), message);
		try {
			sendMessageOrSaveMessage(username, message);
		} catch (UserNotFoundException e) {
			System.out.println("该用户不存在" + username);
			sendMessageError(message, Condition.bad_request);
		}
	}
	
	// 发给一个组
	public void sendMessageToGroup(Message message, List<FWGroupUser> fwGroupUsers) {
		sendMessageToOrg(message, fwGroupUsers);
	}
	
	// 发给整个企业通讯录
	public void sendMessageToOrg(Message message, List<FWGroupUser> fwGroupUsers) {
		// Collection<String> usernames = UserManager.getInstance().getUsernames();
		// List<FWGroupUser> fwGroupUsers = fwOrgModuleService.getAllFWGroupUsers();
		FWGroupMessageHistory fwGroupMessageHistory = new FWGroupMessageHistory();
		fwGroupMessageHistory.setGroupname(message.getTo().getNode());
		fwGroupMessageHistory.setUsername(message.getFrom().getNode());
		fwGroupMessageHistory.setSendtDate(Calendar.getInstance().getTime());
		fwGroupMessageHistory.setBody(message.getBody());
		
		
		try {
			fwOrgModuleService.getFWOrgnizationDao().saveMessage(fwGroupMessageHistory);
			
			
		} catch (Exception e) {
			System.out.println("保存为历史消息失败：" + e.getMessage());
		}
		
		message.setFrom(message.getTo() + "/" + message.getFrom().getNode());
		for(FWGroupUser user : fwGroupUsers) {
			String username = user.getUsername();
			if(FWStringUtils.isStringNullOrEmpty(username)) {
				continue;
			}
			try {
				sendMessageOrSaveMessage(username, message);
			} catch (UserNotFoundException e) {
				System.out.println("该用户不存在" + username);
				sendMessageError(message, Condition.bad_request);
			}
		}
	}
	
	
	public void sendMessageOrSaveMessage(String username, Message message) throws UserNotFoundException {
		// 用户可能不存在
		if(fwOrgModuleService.getFwPresenceService().isOffline(username)) {
			// System.out.println(user.getUsername() + "离线");
			// 离线，将消息进行保存
			System.out.println("给 " + username + " 保存了消息");
			saveMessag(username, message);
		} else {
			// 临时会话不支持消息的存储
			 System.out.println("给 " + username + " 发送了消息");
			// 在线，将消息进行router，不考虑有多个资源的情况，那个MessageRouter应该会帮我们做。
			sendPacketToUsername(username, message);
		}
	}
	
	private void saveMessag(String username, Message message) {
		Message newMessage = null;
		newMessage = message.createCopy();
		String toAddr = FWStringUtils.getPureJidFromNode(username);
		newMessage.setTo(toAddr);
		System.out.println("保存的消息是：" + newMessage.toXML());
//		fwOfflineMessageDao.addMessage(newMessage);
		fwOrgModuleService.getFWOrgnizationDao().addMessage(newMessage);
	} 
	
	private void sendPacketToUsername(String username, Message message) {
		Message newMessage = null;
		newMessage = message.createCopy();
		// 这里没有给出资源部分，导致实际发送时，之后有一个资源获得了message节
		// to部分是： lp@openfire， 然后如果有两个资源 lp@openfire/res1, lp@openfire/res2
		// 那么其中一个无法获取消息。。如何去获取resouce部分？
		String toAddr = FWStringUtils.getPureJidFromNode(username);
		newMessage.setTo(toAddr);
		System.out.println("发送的消息是：" + newMessage.toXML());
		messageRouter.route(newMessage);
	}
	

	
}
