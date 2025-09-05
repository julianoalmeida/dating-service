package br.com.dating.core.shared.outbox;

import java.util.List;

import br.com.dating.core.shared.outbox.model.Outbox;
import br.com.dating.core.shared.outbox.repository.OutboxRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OutboxFinderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutboxFinderService.class);

    private final OutboxRepository outboxRepository;

    public OutboxFinderService(OutboxRepository outboxRepository) {
        this.outboxRepository = outboxRepository;
    }

    @Transactional(readOnly = true)
    public List<Outbox> findAllPending(int maxResults) {
        return outboxRepository.findAllPending(PageRequest.of(0, maxResults)).stream()
            .map(o -> Outbox.builder()
                .id(o.getId())
                .eventId(o.getEventId())
                .key(o.getEventKey())
                .status(o.getEventStatus())
                .type(o.getEventType())
                .message(o.getGenericRecord())
                .createdAt(o.getCreatedAt())
                .updatedAt(o.getUpdatedAt())
                .topic(o.getTopic())
                .build())
            .toList();
    }
}
