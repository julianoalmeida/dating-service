package br.com.dating.consumer.kafka.events;

import br.com.dating.consumer.kafka.config.KafkaConsumerConfig;

import dating.event.UserChatEvent;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;


@Component
public class UserChatEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserChatEventListener.class);

    @KafkaListener(
        groupId = "${spring.application.name}[${spring.profiles.active}][userChatEvent]",
        topics = {"${kafka.consumer.topic.user-message}"},
        containerFactory = KafkaConsumerConfig.USER_CHAT_EVENT_CONTAINER_FACTORY,
        autoStartup = "${kafka.consumer.listener.userChatEventListener.enabled:true}"
    )
    public void onEvent(ConsumerRecord<String, UserChatEvent> eventRecord, Acknowledgment ack) {
        try {

            logInfo("init", eventRecord);

            // UserChatService.send();

            logInfo("completed", eventRecord);

            ack.acknowledge();

        } catch (Exception exception) {
            LOGGER.error("stage=failure", exception);
            throw exception;
        }
    }

    private static void logInfo(String stage, ConsumerRecord<String, UserChatEvent> eventRecord) {
        LOGGER.info("stage={},"
                + " eventId={},"
                + " eventKey={},"
                + " partitionKey={},"
                + " sender={},"
                + " receiver={},"
                + " message={},"
                + " createdAt={},"
                + " eventDate={},"
                + " partition={},"
                + " offset={}",
            stage,
            eventRecord.value().getMetadata().getEventId(),
            eventRecord.key(),
            eventRecord.value().getMetadata().getPartitionKey(),
            eventRecord.value().getSender(),
            eventRecord.value().getReceiver(),
            eventRecord.value().getMessage(),
            eventRecord.value().getCreatedAt(),
            eventRecord.value().getMetadata().getEventDate(),
            eventRecord.partition(),
            eventRecord.offset()
        );
    }
}
