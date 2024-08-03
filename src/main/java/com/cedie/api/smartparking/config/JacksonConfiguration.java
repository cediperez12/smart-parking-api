package com.cedie.api.smartparking.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.jackson.ModelResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration {

    @Bean
    public ModelResolver modelResolver(ObjectMapper mapper) {
        return new ModelResolver(mapper);
    }
}
