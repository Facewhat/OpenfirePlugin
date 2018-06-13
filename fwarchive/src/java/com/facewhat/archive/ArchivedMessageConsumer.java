package com.facewhat.archive;

import com.facewhat.archive.model.ArchivedMessage;

public interface ArchivedMessageConsumer
{
    boolean consume(ArchivedMessage message);
}
