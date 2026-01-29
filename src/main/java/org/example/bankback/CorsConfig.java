package org.example.bankback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns("*")  // Permite todos los orígenes
                        .allowedMethods("*")  // Permite todos los métodos HTTP
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .exposedHeaders("Authorization", "Content-Type")
                        .maxAge(3600);
            }
        };
    }
}
