package kz.dietrix.common.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        // Connection pool tuned to drop stale keep-alive sockets that Cloudflare/OpenAI
        // closes after ~30s of idleness — that's the root cause of "Connection reset".
        ConnectionProvider provider = ConnectionProvider.builder("openai-pool")
                .maxConnections(50)
                .maxIdleTime(Duration.ofSeconds(20))     // close idle conn before remote does
                .maxLifeTime(Duration.ofMinutes(5))      // hard cap on connection age
                .pendingAcquireTimeout(Duration.ofSeconds(30))
                .evictInBackground(Duration.ofSeconds(30))
                .build();

        HttpClient httpClient = HttpClient.create(provider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30_000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .responseTimeout(Duration.ofSeconds(90))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(90, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(30, TimeUnit.SECONDS)));

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(cfg -> cfg.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .build();

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(strategies);
    }
}
