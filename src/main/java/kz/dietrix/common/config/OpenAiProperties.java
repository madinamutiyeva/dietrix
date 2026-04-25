package kz.dietrix.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "openai")
public class OpenAiProperties {

    private String apiKey;
    private String model = "gpt-3.5-turbo";
    private String baseUrl = "https://api.openai.com";
}

