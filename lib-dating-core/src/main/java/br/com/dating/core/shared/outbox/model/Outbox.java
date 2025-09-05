package br.com.dating.core.shared.outbox.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import br.com.dating.core.shared.outbox.enums.EventStatus;
import br.com.dating.core.shared.outbox.enums.EventType;

import org.apache.avro.generic.GenericRecord;

public class Outbox implements Comparable<Outbox> {

    private final Long id;
    private final UUID eventId;
    private final String topic;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final EventType type;
    private final EventStatus status;
    private final String key;
    private final GenericRecord message;

    private Outbox(Builder builder) {
        this.id = builder.id;
        this.eventId = builder.eventId;
        this.topic = builder.topic;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.type = builder.type;
        this.status = builder.status;
        this.key = builder.key;
        this.message = builder.message;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public UUID getEventId() {
        return eventId;
    }

    public String getTopic() {
        return topic;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public EventType getType() {
        return type;
    }

    public EventStatus getStatus() {
        return status;
    }

    public String getKey() {
        return key;
    }

    public GenericRecord getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Outbox that = (Outbox) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Outbox o) {
        return Long.compare(this.id, o.id);
    }

    public static class Builder {
        private Long id;
        private UUID eventId;
        private String topic;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private EventType type;
        private EventStatus status;
        private String key;
        private GenericRecord message;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder eventId(UUID eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder topic(String topic) {
            this.topic = topic;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder type(EventType type) {
            this.type = type;
            return this;
        }

        public Builder status(EventStatus status) {
            this.status = status;
            return this;
        }

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder message(GenericRecord message) {
            this.message = message;
            return this;
        }

        public Outbox build() {
            return new Outbox(this);
        }
    }
}
