package com.facewhat.archive.model;

import org.jivesoftware.database.JiveID;

import java.util.*;

/**
 * A conversation between two or more participants.
 */
@JiveID(502)
public class Conversation
{
    private Long id;
    private Date start;
    private Date end;
    private String ownerJid;
    private String ownerResource;
    private String withJid;
    private String withResource;
    private String subject;
    private String thread;
    private List<Participant> participants;
    private List<ArchivedMessage> messages;

    public Conversation() {
    	
    }
    public Conversation(Date start, String ownerJid, String ownerResource, String withJid, String withResource,
                        String subject, String thread)
    {
        this(start, start, ownerJid, ownerResource, withJid, withResource, subject, thread);
    }
    
    public Conversation(Date start, Date end, String ownerJid, String ownerResource, String withJid, String withResource,
                        String subject, String thread)
    {
        this.start = start;
        this.end = end;
        this.ownerJid = ownerJid;
        this.ownerResource = ownerResource;
        this.withJid = withJid;
        this.withResource = withResource;
        this.subject = subject;
        this.thread = thread;
        participants = new ArrayList<Participant>();
        messages = new ArrayList<ArchivedMessage>();
    }
    
    public void setStart(Date start) {
		this.start = start;
	}
	public void setOwnerJid(String ownerJid) {
		this.ownerJid = ownerJid;
	}
	public void setOwnerResource(String ownerResource) {
		this.ownerResource = ownerResource;
	}
	public void setWithJid(String withJid) {
		this.withJid = withJid;
	}
	public void setWithResource(String withResource) {
		this.withResource = withResource;
	}
	public void setThread(String thread) {
		this.thread = thread;
	}
	public void setParticipants(List<Participant> participants) {
		this.participants = participants;
	}
	public void setMessages(List<ArchivedMessage> messages) {
		this.messages = messages;
	}
	public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Date getStart()
    {
        return start;
    }

    public Date getEnd()
    {
        return end;
    }

    public void setEnd(Date end)
    {
        this.end = end;
    }

    public String getOwnerJid()
    {
        return ownerJid;
    }

    public String getOwnerResource()
    {
        return ownerResource;
    }

    public String getWithJid()
    {
        return withJid;
    }

    public String getWithResource()
    {
        return withResource;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getThread()
    {
        return thread;
    }

    public Collection<Participant> getParticipants()
    {
        return Collections.unmodifiableCollection(participants);
    }

    public void addParticipant(Participant participant)
    {
        synchronized (participants)
        {
            participants.add(participant);
        }
    }

    public List<ArchivedMessage> getMessages()
    {
        return Collections.unmodifiableList(messages);
    }

    public void addMessage(ArchivedMessage message)
    {
        synchronized (messages)
        {
            messages.add(message);
        }
    }

    public boolean isStale(int conversationTimeout)
    {
        Long now = System.currentTimeMillis();

        return end.getTime() + conversationTimeout * 60L * 1000L < now;
    }

    /**
     * Checks if this conversation has an active participant with the given JID.
     *
     * @param jid JID of the participant
     * @return <code>true</code> if this conversation has an active participant with the given JID,
     *         <code>false</code> otherwise.
     */
    public boolean hasParticipant(String jid)
    {
        synchronized (participants)
        {
            for (Participant p : participants)
            {
                if (p.getJid().equals(jid))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if this conversation is new and has not yet been persisted.
     *
     * @return <code>true</code> if this conversation is new and has not yet been persisted,
     *         <code>false</code> otherwise.
     */
    public boolean isNew()
    {
        return id == null;
    }
}
