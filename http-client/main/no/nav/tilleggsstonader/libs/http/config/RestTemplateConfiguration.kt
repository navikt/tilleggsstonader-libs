package no.nav.tilleggsstonader.libs.http.config

import no.nav.tilleggsstonader.libs.http.client.RetryOAuth2HttpClient
import no.nav.tilleggsstonader.libs.http.interceptor.BearerTokenClientCredentialsClientInterceptor
import no.nav.tilleggsstonader.libs.http.interceptor.BearerTokenClientInterceptor
import no.nav.tilleggsstonader.libs.http.interceptor.BearerTokenExchangeClientInterceptor
import no.nav.tilleggsstonader.libs.http.interceptor.BearerTokenOnBehalfOfClientInterceptor
import no.nav.tilleggsstonader.libs.http.interceptor.ConsumerIdClientInterceptor
import no.nav.tilleggsstonader.libs.http.interceptor.MdcValuesPropagatingClientInterceptor
import org.springframework.beans.factory.annotation.Qualifier
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
    RestTemplateBuilder::class,
    ConsumerIdClientInterceptor::class,
    BearerTokenClientInterceptor::class,
    BearerTokenClientCredentialsClientInterceptor::class,
    BearerTokenOnBehalfOfClientInterceptor::class,
    BearerTokenExchangeClientInterceptor::class,
    MdcValuesPropagatingClientInterceptor::class,
)
class RestTemplateConfiguration {

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

    @Bean("basicRestTemplateBuilder")
    fun basicRestTemplateBuilder(
        restTemplateBuilder: RestTemplateBuilder,
        consumerIdClientInterceptor: ConsumerIdClientInterceptor,
        mdcValuesPropagatingClientInterceptor: MdcValuesPropagatingClientInterceptor,
    ): RestTemplateBuilder {
        return restTemplateBuilder
            .setConnectTimeout(Duration.of(5, ChronoUnit.SECONDS))
            .setReadTimeout(Duration.of(25, ChronoUnit.SECONDS))
            .additionalInterceptors(
                consumerIdClientInterceptor,
                mdcValuesPropagatingClientInterceptor,
            )
    }

    @Bean("utenAuth")
    fun restTemplateUtenAuth(
        @Qualifier("basicRestTemplateBuilder") restTemplateBuilder: RestTemplateBuilder,
    ): RestOperations {
        return restTemplateBuilder.build()
    }

    @Bean("tokenExchange")
    fun restTemplate(
        @Qualifier("basicRestTemplateBuilder") restTemplateBuilder: RestTemplateBuilder,
        bearerTokenExchangeClientInterceptor: BearerTokenExchangeClientInterceptor,
    ): RestOperations {
        return restTemplateBuilder
            .interceptors(bearerTokenExchangeClientInterceptor)
            .build()
    }

    @Bean("azure")
    fun restTemplateJwtBearer(
        @Qualifier("basicRestTemplateBuilder") restTemplateBuilder: RestTemplateBuilder,
        bearerTokenClientInterceptor: BearerTokenClientInterceptor,
    ): RestOperations {
        return restTemplateBuilder
            .setConnectTimeout(Duration.of(5, ChronoUnit.SECONDS))
            .setReadTimeout(Duration.of(25, ChronoUnit.SECONDS))
            .additionalInterceptors(bearerTokenClientInterceptor)
            .build()
    }

    @Bean("azureClientCredential")
    fun restTemplateClientCredentialBearer(
        @Qualifier("basicRestTemplateBuilder") restTemplateBuilder: RestTemplateBuilder,
        bearerTokenClientInterceptor: BearerTokenClientCredentialsClientInterceptor,
    ): RestOperations {
        return restTemplateBuilder
            .additionalInterceptors(bearerTokenClientInterceptor)
            .build()
    }

    @Bean("azureOnBehalfOf")
    fun restTemplateOnBehalfOfBearer(
        @Qualifier("basicRestTemplateBuilder") restTemplateBuilder: RestTemplateBuilder,
        bearerTokenClientInterceptor: BearerTokenOnBehalfOfClientInterceptor,
    ): RestOperations {
        return restTemplateBuilder
            .additionalInterceptors(bearerTokenClientInterceptor)
            .build()
    }
}
