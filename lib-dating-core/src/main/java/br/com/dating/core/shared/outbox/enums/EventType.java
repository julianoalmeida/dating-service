package br.com.dating.core.shared.outbox.enums;

import dating.event.UserChatEvent;

import dating.event.UserEvent;

import org.apache.avro.generic.GenericRecord;

public enum EventType {

    USER_CREATED(UserEvent.class),
    USER_CHAT_MESSAGE_CREATED(UserChatEvent.class);

    private final Class<? extends GenericRecord> avroClass;

    EventType(Class<? extends GenericRecord> avroClass){
        this.avroClass = avroClass;
    }

    public Class<? extends GenericRecord> getAvroClass(){
        return this.avroClass;
    }
}
