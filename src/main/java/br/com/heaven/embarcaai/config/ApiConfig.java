package br.com.heaven.embarcaai.config;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiConfig {
    @Bean
    ModelMapper modelMapper() { return new ModelMapper(); }

    @Bean
    WebMvcConfigurer configurarCors(@Value("${app.cors.origins}") String origens) {
        String[] permitidas = java.util.Arrays.stream(origens.split(","))
                .map(String::trim).toArray(String[]::new);
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins(permitidas)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
            }
        };
    }
}
