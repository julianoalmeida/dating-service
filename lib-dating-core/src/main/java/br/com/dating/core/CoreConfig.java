package br.com.dating.core;

import br.com.dating.publisher.PublisherConfig;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@EntityScan
@ComponentScan
@EnableTransactionManagement
@PropertySource(value = "classpath:core.application.yml", factory = YamlPropertySourceFactory.class)
@PropertySource(
    ignoreResourceNotFound = true,
    value = "classpath:core.application-${spring.profiles.active}.yml",
    factory = YamlPropertySourceFactory.class
)
@PropertySource(
    ignoreResourceNotFound = true,
    value = "classpath:core.application-${spring.profiles.include}.yml",
    factory = YamlPropertySourceFactory.class
)
@EnableJpaRepositories
@EnableFeignClients
@Import({PublisherConfig.class})
public class CoreConfig {
}
