package br.com.dating.consumer.kafka.observability;

import io.opentelemetry.api.trace.Span;

public interface KafkaTracing {

    /**
     * Adiciona parametros customizados para o tracing distribuido do kafka no Open Telemetry
     *
     * @param consumerName Nome do consumidor
     * @param partition    Chave de partição
     * @param offset       Offset do evento/comando
     * @param messageClass Nome da classe utilizada pelo schema avro
     * @param eventId      id do evento
     */
    static void addCustomParameters(final String consumerName,
                                    final String partition,
                                    final String offset,
                                    final String messageClass,
                                    final String eventId) {

        Span currentSpan = Span.current();
        currentSpan.setAttribute("consumerName", consumerName);
        currentSpan.setAttribute("partition", partition);
        currentSpan.setAttribute("offset", offset);
        currentSpan.setAttribute("messageClass", messageClass);
        currentSpan.setAttribute("eventId", eventId);
    }

    /**
     * Adiciona erro no tracing distribuido do kafka para o Open Telemetry
     *
     * @param throwable Erro
     */
    static void noticeError(Throwable throwable) {
        Span.current().recordException(throwable);
    }
}
