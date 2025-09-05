package br.com.dating.publisher.kafka;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.avro.generic.GenericRecord;
import org.springframework.kafka.support.SendResult;

/**
 * Interface for sending messages to a message broker.
 */
public interface EventPublisher<K, V> {

    CompletableFuture<SendResult<K, V>> send(String topic, K key, V data);

    void sendGenericEvent(String topic, String key, GenericRecord event);

    void sendBatchEvents(String topic, String key, List<String> events);
}
