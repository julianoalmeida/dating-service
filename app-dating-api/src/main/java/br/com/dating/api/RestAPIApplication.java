package br.com.dating.api;


import br.com.dating.core.CoreConfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({CoreConfig.class})
public class RestAPIApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestAPIApplication.class, args);
    }
}
