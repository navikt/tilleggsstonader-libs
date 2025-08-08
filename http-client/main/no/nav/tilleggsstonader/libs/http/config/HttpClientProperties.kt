package no.nav.tilleggsstonader.libs.http.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "tilleggsstonader.http-client")
data class HttpClientProperties(
    val connectTimeout: Duration = Duration.ofSeconds(2),
    val readTimeout: Duration = Duration.ofSeconds(25),
)
