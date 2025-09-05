package br.com.dating.core;

import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.config.NamingConvention;

@Configuration
class MicrometerConfig {

    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config()
            .withHighCardinalityTagsDetector()
            .namingConvention(NamingConvention.snakeCase)
            .commonTags(
                "app_name",
                "dating"
            )
            .meterFilter(MeterFilter.ignoreTags("id", "class", "outcome"))
            .meterFilter(MeterFilter.deny(this::isActuatorUri))
            .meterFilter(MeterFilter.deny(this::isFavicon));
    }

    private Boolean isActuatorUri(Meter.Id id) {
        var uri = id.getTag("uri");
        return uri != null && uri.startsWith("/actuator");
    }

    private Boolean isFavicon(Meter.Id id) {
        var uri = id.getTag("uri");
        return uri != null && uri.contains("favicon");
    }
}
