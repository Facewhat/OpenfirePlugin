package com.fw.handler;

import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.PacketError.Condition;

import com.fw.orgnization.entity.FWGroup;
import com.fw.orgnization.entity.FWGroupUser;
import com.fw.service.FWOrgModuleService;
import com.fw.util.FWStringUtils;

public class FWIQOrgnizationQueryHandler extends FWIQHandler {
	
	protected static final String NAMESPACE = "http://facewhat.com/orgnization";
	private static String moduleName = "facewhat orgnization";
	private static String name = "orgnizationquery"; 

	public FWIQOrgnizationQueryHandler() {
		super(moduleName, name, NAMESPACE);
	}

	@Override
	public IQ handleIQ(IQ packet) throws UnauthorizedException {
		//System.out.println("handleIQ ---------");
		
		IQ reply = null;
		Element iqElement = packet.getElement();
		
		if(null != iqElement.element("queryorgnization")) {
			// 得到全部组织架构，分组以及所有人包括所有人。
			reply = handlerQueryOrgnization(packet);
		} else if(null != iqElement.element("queryenterprise")) {
			reply = handlerQueryEnterprise(packet);
		} else if(null != iqElement.element("querygroupwithoutuserandgroupbygroupfathername")) {
			reply = handlerQueryGroupWithoutUserAndGroupByGroupfathername(packet);
		} else if(null != iqElement.element("subscribegroup")) {
			//reply = handlerQueryGroupWithoutUserAndGroupByGroupfathername(packet);
			reply = handlerSubscribeGroup(packet);
		} else if(null != iqElement.element("subscribegroupuser")) {
			reply = handlerSubscribeGroupUser(packet);
		} else if(null != iqElement.element("cancelsubscribe")) {
			reply = handlerCancelSubscribe(packet);
		} else {
			reply = error(packet, Condition.feature_not_implemented);
		}
		return reply;
	}
	public IQ handlerCancelSubscribe(IQ packet) {
		//System.out.println("handlerCancelSubscribe ---------");
		IQ reply = IQ.createResultIQ(packet);
		
		FWOrgModuleService service = FWOrgModuleService.getInstance();
		
		JID fromJid = packet.getFrom();
		String pureFromJid = fromJid.getNode() + "@" + fromJid.getDomain();
		// String groupJid = packet.getChildElement().getText(); 		
		if(!FWStringUtils.isStringNullOrEmpty(pureFromJid)) {
			service.cancelSubcribe(pureFromJid);
		} else {
			reply = error(packet, Condition.not_acceptable);
		}
		return reply;
	}

	
	// FWOrgnizationQueryDao fwOrgnizationQueryDao = new FWOrgnizationQueryDaoImpl();
	public IQ handlerSubscribeGroup(IQ packet) {
		//System.out.println("handlerSubscribeGroup ---------");
		IQ reply = IQ.createResultIQ(packet);
		FWOrgModuleService service = FWOrgModuleService.getInstance();
		
		// service.
		JID fromJid = packet.getFrom();
		String pureFromJid = fromJid.getNode() + "@" + fromJid.getDomain();
		String groupJid = packet.getChildElement().getText(); 		
		
		
		if(null == service) {
			System.out.println("service不存在！");
			reply = error(packet, Condition.internal_server_error);
		} else if(FWStringUtils.isStringNullOrEmpty(groupJid) || FWStringUtils.isStringNullOrEmpty(pureFromJid)) {
			reply = error(packet, Condition.not_acceptable);
		} else {
			System.out.println(pureFromJid + " 订阅分组" + groupJid);
			if(!service.addGroupSubscribe(pureFromJid, groupJid)) {
				reply = error(packet, Condition.not_acceptable);
			}
		}
		return reply;
	}
	public IQ handlerSubscribeGroupUser(IQ packet) {
		//System.out.println("handlerSubscribeGroupUser ---------");
		IQ reply = IQ.createResultIQ(packet);
		FWOrgModuleService service = FWOrgModuleService.getInstance();
		
		// service.
		JID fromJid = packet.getFrom();
		String pureFromJid = fromJid.getNode() + "@" + fromJid.getDomain();
		String userJid = packet.getChildElement().getText(); 		
		
		if(null == service) {
			System.out.println("service不存在！");
			reply = error(packet, Condition.internal_server_error);
		} else if(FWStringUtils.isStringNullOrEmpty(userJid) || FWStringUtils.isStringNullOrEmpty(pureFromJid)) {
			reply = error(packet, Condition.not_acceptable);
		} else {
			System.out.println(pureFromJid + " 订阅" + userJid);
			service.addGroupUserSubscribe(pureFromJid, userJid);
//			
//			// 组装
//			final Element element = reply.setChildElement("querygroupwithoutuserandgroupbygroupfathername", NAMESPACE);
//			// Map<String, List<FWGroupUser>> fwGroupUsers = service.getFwGroupUsers();
//			String orgDomain = service.getOrgServerDomain();
//			String xmppDomain = service.getXmppServerDomain();
//			for(FWGroup group : service.getFwGroups()) {
//				if(fathername.equals(group.getGroupfathername())) {
//					addGroupElement(element, group, null, orgDomain, xmppDomain);
//				}
//			}
		}
		return reply;
	}
	
	// 根据父部门的id获取得到子部门，不包含子部门的用户，以及子部门的子部门。
	public IQ handlerQueryGroupWithoutUserAndGroupByGroupfathername(IQ packet) {
		//System.out.println("handlerQueryGroupWithoutUserAndGroupByGroupfathername ---------");
		IQ reply = IQ.createResultIQ(packet);
		FWOrgModuleService service = FWOrgModuleService.getInstance();
		String fathername = packet.getChildElement().getText(); 		
		
		if(null == service) {
			System.out.println("service不存在！");
			reply = error(packet, Condition.internal_server_error);
		} else if(FWStringUtils.isStringNullOrEmpty(fathername)) {
			reply = error(packet, Condition.not_acceptable);
		} else {
			// 组装
			final Element element = reply.setChildElement("querygroupwithoutuserandgroupbygroupfathername", NAMESPACE);
			// Map<String, List<FWGroupUser>> fwGroupUsers = service.getFwGroupUsers();
			String orgDomain = service.getOrgServerDomain();
			String xmppDomain = service.getXmppServerDomain();
			for(FWGroup group : service.getFwGroups()) {
				if(fathername.equals(group.getGroupfathername())) {
					addGroupElement(element, group, null, orgDomain, xmppDomain);
				}
			}
		}
		return reply;
	}
	
	public IQ handlerQueryEnterprise(IQ packet) {
		//System.out.println("handlerQueryEnterprise ---------");
		IQ reply = IQ.createResultIQ(packet);
		FWOrgModuleService service = FWOrgModuleService.getInstance();
		if(null == service) {
			System.out.println("service不存在！");
			reply = error(packet, Condition.internal_server_error);
		} else {
			// 组装
			final Element element = reply.setChildElement("queryenterprise", NAMESPACE);
			// Map<String, List<FWGroupUser>> fwGroupUsers = service.getFwGroupUsers();
			String orgDomain = service.getOrgServerDomain();
			String xmppDomain = service.getXmppServerDomain();
			for(FWGroup group : service.getFwGroups()) {
				// 父节点为0的是企业名称，企业级下不能有用户。不读取。。
				if("0".equals(group.getGroupfathername())) {
					addGroupElement(element, group, null, orgDomain, xmppDomain);
				}
			}
		}
		return reply;
	}
	
	
	
	public IQ handlerQueryOrgnization(IQ packet) {
		//System.out.println("handlerQueryOrgnization ---------");
		IQ reply = IQ.createResultIQ(packet);
		
		FWOrgModuleService service = FWOrgModuleService.getInstance();
		if(null == service) {
			System.out.println("service不存在！");
			reply = error(packet, Condition.internal_server_error);
		} else {
			// 组装
			final Element element = reply.setChildElement("queryorgnization", NAMESPACE);
			Map<String, List<FWGroupUser>> fwGroupUsers = service.getFwGroupUsers();
			String orgDomain = service.getOrgServerDomain();
			String xmppDomain = service.getXmppServerDomain();
			for(FWGroup group : service.getFwGroups()) {
				addGroupElement(element, group, fwGroupUsers.get(group.getGroupname()), orgDomain, xmppDomain);
			}
		}
		return reply;
	}
	
	
	private void addGroupElement(Element parentElement, FWGroup group, List<FWGroupUser> fwGroupUsers, String orgDomain, String xmppDomain) {
		//System.out.println("addGroupElement ---------");
		
		final Element groupElement;
		groupElement = parentElement.addElement("group");
		groupElement.addAttribute("groupjid", group.getGroupPureJid(orgDomain));
		groupElement.addAttribute("groupname", group.getGroupname());
		groupElement.addAttribute("displayname", group.getDisplayname());
		groupElement.addAttribute("groupfathername", group.getGroupfathername());
		groupElement.addAttribute("creationdate", group.getCreationdate());
		groupElement.addAttribute("isorgnization", String.valueOf(group.isIsorgnization()));
		
		// groupElement.addElement(arg0)
		if(null != fwGroupUsers) {
			for(FWGroupUser user : fwGroupUsers) {
				addGroupUserElement(groupElement, user, xmppDomain);
			}
		}
	}
	private  void addGroupUserElement(Element parentElement, FWGroupUser fwGroupUser, String xmppDomain) {
		//System.out.println("addGroupUserElement ---------");
		
		final Element userElement;
		userElement = parentElement.addElement("groupuser");
		userElement.addAttribute("userjid", fwGroupUser.getGroupUserPureJid(xmppDomain));
		userElement.addAttribute("username", fwGroupUser.getUsername());
		userElement.addAttribute("usernickname", fwGroupUser.getUsernickname());
		userElement.addAttribute("fullpinyin", fwGroupUser.getFullpinyin());
		userElement.addAttribute("shortpinyin", fwGroupUser.getShortpinyin());
	}
	
}
