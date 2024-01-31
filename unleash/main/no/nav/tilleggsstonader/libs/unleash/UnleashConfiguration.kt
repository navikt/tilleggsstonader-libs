package no.nav.tilleggsstonader.libs.unleash

import io.getunleash.strategy.Strategy
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class UnleashConfiguration(
    private val strategies: List<Strategy>,
) {
    private val logger = LoggerFactory.getLogger(UnleashConfiguration::class.java)

    @Bean
    @ConditionalOnProperty("unleash.enabled", havingValue = "true", matchIfMissing = true)
    fun unleashService(
        @Value("\${UNLEASH_SERVER_API_URL}") apiUrl: String,
        @Value("\${UNLEASH_SERVER_API_TOKEN}") apiToken: String,
        @Value("\${NAIS_APP_NAME}") appName: String,
    ): UnleashService {
        logger.info("Oppretter FeatureToggleService med strategies:${strategies.map { it.javaClass.simpleName }}")
        return DefaultUnleashService(
            apiUrl = apiUrl,
            apiToken = apiToken,
            appName = appName,
            strategies = strategies,
        )
    }

    @Bean
    @ConditionalOnProperty("unleash.enabled", havingValue = "false", matchIfMissing = false)
    fun dummyUnleashService(): UnleashService {
        logger.warn(
            "Funksjonsbryter-funksjonalitet er skrudd AV. " +
                    "isEnabled gir 'false' med mindre man har oppgitt en annen default verdi.",
        )
        return DummyUnleashService()
    }
}
