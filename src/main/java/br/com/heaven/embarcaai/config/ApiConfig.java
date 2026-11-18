package br.com.heaven.embarcaai.config;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.modelmapper.convention.MatchingStrategies;

@Configuration
public class ApiConfig {
    @Bean
    ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        // STRICT: o nome do campo tem que ser igual. Evita que pacienteId ou unidadeId sejam copiados para "id".
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return mapper;
    }

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
