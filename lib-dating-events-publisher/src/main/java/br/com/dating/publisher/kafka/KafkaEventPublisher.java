package br.com.dating.publisher.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.avro.generic.GenericRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventPublisher implements EventPublisher<String, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public CompletableFuture<SendResult<String, Object>> send(String topic, String key, Object data) {
        return kafkaTemplate.send(topic, key, data);
    }

    /**
     * Publica uma instância única de {@link GenericRecord} no tópico Kafka configurado
     * e aguarda a confirmação do envio do evento com join();
     *
     * @param topic Nome do tópico
     * @param key   Chave de partição
     * @param event Evento
     */
    @Override
    public void sendGenericEvent(String topic, String key, GenericRecord event) {
        kafkaTemplate.send(topic, key, event)
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    LOGGER.info("stage=successfully-sent, event={}", event);
                } else {
                    final String message = String.format("stage=%s,failure", ex);
                    LOGGER.error(message, ex);
                }
            }).join();
    }

    /**
     * Publica uma lista da instância de {@link String} no tópico Kafka configurado.
     *
     * @param events o objeto de transação que será enviado ao tópico
     */
    @Override
    public void sendBatchEvents(String topic, String key, final List<String> events) {

        LOGGER.info("stage=init");

        List<CompletableFuture<SendResult<String, Object>>> futures = new ArrayList<>();

        for (var event : events) {
            futures.add(this.send(topic, key, event));
        }

        CompletableFuture<?> all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        all.join();

        boolean hasFailure = futures.stream().anyMatch(CompletableFuture::isCompletedExceptionally);

        if (hasFailure) {
            throw new IllegalStateException("Failed to send one or more transaction messages to Kafka topic: " + topic);
        }

        LOGGER.info("stage=event-sent-successfully, topic={}", topic);
    }
}
