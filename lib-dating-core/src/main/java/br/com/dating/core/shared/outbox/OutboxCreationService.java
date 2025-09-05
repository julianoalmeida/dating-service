package br.com.dating.core.shared.outbox;

import br.com.dating.core.shared.outbox.entity.OutboxEntity;
import br.com.dating.core.shared.outbox.enums.EventType;
import br.com.dating.core.shared.outbox.repository.OutboxRepository;

import org.apache.avro.generic.GenericRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OutboxCreationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutboxCreationService.class);

    private final OutboxRepository outboxRepository;

    public OutboxCreationService(OutboxRepository outboxRepository) {
        this.outboxRepository = outboxRepository;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void execute(String topic, String key, UUID eventId, EventType eventType, GenericRecord genericRecord) {

        LOGGER.info("stage=init, topic={}, key={}, eventId={}, eventType={}", topic, key, eventId, eventType);

        if (!eventType.getAvroClass().equals(genericRecord.getClass())) {
            throw new IllegalArgumentException("Incompatible record type");
        }

        var entity = new OutboxEntity(eventId, topic, key, eventType, genericRecord);

        outboxRepository.save(entity);

        LOGGER.info("stage=completed, topic={}, key={}, eventId={}, eventType={}", topic, key, eventId, eventType);
    }
}
