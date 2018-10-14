package com.engagetech.expenses.service.exchange.fcc;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.Logger.Level;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FCCApiConfiguration {

    private final String apiUrl;

    public FCCApiConfiguration(@Value("${app.expense.exchange.fcc-api.api-url}") String apiUrl) {
        this.apiUrl = apiUrl;
    }

    @Bean
    public FCCApiClient fixerIoExchangeConnector() {
        ObjectMapper objectMapper = new ObjectMapper()
              .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        return Feign
              .builder()
              .encoder(new JacksonEncoder(objectMapper))
              .decoder(new JacksonDecoder(objectMapper))
              .logLevel(Level.FULL)
              .target(FCCApiClient.class, apiUrl);
    }

}
