package br.com.dating.consumer;

import br.com.dating.core.CoreConfig;
import br.com.dating.publisher.PublisherConfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({CoreConfig.class, PublisherConfig.class})
public class EventConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventConsumerApplication.class, args);
    }
}
