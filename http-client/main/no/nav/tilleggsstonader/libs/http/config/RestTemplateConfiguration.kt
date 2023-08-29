package no.nav.tilleggsstonader.libs.http.config

import no.nav.tilleggsstonader.libs.http.client.RetryOAuth2HttpClient
import no.nav.tilleggsstonader.libs.http.interceptor.BearerTokenClientCredentialsClientInterceptor
import no.nav.tilleggsstonader.libs.http.interceptor.BearerTokenClientInterceptor
import no.nav.tilleggsstonader.libs.http.interceptor.BearerTokenExchangeClientInterceptor
import no.nav.tilleggsstonader.libs.http.interceptor.BearerTokenOnBehalfOfClientInterceptor
import no.nav.tilleggsstonader.libs.http.interceptor.ConsumerIdClientInterceptor
import no.nav.tilleggsstonader.libs.http.interceptor.MdcValuesPropagatingClientInterceptor
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.web.client.RestOperations
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
        restTemplateBuilder: RestTemplateBuilder,
        consumerIdClientInterceptor: ConsumerIdClientInterceptor,
        mdcValuesPropagatingClientInterceptor: MdcValuesPropagatingClientInterceptor,
    ): RetryOAuth2HttpClient {
        return RetryOAuth2HttpClient(
            restTemplateBuilder
                .setConnectTimeout(Duration.of(2, ChronoUnit.SECONDS))
                .setReadTimeout(Duration.of(4, ChronoUnit.SECONDS))
                .additionalInterceptors(
                    consumerIdClientInterceptor,
                    mdcValuesPropagatingClientInterceptor,
                ),
        )
    }

    @Bean("utenAuth")
    fun restTemplateUtenAuth(
        restTemplateBuilder: RestTemplateBuilder,
        consumerIdClientInterceptor: ConsumerIdClientInterceptor,
        mdcValuesPropagatingClientInterceptor: MdcValuesPropagatingClientInterceptor,
    ): RestOperations {
        return restTemplateBuilder
            .defaultBuilderConfig()
            .build()
    }

    @Bean("tokenExchange")
    fun restTemplate(
        restTemplateBuilder: RestTemplateBuilder,
        bearerTokenExchangeClientInterceptor: BearerTokenExchangeClientInterceptor,
    ): RestOperations {
        return restTemplateBuilder
            .defaultBuilderConfig()
            .interceptors(bearerTokenExchangeClientInterceptor)
            .build()
    }

    @Bean("azure")
    fun restTemplateJwtBearer(
        restTemplateBuilder: RestTemplateBuilder,
        bearerTokenClientInterceptor: BearerTokenClientInterceptor,
    ): RestOperations {
        return restTemplateBuilder
            .defaultBuilderConfig()
            .additionalInterceptors(bearerTokenClientInterceptor)
            .build()
    }

    @Bean("azureClientCredential")
    fun restTemplateClientCredentialBearer(
        restTemplateBuilder: RestTemplateBuilder,
        bearerTokenClientInterceptor: BearerTokenClientCredentialsClientInterceptor,
    ): RestOperations {
        return restTemplateBuilder
            .defaultBuilderConfig()
            .additionalInterceptors(bearerTokenClientInterceptor)
            .build()
    }

    @Bean("azureOnBehalfOf")
    fun restTemplateOnBehalfOfBearer(
        restTemplateBuilder: RestTemplateBuilder,
        bearerTokenClientInterceptor: BearerTokenOnBehalfOfClientInterceptor,
    ): RestOperations {
        return restTemplateBuilder
            .defaultBuilderConfig()
            .additionalInterceptors(bearerTokenClientInterceptor)
            .build()
    }

    private fun RestTemplateBuilder.defaultBuilderConfig() = this
        .setConnectTimeout(Duration.of(2, ChronoUnit.SECONDS))
        .setReadTimeout(Duration.of(25, ChronoUnit.SECONDS))
        .additionalInterceptors(
            consumerIdClientInterceptor,
            mdcValuesPropagatingClientInterceptor,
        )
}
