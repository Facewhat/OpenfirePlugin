package com.facewhat.archive.xep0136;


import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.handler.IQHandler;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketError;

import com.facewhat.archive.ArchivePlugin;
import com.facewhat.archive.manager.IndexManager;
import com.facewhat.archive.manager.PersistenceManager;

/**
 * Abstract base class for XEP-0136 IQ Handlers.
 */
public abstract class AbstractIQHandler extends IQHandler
{
    protected static final String NAMESPACE = "urn:xmpp:archive";
    private final IQHandlerInfo info;

    protected AbstractIQHandler(String moduleName, String elementName)
    {
        super(moduleName);
        this.info = new IQHandlerInfo(elementName, NAMESPACE);
    }

    public final IQHandlerInfo getInfo()
    {
        return info;
    }

    protected PersistenceManager getPersistenceManager()
    {
        return ArchivePlugin.getInstance().getPersistenceManager();
    }

    protected IndexManager getIndexManager()
    {
        return ArchivePlugin.getInstance().getIndexManager();
    }

    protected IQ error(Packet packet, PacketError.Condition condition)
    {
        IQ reply;

        reply = new IQ(IQ.Type.error, packet.getID());
        reply.setFrom(packet.getTo());
        reply.setTo(packet.getFrom());
        reply.setError(condition);
        return reply;
    }
}
