package com.fw.ztest;


import java.util.Collection;
import java.util.List;

import org.jivesoftware.openfire.MessageRouter;
import org.jivesoftware.openfire.PresenceManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.xmpp.component.AbstractComponent;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Presence;

import com.fw.orgnization.entity.FWGroupUser;
import com.fw.service.FWOrgModuleService;
import com.fw.util.FWStringUtils;
// 此类没有用了。
public class FWMessageComponent extends AbstractComponent{

	
	private FWOrgModuleService fwOrganizationService = null;
	private MessageRouter messageRouter = null;
	
	public FWMessageComponent(FWOrgModuleService fwOrganizationService) {
		this.fwOrganizationService = fwOrganizationService;
		messageRouter = XMPPServer.getInstance().getMessageRouter();
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void send(Packet arg0) {
		// TODO Auto-generated method stub
		super.send(arg0);
	}
	
	public void sendMessageToOrg(Message message) {
		
		List<FWGroupUser> fwGroupUsers = fwOrganizationService.getAllFWGroupUsers();
		message.setFrom(message.getTo() + "/" + message.getFrom().getNode());
		for(FWGroupUser user : fwGroupUsers) {
			User u = null;
			try {
				u = XMPPServer.getInstance().getUserManager().getUser(user.getUsername());
				
			} catch (UserNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// String time = XMPPServer.getInstance().getPresenceManager().getLastPresenceStatus(u);
			
			PresenceManager presenceManager = XMPPServer.getInstance().getPresenceManager();
			if (presenceManager.isAvailable(u)) {
				Presence p = presenceManager.getPresence(u);
				if(null == p.getShow()) {
					System.out.println(user.getUsername() + "在线");
				} else {
					if(Presence.Show.chat == p.getShow()) {
						System.out.println(user.getUsername() + "chat");
					} else if(Presence.Show.away == p.getShow()) {
						System.out.println(user.getUsername() + "away");
					} else if(Presence.Show.dnd == p.getShow()) {
						System.out.println(user.getUsername() + "dnd");
					} else if(Presence.Show.xa == p.getShow()) {
						System.out.println(user.getUsername() + "xa");
					}  else if(Presence.Show.chat == p.getShow()) {
						System.out.println(user.getUsername() + "chat");
					}  
				}
			} else {
				System.out.println(user.getUsername() + "离线");
			}
		}
	}
	
	
	private void sendPacketToUsername(String username, Message message) {
		Message newMessage = null;
		newMessage = message.createCopy();
		String toAddr = FWStringUtils.getPureJidFromNode(username);
		newMessage.setTo(toAddr);
		messageRouter.route(newMessage);
	}
	private void sendPacketToJid(String jid,
			Message message) {
		Message newMessage = null;
		newMessage = message.createCopy();
			// String toAddr = FWUtils.getPureJidFromNode(username);
		send(newMessage);
	}
	private void sendPacketToUsernames(Collection<String> usernames,
			Message message) {
		Message newMessage = null;
		for (String username : usernames) {
			newMessage = message.createCopy();
			String toAddr = FWStringUtils.getPureJidFromNode(username);
			newMessage.setTo(toAddr);

			send(newMessage);
		}
	}
	/**
	 * 发送消息给JID集合
	 * 
	 * @param userJID
	 *            所有jid，即要带上resource
	 * @param message
	 *            消息体
	 */
	private void sendPacketToJIDs(Collection<JID> userJID, Message message) {
		Message newMessage = null;
		for (JID jid : userJID) {
			newMessage = message.createCopy();
			newMessage.setTo(jid);

			send(newMessage);
		}
	}
	
	
	
	

}
