package br.com.dating.core.shared.outbox;

import br.com.dating.core.shared.outbox.repository.OutboxRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
public class OutboxUpdaterService {

    private static final int SUCCESS = 1;

    private static final Logger LOGGER = LoggerFactory.getLogger(OutboxUpdaterService.class);

    private final OutboxRepository outboxRepository;

    public OutboxUpdaterService(OutboxRepository outboxRepository) {
        this.outboxRepository = outboxRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean markAsSent(UUID eventId) {
        return outboxRepository.markAsSent(eventId) == SUCCESS;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean markAsAck(UUID eventId) {
        LOGGER.info("stage=init, eventId={}", eventId);
        var updated = outboxRepository.markAsAck(eventId) == SUCCESS;
        LOGGER.info("stage=completed, eventId={}, updated={}", eventId, updated);
        return updated;
    }
}
