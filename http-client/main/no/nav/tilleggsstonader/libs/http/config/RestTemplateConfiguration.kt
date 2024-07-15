package no.nav.tilleggsstonader.libs.http.config

import no.nav.tilleggsstonader.libs.http.client.RetryOAuth2HttpClient
import no.nav.tilleggsstonader.libs.http.interceptor.BearerTokenClientCredentialsClientInterceptor
import no.nav.tilleggsstonader.libs.http.interceptor.BearerTokenClientInterceptor
import no.nav.tilleggsstonader.libs.http.interceptor.BearerTokenExchangeClientInterceptor
import no.nav.tilleggsstonader.libs.http.interceptor.BearerTokenOnBehalfOfClientInterceptor
import no.nav.tilleggsstonader.libs.http.interceptor.ConsumerIdClientInterceptor
import no.nav.tilleggsstonader.libs.http.interceptor.MdcValuesPropagatingClientInterceptor
import org.springframework.boot.web.client.ClientHttpRequestFactories
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestTemplate
import java.time.Duration
import java.time.temporal.ChronoUnit

@Suppress("SpringFacetCodeInspection")
@Configuration
@Import(
    ConsumerIdClientInterceptor::class,
    BearerTokenClientInterceptor::class,
    BearerTokenClientCredentialsClientInterceptor::class,
    BearerTokenOnBehalfOfClientInterceptor::class,
    BearerTokenExchangeClientInterceptor::class,
    MdcValuesPropagatingClientInterceptor::class,
)
class RestTemplateConfiguration(
    private val consumerIdClientInterceptor: ConsumerIdClientInterceptor,
    private val mdcValuesPropagatingClientInterceptor: MdcValuesPropagatingClientInterceptor,
) {

    @Primary
    @Bean
    fun oAuth2HttpClient(
        restClientBuilder: RestClient.Builder,
        consumerIdClientInterceptor: ConsumerIdClientInterceptor,
        mdcValuesPropagatingClientInterceptor: MdcValuesPropagatingClientInterceptor,
    ): RetryOAuth2HttpClient {
        val clientHttpRequestFactorySettings = ClientHttpRequestFactorySettings.DEFAULTS
            .withConnectTimeout(Duration.of(1, ChronoUnit.SECONDS))
            .withReadTimeout(Duration.of(1, ChronoUnit.SECONDS))
        val restClient = restClientBuilder
            .requestFactory(ClientHttpRequestFactories.get(clientHttpRequestFactorySettings))
            .requestInterceptor(consumerIdClientInterceptor)
            .requestInterceptor(mdcValuesPropagatingClientInterceptor)
            .build()
        return RetryOAuth2HttpClient(restClient)
    }

    @Bean("utenAuth")
    fun restTemplateUtenAuth(
        restTemplateBuilder: RestTemplateBuilder,
        consumerIdClientInterceptor: ConsumerIdClientInterceptor,
        mdcValuesPropagatingClientInterceptor: MdcValuesPropagatingClientInterceptor,
    ): RestTemplate {
        return restTemplateBuilder
            .defaultBuilderConfig()
            .build()
    }

    @Bean("tokenExchange")
    fun restTemplate(
        restTemplateBuilder: RestTemplateBuilder,
        bearerTokenExchangeClientInterceptor: BearerTokenExchangeClientInterceptor,
    ): RestTemplate {
        return restTemplateBuilder
            .defaultBuilderConfig()
            .interceptors(bearerTokenExchangeClientInterceptor)
            .build()
    }

    @Bean("azure")
    fun restTemplateJwtBearer(
        restTemplateBuilder: RestTemplateBuilder,
        bearerTokenClientInterceptor: BearerTokenClientInterceptor,
    ): RestTemplate {
        return restTemplateBuilder
            .defaultBuilderConfig()
            .additionalInterceptors(bearerTokenClientInterceptor)
            .build()
    }

    @Bean("azureClientCredential")
    fun restTemplateClientCredentialBearer(
        restTemplateBuilder: RestTemplateBuilder,
        bearerTokenClientInterceptor: BearerTokenClientCredentialsClientInterceptor,
    ): RestTemplate {
        return restTemplateBuilder
            .defaultBuilderConfig()
            .additionalInterceptors(bearerTokenClientInterceptor)
            .build()
    }

    @Bean("azureOnBehalfOf")
    fun restTemplateOnBehalfOfBearer(
        restTemplateBuilder: RestTemplateBuilder,
        bearerTokenClientInterceptor: BearerTokenOnBehalfOfClientInterceptor,
    ): RestTemplate {
        return restTemplateBuilder
            .defaultBuilderConfig()
            .additionalInterceptors(bearerTokenClientInterceptor)
            .build()
    }

    // Ekspirmentell som har inn identisk config som familie har då azure-graph feiler
    @Bean("jwtBearer")
    fun restTemplateJwtBearer(
        consumerIdClientInterceptor: ConsumerIdClientInterceptor,
        bearerTokenClientInterceptor: BearerTokenClientInterceptor,
    ): RestTemplate =
        RestTemplateBuilder()
            .interceptors(
                consumerIdClientInterceptor,
                bearerTokenClientInterceptor,
                MdcValuesPropagatingClientInterceptor(),
            ).setConnectTimeout(Duration.ofSeconds(20))
            .setReadTimeout(Duration.ofSeconds(20))
            .build()

    private fun RestTemplateBuilder.defaultBuilderConfig() = this
        .setConnectTimeout(Duration.of(2, ChronoUnit.SECONDS))
        .setReadTimeout(Duration.of(25, ChronoUnit.SECONDS))
        .additionalInterceptors(
            consumerIdClientInterceptor,
            mdcValuesPropagatingClientInterceptor,
        )
}
