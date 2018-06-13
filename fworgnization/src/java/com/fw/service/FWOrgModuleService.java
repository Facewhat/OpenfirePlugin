package com.fw.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.jivesoftware.openfire.PacketRouter;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.disco.DiscoInfoProvider;
import org.jivesoftware.openfire.disco.DiscoItem;
import org.jivesoftware.openfire.disco.DiscoItemsProvider;
import org.jivesoftware.openfire.disco.DiscoServerItem;
import org.jivesoftware.openfire.disco.ServerItemsProvider;
import org.jivesoftware.openfire.handler.IQHandler;
import org.jivesoftware.openfire.user.PresenceEventDispatcher;
import org.xmpp.component.Component;
import org.xmpp.component.ComponentException;
import org.xmpp.component.ComponentManager;
import org.xmpp.forms.DataForm;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Message.Type;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketError.Condition;
import org.xmpp.packet.Presence;

import com.fw.handler.FWIQOrgnizationQueryHandler;
import com.fw.orgnization.dao.FWOrgnizationDao;
import com.fw.orgnization.dao.FWOrgnizationDaoImpl;
import com.fw.orgnization.entity.FWGroup;
import com.fw.orgnization.entity.FWGroupUser;

import com.fw.util.FWStringUtils;

public class FWOrgModuleService implements Component, DiscoInfoProvider,
		DiscoItemsProvider, ServerItemsProvider {
	
	private final String orgServiceName;
	private String orgDescription = null;
	private boolean isHidden = true;
	
	private XMPPServer xmppServer = null;
	private PacketRouter packetRouter = null;
	private static FWOrgModuleService instance = null;

	// <订阅者jid, 被订阅的jid>
	private Map<String, Set<String>> subscribers;
	// <被订阅的jid，订阅者jid>
	private Map<String, Set<String>> subscribees;
	
	// 所有的部门
	private List<FWGroup> fwGroups = new ArrayList<FWGroup>();
	// 每个部门中的人  <部门名称（node部分）， 部门中的人>
	private Map<String, List<FWGroupUser>> fwGroupUsers = new HashMap<String, List<FWGroupUser>>();
	// 企业通讯录下所有人
	private List<FWGroupUser> allFWGroupUsers = new ArrayList<FWGroupUser>();
	
	public FWOrgnizationDao fwOrgnizationDao = null;
	
	// 处理器  handler 
	FWIQOrgnizationQueryHandler fwiqOrgnizationQueryHandler = null; // 组织通讯录查询
	
	// 未知 作用 属性
	FWMessageService fwMessageService = null; // 消息处理
	FWPresenceService fwPresenceService = null; // 出席处理

	List<IQHandler> iqHandlers = new ArrayList<IQHandler>();
	
	public static FWOrgModuleService getInstance() {
		
		return instance;
		
	}
	
	public FWOrgModuleService(String subdomain, String description,
			Boolean isHidden) {

		instance = this;
		this.orgServiceName = subdomain;
		this.orgDescription = description;
		this.isHidden = isHidden;

		subscribers = new ConcurrentHashMap<String, Set<String>>();
		subscribees = new ConcurrentHashMap<String, Set<String>>();
		//new JID(null, subdomain + "."+ getXmppServerDomain(),null);
	}
	
	@Override
	public void initialize(JID arg0, ComponentManager arg1)
			throws ComponentException {
		xmppServer = XMPPServer.getInstance();
		packetRouter = xmppServer.getPacketRouter();
		
		fwMessageService = new FWMessageService(this);
		fwPresenceService = new FWPresenceService(this);

		fwiqOrgnizationQueryHandler = new FWIQOrgnizationQueryHandler();

		// 所有的iqhandler置于其中
		iqHandlers.add(fwiqOrgnizationQueryHandler);

		fwOrgnizationDao = new FWOrgnizationDaoImpl();
		
		try {
			fwGroups = fwOrgnizationDao.getAllDepartment();
			for (FWGroup group : fwGroups) {
				fwGroupUsers.put(group.getGroupname(), fwOrgnizationDao
						.getDepartmentUser(group.getGroupname()));
			}
			allFWGroupUsers = fwOrgnizationDao.getAllGroupUser();
		} catch (Exception e) {
			System.out.println("获取群组数据失败！");
		}

	}

	@Override
	public void processPacket(Packet packet) {

		try {
			if (packet instanceof IQ) 
			{
				System.out.println("IQ: --------------");
				System.out.println(packet.toString());
				if (process((IQ) packet))
				{
					return;
				}
			} 
			else if (packet instanceof Message)
			{
				System.out.println("Message: --------------");
				System.out.println(packet.toString());
				
				if (((Message) packet).getType() == Type.error)
				{
					return;
				}
				
				process((Message) packet);
			} 
			else if (packet instanceof Presence)
			{
				System.out.println("Presence: --------------");
				System.out.println(packet.toString());
				
				process((Presence) packet);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean process(IQ iq) throws Exception {
		
		Element childElement = iq.getChildElement();
		String namespace = null;
		
		if (IQ.Type.error == iq.getType()) {
			return false;
		}
		
		if (childElement != null) {
			namespace = childElement.getNamespaceURI();
		} 
		else 
		{
	        IQ reply = new IQ(IQ.Type.error, iq.getID());
	        reply.setFrom(iq.getTo());
	        reply.setTo(iq.getFrom());
	        reply.setError(Condition.bad_request);
	        packetRouter.route(reply);
		}
		
		if ("http://jabber.org/protocol/disco#info".equals(namespace)) {

			IQ reply = IQ.createResultIQ(iq);
			final Element queryElement = reply.setChildElement("query","http://jabber.org/protocol/disco#info");
			
			for (IQHandler handler : iqHandlers) 
			{
				final Element featureElement = queryElement.addElement("feature");
				
				featureElement.addAttribute("var", handler.getInfo().getNamespace());
			}
			
			System.out.println(reply.toXML());
			
			packetRouter.route(reply);
		}

		else {
			for (IQHandler handler : iqHandlers) 
			{
				if (namespace.equals(handler.getInfo().getNamespace()))
				{
					IQ reply = fwiqOrgnizationQueryHandler.handleIQ(iq);
					
					if (null != reply) 
					{
						System.out.println(reply.toString());
						packetRouter.route(reply);
					}
				}
			}
		}

		return true;
	}
	
	private void process(Presence presence) throws Exception {

		fwPresenceService.process(presence);
		
	}

	private void process(Message message) throws Exception {

		JID toJid = message.getTo();
		JID fromJid = message.getFrom();
		// 情况
		// 1个人发给个人
		// 2个人发给群组
		// 3个人发给整个组织。

		String toRes = toJid.getResource();
		String toNode = toJid.getNode();
		Message.Type type = message.getType();
		if (null == type) {
			fwMessageService.sendMessageError(message, Condition.bad_request);
		}

		if (!FWStringUtils.isStringNullOrEmpty(toRes) && type == Type.chat&& !FWStringUtils.isStringNullOrEmpty(toNode))
		{
			// 如果不为空就是发送给个人的，如果这个人不存在，就不行了。
			FWGroup group = getFWGroupByGroupname(toNode);
			// 存在这个组，并且组里面存在这个用户。
			if (null != group&& null != getFWGroupUserByUsername(fwGroupUsers.get(group.getGroupname()), toRes)) 
			{
				fwMessageService.sendMessageToUser(message);
			} 
			else 
			{
				fwMessageService.sendMessageError(message,
						Condition.bad_request);
			}
		} 
		else if (!FWStringUtils.isStringNullOrEmpty(toNode)&& type == Type.groupchat) 
		{
			FWGroup group = getFWGroupByGroupname(toNode);
			if (null != group)
			{
				if (group.getGroupfathername().equals("0")) 
				{
					// 父节点为0的是顶级。即公司
					fwMessageService.sendMessageToOrg(message, allFWGroupUsers);
				} 
				else
				{
					// 发给一个组
					fwMessageService.sendMessageToGroup(message,
							fwGroupUsers.get(group.getGroupname()));
				}
			}
			else 
			{
				// 没找到group
				fwMessageService.sendMessageError(message,
						Condition.internal_server_error);
			}
		}
		else 
		{
			// 其他情况
			System.out.println("发送的消息有误！");
			fwMessageService.sendMessageError(message, Condition.bad_request);
		}
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		System.out.println("FWOrganizationService start");
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		System.out.println("FWOrganizationService start");
	}

	public String getOrgServiceName() {
		return orgServiceName;
	}

	public String getOrgnizationDescription() {
		return orgDescription;
	}

	public boolean isHidden() {
		return isHidden;
	}

	@Override
	public Iterator<DiscoServerItem> getItems() {
		final ArrayList<DiscoServerItem> items = new ArrayList<>();
		final DiscoServerItem item = new DiscoServerItem(new JID(
				getOrgServiceName()), getOrgnizationDescription(),
				null, null, this, this);
		items.add(item);
		return items.iterator();
	}

	@Override
	public String getDescription() {
		return orgDescription;
	}

	@Override
	public Iterator<DiscoItem> getItems(String name, String node, JID senderJID) {
		System.out.println("name:" + name + " node:" + node);
		
		return null;
	}

	@Override
	public Iterator<Element> getIdentities(String name, String node,
			JID senderJID) {
		ArrayList<Element> identities = new ArrayList<>();
		Element identity = DocumentHelper.createElement("identity");
		identity.addAttribute("category", "fworgnization");
		identity.addAttribute("name", getDescription());
		identity.addAttribute("type", "text");
		identities.add(identity);

		return identities.iterator();

	}

	@Override
	public Iterator<String> getFeatures(String name, String node, JID senderJID) {
		ArrayList<String> features = new ArrayList<>();
		features.add("http://jabber.org/protocol/muc");
		features.add("http://jabber.org/protocol/disco#info");
		features.add("http://jabber.org/protocol/disco#items");
		features.add("jabber:iq:search");
		
		return features.iterator();
	}

	@Override
	public DataForm getExtendedInfo(String name, String node, JID senderJID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasInfo(String name, String node, JID senderJID) {
		// TODO Auto-generated method stub
		return false;
	}

	public List<FWGroup> getFwGroups() {
		return fwGroups;
	}

	public Map<String, List<FWGroupUser>> getFwGroupUsers() {
		return fwGroupUsers;
	}

	public List<FWGroupUser> getAllFWGroupUsers() {
		return allFWGroupUsers;
	}

	public FWMessageService getFwMessageService() {
		return fwMessageService;
	}

	public FWPresenceService getFwPresenceService() {
		return fwPresenceService;
	}

	//从分组中得到指定Username的组员
	public FWGroupUser getFWGroupUserByUsername(List<FWGroupUser> fwGroupUsers,
			String username) {
		if (FWStringUtils.isStringNullOrEmpty(username)) {
			return null;
		}
		for (FWGroupUser user : fwGroupUsers) {
			if (user.getUsername().equals(username.trim())) {
				return user;
			}
		}
		return null;
	}
	
	public FWGroup getFWGroupByGroupjid(String groupJid) {
		String groupname = groupJid.split("@")[0];
		return getFWGroupByGroupname(groupname);
	}

	
	public FWGroup getFWGroupByGroupname(String groupname) {
		if (FWStringUtils.isStringNullOrEmpty(groupname)) {
			return null;
		}
		for (FWGroup group : fwGroups) {
			if (group.getGroupname().equals(groupname.trim())) {
				return group;
			}
		}
		return null;
	}


	public Map<String, Set<String>> getSubscribers() {
		return subscribers;
	}
	
	
	
	/**
	 * 如果不存在该组，则返回false
	 * @param subscriberJid 订阅者纯，
	 * @param groupJid 组jid
	 * @return
	 */
	public boolean addGroupSubscribe(String subscriberJid, String groupJid) {
		// getFWGr
//		FWGroup group = getFWGroupByGroupjid(groupJid);
//		if(null == group) {
//			return false;
//		}
		String groupname = groupJid.split("@")[0];
		if(!fwGroupUsers.containsKey(groupname)) {
			return false;
		}
		for(FWGroupUser user : fwGroupUsers.get(groupname)) {
			System.out.println("订阅" + user.getUsername());
			addGroupUserSubscribe(subscriberJid, FWStringUtils.getPureJidFromNode(user.getUsername()));
		}
		// for(FWGroupUser user : group.get)
		
		return true;
	}
	
	/**
	 * 取消某人的所有订阅，下线的时候主动调用。
	 * @param cancelSubscribeJid 取消订阅者的纯jid
	 */
	public void cancelSubcribe(String cancelSubscribeJid) {
		// 如果 subscribers的中key不包含，那么可以断定subscribees中的value也不包含
		if(subscribers.containsKey(cancelSubscribeJid)) {
			for(String mySubscribe : subscribers.get(cancelSubscribeJid)) {
				if(subscribees.containsKey(mySubscribe)) {
					subscribees.get(mySubscribe).remove(cancelSubscribeJid);
				}
			}
			subscribers.remove(cancelSubscribeJid);
		}
	}
	/**
	 * 
	 * @param subscriberJid 订阅者的纯jid
	 * @param subscribeeJid 被订阅者的纯Jid
	 * @return
	 */
	public void addGroupUserSubscribe(String subscriberJid, String subscribeeJid) {
		// 订阅者和被订阅者都要在 allFWGroupUsers中（双向订阅），暂时不判断这个，默认所有人都会是在组织通讯录中。
		if(subscribers.containsKey(subscriberJid)) {
			subscribers.get(subscriberJid).add(subscribeeJid);
		} else {
			
			Set<String> bees = new HashSet<String>();
			bees.add(subscribeeJid);
			subscribers.put(subscriberJid, bees);
		}
		if(subscribees.containsKey(subscribeeJid)) {
			
			subscribees.get(subscribeeJid).add(subscriberJid);
		} else {
			Set<String> bers = new HashSet<String>();
			bers.add(subscriberJid);
			subscribees.put(subscribeeJid, bers);
		}
		// 每次订阅后，就马上从被订阅者发送出席节给订阅者，即发送被订阅者状态
		fwPresenceService.sendPresenceTo(subscribeeJid, subscriberJid);

	}
	

	public void setSubscribers(Map<String, Set<String>> subscribers) {
		this.subscribers = subscribers;
	}

	public Map<String, Set<String>> getSubscribees() {
		return subscribees;
	}

	public void setSubscribees(Map<String, Set<String>> subscribees) {
		this.subscribees = subscribees;
	}
	

	public FWOrgnizationDao getFWOrgnizationDao() {
		return fwOrgnizationDao;
	}

	@Override
	public String getName() {
		// TODO 自动生成的方法存根
		return orgServiceName;
	}
	
	public String getXmppServerDomain() {
		return XMPPServer.getInstance().getServerInfo().getXMPPDomain();
	}

	public String getOrgServerDomain() {
		return getOrgServiceName() + "." + getXmppServerDomain();
	}
	
}
