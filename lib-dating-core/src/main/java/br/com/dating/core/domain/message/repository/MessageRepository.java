package br.com.dating.core.domain.message.repository;

import br.com.dating.core.domain.message.entity.MessageEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    @Query("SELECT m FROM MessageEntity m WHERE m.conversationId = :conversationId ORDER BY m.createdAt DESC")
    Page<MessageEntity> findByConversationIdOrderByCreatedAtDesc(Long conversationId, PageRequest pageRequest);

    @Modifying
    @Transactional
    @Query("UPDATE MessageEntity m SET m.isRead = true WHERE m.conversationId = :conversationId AND m.senderId != :userId")
    void markAsReadByConversationAndReceiver(Long conversationId, Long userId);

    @Query("""
        SELECT COUNT(m) FROM MessageEntity m JOIN ConversationEntity c ON m.conversationId = c.id WHERE (c.user1Id = :userId OR c.user2Id = :userId)
        AND m.senderId != :userId AND m.isRead = false
        """)
    int countUnreadMessagesByUserId(Long userId);

    @Query("SELECT COUNT(m) FROM MessageEntity m WHERE m.senderId = :userId AND m.createdAt = CURRENT_DATE")
    int countDailyMessagesByUserId(Long userId);
}
