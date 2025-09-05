package br.com.dating.core.shared.outbox.repository;

import br.com.dating.core.shared.outbox.entity.OutboxEntity;

import br.com.dating.core.shared.outbox.enums.EventStatus;
import br.com.dating.core.shared.outbox.enums.EventType;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEntity, Long> {

    /**
     * Como precisamos garantir a ordem de envio, devemos ter apenas 1 JOB realizando o envio das
     * mensagens, então NÃO precisamos utilizar skip_lock para consultar os envios pendentes.
     * <p>
     * OBS: o pageable utiliza o mecanismo de FETCH FIRST do Oracle que é superior ao ROWNUM em performance
     *
     * <p>Também não devemos fazer o lock, o consumer pode pegar a mensagem e marcar como lida antes
     * de ser marcada como enviada.
     */
    @Query("""
        SELECT o from OutboxEntity o
        WHERE o.eventStatus = br.com.dating.core.shared.outbox.enums.EventStatus.PENDING ORDER BY o.id ASC
        """)
    List<OutboxEntity> findAllPending(Pageable pageable);

    /**
     * Muda o status de envio do evento na tabela de outbox para SENT, apenas se o status anterior for PENDING.
     *
     * @param eventId Identificador do Evento da tabela de outbox
     * @return 1 se a atualização ocorrer com sucesso
     */
    @Modifying
    @Query("""
        UPDATE OutboxEntity o set o.updatedAt = CURRENT_TIMESTAMP,
        o.eventStatus = br.com.dating.core.shared.outbox.enums.EventStatus.SENT
        WHERE o.eventId = :eventId
        AND o.eventStatus = br.com.dating.core.shared.outbox.enums.EventStatus.PENDING
        """)
    int markAsSent(UUID eventId);

    /**
     * Muda o status de envio do evento na tabela de outbox para ACK, independente do status atual.
     *
     * @param eventId Identificador do Evento da tabela de outbox
     * @return 1 se a atualização ocorrer com sucesso
     */
    @Modifying
    @Query("""
        UPDATE OutboxEntity o set o.updatedAt = CURRENT_TIMESTAMP,
        o.eventStatus = br.com.dating.core.shared.outbox.enums.EventStatus.ACK
        WHERE o.eventId = :eventId
        """)
    int markAsAck(UUID eventId);

    Long countByEventKeyAndEventType(String eventKey, EventType eventType);

    Long countByEventKeyAndEventTypeAndEventStatus(String eventKey, EventType eventType, EventStatus eventStatus);

    boolean existsByEventStatusNot(EventStatus eventStatus);

    boolean existsByEventType(EventType eventType);
}
