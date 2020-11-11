package com.opslycloud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(@Value("${social.network.base.url}") String baseUrl, @Autowired WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .clone()
                .baseUrl(baseUrl)
                .build();
    }
}
