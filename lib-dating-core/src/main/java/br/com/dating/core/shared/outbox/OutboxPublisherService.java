package br.com.dating.core.shared.outbox;

import br.com.dating.publisher.kafka.EventPublisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OutboxPublisherService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutboxPublisherService.class);

    private final int maxResults;
    private final OutboxFinderService outboxFinderService;
    private final OutboxUpdaterService outboxUpdaterService;
    private final EventPublisher eventPublisher;

    public OutboxPublisherService(@Value("${outboxPublisher.maxResults}") int maxResults,
                                  OutboxFinderService outboxFinderService,
                                  OutboxUpdaterService outboxUpdaterService,
                                  EventPublisher eventPublisher) {
        this.maxResults = maxResults;
        this.outboxFinderService = outboxFinderService;
        this.outboxUpdaterService = outboxUpdaterService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(propagation = Propagation.NEVER)
    public void execute() {

        LOGGER.info("stage=init");

        var pendingEvents = outboxFinderService.findAllPending(maxResults);

        LOGGER.info("stage=send-events, totalEvents={}", pendingEvents.size());

        for (var event : pendingEvents) {
            eventPublisher.sendGenericEvent(event.getTopic(), event.getKey(), event.getMessage());
            outboxUpdaterService.markAsSent(event.getEventId());
        }

        LOGGER.info("stage=completed, totalEvents={}", pendingEvents.size());
    }
}
