package br.com.dating.core.domain.message.entity;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

import static br.com.dating.core.domain.message.entity.MessageEntity.ENTITY_NAME;
import static br.com.dating.core.domain.message.entity.MessageEntity.TABLE_NAME;

@Entity(name = ENTITY_NAME)
@Table(name = TABLE_NAME)
public class MessageEntity {

    protected static final String ENTITY_NAME = "MessageEntity";
    protected static final String TABLE_NAME = "messages";
    private static final String SEQUENCE_NAME = "sq_message_id";

    /**
     * Identificação da entidade no banco.
     */
    @Id
    @Column(nullable = false, length = 19)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(
        name = SEQUENCE_NAME,
        sequenceName = SEQUENCE_NAME, allocationSize = 1
    )
    private Long id;

    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageType messageType = MessageType.TEXT;

    @Column(nullable = false)
    private Boolean isRead = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public MessageEntity() {}

    public MessageEntity(Long conversationId, Long senderId, String content, MessageType messageType) {
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.content = content;
        this.messageType = messageType;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public MessageType getMessageType() { return messageType; }
    public void setMessageType(MessageType messageType) { this.messageType = messageType; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MessageEntity messageEntity = (MessageEntity) o;
        return Objects.equal(id, messageEntity.id) && Objects.equal(conversationId, messageEntity.conversationId) && Objects.equal(senderId, messageEntity.senderId) && messageType == messageEntity.messageType && Objects.equal(createdAt, messageEntity.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, conversationId, senderId, messageType, createdAt);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("conversationId", conversationId)
                .add("senderId", senderId)
                .add("content", content)
                .add("messageType", messageType)
                .add("isRead", isRead)
                .add("createdAt", createdAt)
                .toString();
    }

    public enum MessageType {
        TEXT, IMAGE, GIFT, SYSTEM
    }
}
