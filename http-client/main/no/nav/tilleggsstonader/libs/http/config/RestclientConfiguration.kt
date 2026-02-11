package no.nav.tilleggsstonader.libs.http.config

import no.nav.tilleggsstonader.libs.http.interceptor.BearerTokenClientCredentialsClientInterceptor
import no.nav.tilleggsstonader.libs.http.interceptor.BearerTokenClientInterceptor
import no.nav.tilleggsstonader.libs.http.interceptor.BearerTokenExchangeClientInterceptor
import no.nav.tilleggsstonader.libs.http.interceptor.BearerTokenOnBehalfOfClientInterceptor
import no.nav.tilleggsstonader.libs.http.interceptor.ConsumerIdClientInterceptor
import no.nav.tilleggsstonader.libs.http.interceptor.MdcValuesPropagatingClientInterceptor
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder
import org.springframework.boot.http.client.HttpClientSettings
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.web.client.RestClient
import java.time.Duration

@Configuration
@Import(
    ConsumerIdClientInterceptor::class,
    BearerTokenClientInterceptor::class,
    BearerTokenClientCredentialsClientInterceptor::class,
    BearerTokenOnBehalfOfClientInterceptor::class,
    BearerTokenExchangeClientInterceptor::class,
    MdcValuesPropagatingClientInterceptor::class,
)
@EnableConfigurationProperties(HttpClientProperties::class)
class RestclientConfiguration(
    private val consumerIdClientInterceptor: ConsumerIdClientInterceptor,
    private val mdcValuesPropagatingClientInterceptor: MdcValuesPropagatingClientInterceptor,
    private val httpClientProperties: HttpClientProperties,
) {
    @Bean("utenAuth")
    fun restClientUtenAuth(restClientBuilder: RestClient.Builder): RestClient =
        restClientBuilder
            .defaultBuilderConfig()
            .build()

    @Bean("tokenExchange")
    fun restClient(
        restClientBuilder: RestClient.Builder,
        bearerTokenExchangeClientInterceptor: BearerTokenExchangeClientInterceptor,
    ): RestClient =
        restClientBuilder
            .defaultBuilderConfig()
            .requestInterceptor(bearerTokenExchangeClientInterceptor)
            .build()

    @Bean("azure")
    fun restClientJwtBearer(
        restClientBuilder: RestClient.Builder,
        bearerTokenClientInterceptor: BearerTokenClientInterceptor,
    ): RestClient =
        restClientBuilder
            .defaultBuilderConfig()
            .requestInterceptor(bearerTokenClientInterceptor)
            .build()

    @Bean("azureClientCredential")
    fun restClientClientCredentialBearer(
        restClientBuilder: RestClient.Builder,
        bearerTokenClientInterceptor: BearerTokenClientCredentialsClientInterceptor,
    ): RestClient =
        restClientBuilder
            .defaultBuilderConfig()
            .requestInterceptor(bearerTokenClientInterceptor)
            .build()

    @Bean("azureOnBehalfOf")
    fun restClientOnBehalfOfBearer(
        restClientBuilder: RestClient.Builder,
        bearerTokenClientInterceptor: BearerTokenOnBehalfOfClientInterceptor,
    ): RestClient =
        restClientBuilder
            .defaultBuilderConfig()
            .requestInterceptor(bearerTokenClientInterceptor)
            .build()

    private fun RestClient.Builder.defaultBuilderConfig() =
        this
            .requestInterceptor(consumerIdClientInterceptor)
            .requestInterceptor(mdcValuesPropagatingClientInterceptor)
            .requestFactory(
                ClientHttpRequestFactoryBuilder.detect().build(
                    HttpClientSettings
                        .defaults()
                        .withConnectTimeout(httpClientProperties.connectTimeout)
                        .withReadTimeout(httpClientProperties.readTimeout),
                ),
            )
}
