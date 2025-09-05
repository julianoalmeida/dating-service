package br.com.dating.core.shared.outbox.converter;

import br.com.dating.core.shared.outbox.enums.EventType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.avro.generic.GenericRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericRecordConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericRecordConverter.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static GenericRecord convertToEntityAttribute(String message, EventType eventType) {
        try {
            return message == null ? null : OBJECT_MAPPER.readValue(message, eventType.getAvroClass());
        } catch (JsonProcessingException e) {
            LOGGER.error("stage=error-to-parse-message, message={}", message, e);
            throw new RuntimeException(e);
        }
    }
}
