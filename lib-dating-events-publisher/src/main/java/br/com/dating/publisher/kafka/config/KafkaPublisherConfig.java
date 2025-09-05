package br.com.dating.publisher.kafka.config;

import br.com.dating.publisher.factory.YamlPropertySourceFactory;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@PropertySource(value = "classpath:publisher.application.yml", factory = YamlPropertySourceFactory.class)
@PropertySource(
    ignoreResourceNotFound = true,
    value = "classpath:publisher.application-${spring.profiles.active}.yml",
    factory = YamlPropertySourceFactory.class
)
@PropertySource(
    ignoreResourceNotFound = true,
    value = "classpath:core.publisher-${spring.profiles.include}.yml",
    factory = YamlPropertySourceFactory.class
)
public class KafkaPublisherConfig {

    @Value("${kafka.producer.requestTimeout:15000}")
    private int requestTimeoutMs;

    @Value("${kafka.producer.bootstrap.servers}")
    private String bootstrapServers;

    @Value("${kafka.producer.clientId}")
    private String clientId;

    @Value("${kafka.producer.schema.registry.url}")
    private String schemaRegistryURL;

    @Value("${kafka.producer.ssl.enabled:true}")
    private boolean sslEnabled;

    @Value("${kafka.producer.lingerMs:20}")
    private String producerLingerMs;

    @Value("${kafka.producer.batchSize:32768}") // 32 KB batch size (32*1024)
    private String producerBatchSize;

    public Map<String, Object> getDefaultConfig(KafkaProperties kafkaProperties, String lingerMs, String batchSize) {
        final var props = new HashMap<String, Object>(kafkaProperties.getProperties());

        if (sslEnabled) {
            addSSLProperties(kafkaProperties, props);
        }

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        // Garante que a ordem de envio das mensagens seja respeitada
        // https://medium.com/@mbh023/kafka-ordering-message-and-deduplication-with-idempotent-producer-cdc6a3ccc8af
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, io.confluent.kafka.serializers.KafkaAvroSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeoutMs);
        props.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, requestTimeoutMs);
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 3000);
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryURL);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        return props;
    }

    private void addSSLProperties(KafkaProperties kafkaProperties, HashMap<String, Object> props) {
        if (kafkaProperties.getSsl() != null) {
            props.putAll(kafkaProperties.getSsl().buildProperties(null));
        }

        if (kafkaProperties.getSecurity().getProtocol() != null) {
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, kafkaProperties.getSecurity().getProtocol());
        }
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory(KafkaProperties kafkaProperties) {
        final Map<String, Object> config = getDefaultConfig(kafkaProperties, producerLingerMs, producerBatchSize);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
