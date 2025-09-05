package br.com.dating.consumer.kafka.config;

import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    //fct.user.message.event
    public static final String USER_CHAT_EVENT_CONTAINER_FACTORY = "USER_CHAT_EVENT_CONTAINER_FACTORY";

    @Value("${kafka.consumer.bootstrap.servers}")
    private String bootstrapServers;

    @Value("${kafka.consumer.schema.registry.url}")
    private String schemaRegistryURL;

    @Value("${kafka.consumer.enable.auto.commit:false}")
    private Boolean enableAutoCommit;

    @Value("${kafka.consumer.requestTimeout:15000}")
    private int requestTimeoutMs;

    @Value("${kafka.consumer.ssl.enabled:true}")
    private boolean sslEnabled;

    // O tópico tem 10 partições (vai ter concorrência de 5 threads em cada datacenter)
    @Value("${kafka.consumer.user-chat-event.concurrency:5}")
    private int userChatEventConsumerConcurrency;

    /**
     * Cria uma nova instância da {@link KafkaListenerContainerFactory} a partir da {@code
     * ConsumerFactory} já configurada.
     *
     * @return Uma nova instância da {@link KafkaListenerContainerFactory} já configurada.
     */
    @Bean(USER_CHAT_EVENT_CONTAINER_FACTORY)
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Object, Object>> userChatEventConsumer(
        KafkaProperties kafkaProperties) {
        return getContainerFactory(kafkaProperties, userChatEventConsumerConcurrency);
    }

    /**
     * Cria uma nova instância da {@link KafkaListenerContainerFactory}
     *
     * @param kafkaProperties Propriedades do Kafka
     * @param concurrency     Número de threads que irão processar as mensagens
     * @return Uma nova instância da {@link KafkaListenerContainerFactory} já configurada.
     */
    private KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Object, Object>> getContainerFactory(
        KafkaProperties kafkaProperties,
        int concurrency
    ) {
        final var factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(kafkaProperties, OffsetResetStrategy.EARLIEST, 200));
        factory.setConcurrency(concurrency);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        factory.getContainerProperties().setObservationEnabled(true);

        // Configuração de retentantiva infinita (isso trava o offset até arrumar algum problema no consumo de eventos)
        factory.setCommonErrorHandler(new DefaultErrorHandler(new FixedBackOff(TimeUnit.SECONDS.toMillis(5),
            FixedBackOff.UNLIMITED_ATTEMPTS
        )));
        return factory;
    }

    /**
     * Fornece uma nova instância de {@link ConsumerFactory} devidamente configurada com os
     * parâmetros deste projeto.
     *
     * @return Uma nova instância de {@code ConsumerFactory} para produtores Kafka já configurada.
     */
    public ConsumerFactory<Object, Object> consumerFactory(KafkaProperties kafkaProperties,
                                                           OffsetResetStrategy resetStrategy,
                                                           int maxPollRecords) {
        final var props = new HashMap<String, Object>(kafkaProperties.getProperties());

        if (sslEnabled) {
            addSSLProperties(kafkaProperties, props);
        }

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, io.confluent.kafka.serializers.KafkaAvroDeserializer.class);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, resetStrategy.name().toLowerCase());
        props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeoutMs);
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");

        // tem 5 minutos para processar X registros senão a partição é rebalanceada
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, (int) TimeUnit.MINUTES.toMillis(5));

        props.put(SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryURL);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    /**
     * Adiciona as propriedades de SSL ao mapa de propriedades do Kafka.
     *
     * @param kafkaProperties Propriedades do Kafka
     * @param props Mapa de propriedades do Kafka
     */
    private void addSSLProperties(KafkaProperties kafkaProperties, HashMap<String, Object> props) {
        if (kafkaProperties.getSsl() != null) {
            props.putAll(kafkaProperties.getSsl().buildProperties(null));
        }

        if (kafkaProperties.getSecurity().getProtocol() != null) {
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, kafkaProperties.getSecurity().getProtocol());
        }
    }
}
