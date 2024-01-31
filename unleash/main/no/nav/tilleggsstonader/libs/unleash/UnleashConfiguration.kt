package no.nav.tilleggsstonader.libs.unleash

import io.getunleash.strategy.Strategy
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(UnleashProperties::class)
class UnleashConfiguration(
    val properties: UnleashProperties,
    @Value("\${UNLEASH_SERVER_API_URL}") val apiUrl: String,
    @Value("\${UNLEASH_SERVER_API_TOKEN}") val apiToken: String,
    @Value("\${NAIS_APP_NAME}") val appName: String,
    private val strategies: List<Strategy>,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun unleashNext(): UnleashService =
        if (properties.enabled) {
            logger.info("Oppretter FeatureToggleService med strategies:${strategies.map { it.javaClass.simpleName }}")
            DefaultUnleashService(
                apiUrl = apiUrl,
                apiToken = apiToken,
                appName = appName,
                strategies = strategies,
            )
        } else {
            logger.warn(
                "Funksjonsbryter-funksjonalitet er skrudd AV. " +
                    "isEnabled gir 'false' med mindre man har oppgitt en annen default verdi.",
            )
            DummyUnleashService()
        }
}

@ConfigurationProperties("unleash")
class UnleashProperties(
    val enabled: Boolean = true,
)
