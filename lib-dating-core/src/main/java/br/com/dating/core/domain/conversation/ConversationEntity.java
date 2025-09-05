package br.com.dating.core.domain.conversation;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

import static br.com.dating.core.domain.conversation.ConversationEntity.ENTITY_NAME;
import static br.com.dating.core.domain.conversation.ConversationEntity.TABLE_NAME;

@Entity(name = ENTITY_NAME)
@Table(name = TABLE_NAME)
public class ConversationEntity {

    protected static final String ENTITY_NAME = "ConversationEntity";
    protected static final String TABLE_NAME = "conversations";
    private static final String SEQUENCE_NAME = "sq_conversation_id";

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

    @Column(name = "user1_id", nullable = false)
    private Long user1Id;

    @Column(name = "user2_id", nullable = false)
    private Long user2Id;

    @Column
    private String lastMessage;

    @Column
    private LocalDateTime lastMessageAt;

    @Column(nullable = false)
    private Integer unreadCount1 = 0;

    @Column(nullable = false)
    private Integer unreadCount2 = 0;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public ConversationEntity() {}

    public ConversationEntity(Long user1Id, Long user2Id) {
        this.user1Id = user1Id;
        this.user2Id = user2Id;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUser1Id() { return user1Id; }
    public void setUser1Id(Long user1Id) { this.user1Id = user1Id; }

    public Long getUser2Id() { return user2Id; }
    public void setUser2Id(Long user2Id) { this.user2Id = user2Id; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public LocalDateTime getLastMessageAt() { return lastMessageAt; }
    public void setLastMessageAt(LocalDateTime lastMessageAt) { this.lastMessageAt = lastMessageAt; }

    public Integer getUnreadCount1() { return unreadCount1; }
    public void setUnreadCount1(Integer unreadCount1) { this.unreadCount1 = unreadCount1; }

    public Integer getUnreadCount2() { return unreadCount2; }
    public void setUnreadCount2(Integer unreadCount2) { this.unreadCount2 = unreadCount2; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ConversationEntity that = (ConversationEntity) o;
        return Objects.equal(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("user1Id", user1Id)
                .add("user2Id", user2Id)
                .add("lastMessage", lastMessage)
                .add("lastMessageAt", lastMessageAt)
                .add("unreadCount1", unreadCount1)
                .add("unreadCount2", unreadCount2)
                .add("createdAt", createdAt)
                .add("updatedAt", updatedAt)
                .toString();
    }
}
