package br.com.dating.core.shared.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe utilitária para formatar os valores na geração de eventos.
 */
public final class PublicFormats {

    public static final ZoneId AMERICA_SP = ZoneId.of("America/Sao_Paulo");

    public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS_SSSSSS = DateTimeFormatter.ofPattern(
        "yyyy-MM-dd'T'HH:mm:ss[.SSSSSS][.SSSSS][.SSSS][.SSS][.SS][.S]");

    public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter ISO_8601_MILLIS_3 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static final DateTimeFormatter ISO_8601_MILLIS_6 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");

    private static final DateTimeFormatter ISO_8601_UTC = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSSSSS][.SSSSS][.SSSS][.SSS][.SS][.S]X");

    public static final DateTimeFormatter DD_MM_YYYY_HH_MM_SS = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static final DateTimeFormatter EVENT_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

    private PublicFormats() {
    }

    /**
     * Formata um LocalDateTime para o formato ISO 8601 com milissegundos de 6 casas.
     */
    public static String formatLocalDateTimeUtcMillis6(LocalDateTime localDateTime) {
        return ISO_8601_MILLIS_6.format(localDateTime.atZone(PublicFormats.AMERICA_SP).withZoneSameInstant(ZoneOffset.UTC));
    }

    /**
     * Formata um LocalDateTime para o formato ISO 8601 com milissegundos de 3 casas.
     */
    public static String formatLocalDateTimeUtcMillis3(LocalDateTime localDateTime) {
        return ISO_8601_MILLIS_3.format(localDateTime.atZone(PublicFormats.AMERICA_SP).withZoneSameInstant(ZoneOffset.UTC));
    }

    public static LocalDateTime parseZonedDateTimeUtcTruncMillis(String zonedDateTime) {
        return ZonedDateTime.parse(zonedDateTime, PublicFormats.ISO_8601_UTC)
            .withZoneSameInstant(PublicFormats.AMERICA_SP)
            .toLocalDateTime();
    }

    public static LocalDateTime parseLocalDateTimeWithMillis(String localDateTime) {
        return LocalDateTime.parse(localDateTime, PublicFormats.YYYY_MM_DD_HH_MM_SS_SSSSSS);
    }

    public static LocalDateTime parsePublicLocalDateTime(String localDateTime) {
        if (localDateTime.contains("Z")) {
            return PublicFormats.parseZonedDateTimeUtcTruncMillis(localDateTime);
        }
        return PublicFormats.parseLocalDateTimeWithMillis(localDateTime);
    }

    public static LocalDateTime parseLocalDateTime(String localDateTime) {
        return LocalDateTime.parse(localDateTime, YYYY_MM_DD_HH_MM_SS.withZone(AMERICA_SP));
    }

    public static LocalDateTime parseLocalDateTime(String localDateTime, DateTimeFormatter dateTimeFormatter) {
        return LocalDateTime.parse(localDateTime, dateTimeFormatter.withZone(AMERICA_SP));
    }

    public static String parseLocalDateTimeEvent(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.format(PublicFormats.EVENT_PATTERN);
    }

    public static String parseLocalDateTimeEvent(String localDateTime) {
        return localDateTime == null ? null : LocalDateTime.parse(localDateTime).format(PublicFormats.EVENT_PATTERN);
    }
}
