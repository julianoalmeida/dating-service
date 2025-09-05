package br.com.dating.core.shared.outbox.entity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import br.com.dating.core.shared.outbox.converter.GenericRecordConverter;
import br.com.dating.core.shared.outbox.enums.EventStatus;
import br.com.dating.core.shared.outbox.enums.EventType;

import org.apache.avro.generic.GenericRecord;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PostLoad;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity(name = OutboxEntity.ENTITY_NAME)
@Table(name = OutboxEntity.TABLE_NAME,
    indexes = {
        @Index(name = "outbox_idx01", columnList = "topic"),
        @Index(name = "outbox_idx02", columnList = "event_key"),
        @Index(name = "outbox_idx03", columnList = "event_type"),
        @Index(name = "outbox_idx06", columnList = "event_status"),
        @Index(name = "outbox_idx07", columnList = "event_status, id"),
        @Index(name = "outbox_uk02", columnList = "event_id", unique = true)
    }
)
public class OutboxEntity {

    protected static final String ENTITY_NAME = "OutboxEntity";
    protected static final String TABLE_NAME = "outbox";
    private static final String SEQUENCE_NAME = "sq_outbox_id";

    @Id
    @Column(nullable = false, length = 19)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = OutboxEntity.SEQUENCE_NAME)
    @SequenceGenerator(
        name = OutboxEntity.SEQUENCE_NAME,
        sequenceName = OutboxEntity.SEQUENCE_NAME,
        allocationSize = 1 // Nao aumentar, pois o id é usado para garantir ordem
    )
    private Long id;

    /**
     * Identificador público do evento.
     */
    @Column(nullable = false, length = 16, unique = true)
    private UUID eventId;

    /**
     * Tópico
     */
    @Column(nullable = false, updatable = false)
    private String topic;

    /**
     * Chave única do evento
     */
    @Column(nullable = false, updatable = false)
    private String eventKey;

    /**
     * Tipo do evento
     */
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private EventType eventType;

    /**
     * Status de envio do evento.
     */
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EventStatus eventStatus;

    /**
     * Payload do evento
     */
    @Column(columnDefinition = "clob", nullable = false, updatable = false)
    private String eventPayload;

    @Transient
    private GenericRecord genericRecord;

    /**
     * Data de criação.
     */
    @Column(
        nullable = false,
        insertable = false,
        updatable = false,
        columnDefinition = "timestamp(6) default systimestamp")
    private LocalDateTime createdAt;

    /**
     * Data da última atualização.
     */
    @Column(
        nullable = false,
        insertable = false,
        columnDefinition = "timestamp(6) default systimestamp")
    private LocalDateTime updatedAt;

    @PostLoad
    private void initializeGenericRecord() {
        this.genericRecord = GenericRecordConverter.convertToEntityAttribute(eventPayload, this.eventType);
    }

    public OutboxEntity() {
    }

    public OutboxEntity(UUID eventId,
                        String topic,
                        String eventKey,
                        EventType eventType,
                        GenericRecord eventPayload) {
        this.eventId = eventId;
        this.topic = topic;
        this.eventKey = eventKey;
        this.eventType = eventType;
        this.eventStatus = EventStatus.PENDING;
        this.eventPayload = eventPayload.toString();
        this.genericRecord = eventPayload;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public EventStatus getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(EventStatus eventStatus) {
        this.eventStatus = eventStatus;
    }

    public String getEventPayload() {
        return eventPayload;
    }

    public void setEventPayload(String eventPayload) {
        this.eventPayload = eventPayload;
    }

    public GenericRecord getGenericRecord() {
        return genericRecord;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OutboxEntity that = (OutboxEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
