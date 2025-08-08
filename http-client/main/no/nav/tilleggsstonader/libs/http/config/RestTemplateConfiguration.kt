package no.nav.tilleggsstonader.libs.http.config

import no.nav.tilleggsstonader.libs.http.client.RetryOAuth2HttpClient
import no.nav.tilleggsstonader.libs.http.interceptor.BearerTokenClientCredentialsClientInterceptor
import no.nav.tilleggsstonader.libs.http.interceptor.BearerTokenClientInterceptor
import no.nav.tilleggsstonader.libs.http.interceptor.BearerTokenExchangeClientInterceptor
import no.nav.tilleggsstonader.libs.http.interceptor.BearerTokenOnBehalfOfClientInterceptor
import no.nav.tilleggsstonader.libs.http.interceptor.ConsumerIdClientInterceptor
import no.nav.tilleggsstonader.libs.http.interceptor.MdcValuesPropagatingClientInterceptor
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestTemplate
import java.time.Duration

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
@EnableConfigurationProperties(HttpClientProperties::class)
class RestTemplateConfiguration(
    private val consumerIdClientInterceptor: ConsumerIdClientInterceptor,
    private val mdcValuesPropagatingClientInterceptor: MdcValuesPropagatingClientInterceptor,
    private val httpClientProperties: HttpClientProperties,
) {
    @Primary
    @Bean
    fun oAuth2HttpClient(
        restClientBuilder: RestClient.Builder,
        consumerIdClientInterceptor: ConsumerIdClientInterceptor,
        mdcValuesPropagatingClientInterceptor: MdcValuesPropagatingClientInterceptor,
    ): RetryOAuth2HttpClient {
        val clientHttpRequestFactorySettings =
            ClientHttpRequestFactorySettings
                .defaults()
                .withConnectTimeout(Duration.ofSeconds(1))
                .withReadTimeout(Duration.ofSeconds(1))
        val requestFactory = ClientHttpRequestFactoryBuilder.detect().build(clientHttpRequestFactorySettings)
        val restClient =
            restClientBuilder
                .requestFactory(requestFactory)
                .requestInterceptor(consumerIdClientInterceptor)
                .requestInterceptor(mdcValuesPropagatingClientInterceptor)
                .build()
        return RetryOAuth2HttpClient(restClient)
    }

    @Bean("utenAuth")
    fun restTemplateUtenAuth(restTemplateBuilder: RestTemplateBuilder): RestTemplate =
        restTemplateBuilder
            .defaultBuilderConfig()
            .build()

    @Bean("tokenExchange")
    fun restTemplate(
        restTemplateBuilder: RestTemplateBuilder,
        bearerTokenExchangeClientInterceptor: BearerTokenExchangeClientInterceptor,
    ): RestTemplate =
        restTemplateBuilder
            .defaultBuilderConfig()
            .interceptors(bearerTokenExchangeClientInterceptor)
            .build()

    @Bean("azure")
    fun restTemplateJwtBearer(
        restTemplateBuilder: RestTemplateBuilder,
        bearerTokenClientInterceptor: BearerTokenClientInterceptor,
    ): RestTemplate =
        restTemplateBuilder
            .defaultBuilderConfig()
            .additionalInterceptors(bearerTokenClientInterceptor)
            .build()

    @Bean("azureClientCredential")
    fun restTemplateClientCredentialBearer(
        restTemplateBuilder: RestTemplateBuilder,
        bearerTokenClientInterceptor: BearerTokenClientCredentialsClientInterceptor,
    ): RestTemplate =
        restTemplateBuilder
            .defaultBuilderConfig()
            .additionalInterceptors(bearerTokenClientInterceptor)
            .build()

    @Bean("azureOnBehalfOf")
    fun restTemplateOnBehalfOfBearer(
        restTemplateBuilder: RestTemplateBuilder,
        bearerTokenClientInterceptor: BearerTokenOnBehalfOfClientInterceptor,
    ): RestTemplate =
        restTemplateBuilder
            .defaultBuilderConfig()
            .additionalInterceptors(bearerTokenClientInterceptor)
            .build()

    private fun RestTemplateBuilder.defaultBuilderConfig() =
        this
            .connectTimeout(httpClientProperties.connectTimeout)
            .readTimeout(httpClientProperties.readTimeout)
            .additionalInterceptors(
                consumerIdClientInterceptor,
                mdcValuesPropagatingClientInterceptor,
            )
}
