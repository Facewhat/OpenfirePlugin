package com.facewhat.archive.manager;

import org.jivesoftware.openfire.session.Session;
import org.xmpp.packet.Message;

import com.facewhat.archive.model.ArchivedMessage;

/**
 * Adds messages to the archive.
 */

// 接口 消息管理 器  

// 添加消息到档案

public interface ArchiveManager
{
    /**
     * Adds a message to the archive.
     *
     * @param session  the session the message was received through.
     * @param message  the message to archive.
     * @param incoming <code>true</code> if this a message received by the server, <code>false</code> if it
     *                 is sent by the server.
     */
	public void archiveMessage(Session session, Message message, boolean incoming);

    /**
     * Sets the conversation timeout.<p>
     * A new conversation is created if there no messages have been exchanged between two JIDs
     * for the given timeout.
     *
     * @param conversationTimeout the conversation timeout to set in minutes.
     */
	public void setConversationTimeout(int conversationTimeout);
    
	public ArchivedMessage createArchivedMessage(Session session, Message message, ArchivedMessage.Direction direction);
}
