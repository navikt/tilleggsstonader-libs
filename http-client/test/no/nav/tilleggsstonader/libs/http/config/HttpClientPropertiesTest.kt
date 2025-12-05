package no.nav.tilleggsstonader.libs.http.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.context.properties.bind.Binder
import org.springframework.boot.context.properties.source.ConfigurationPropertySource
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource
import java.time.Duration

class HttpClientPropertiesTest {
    @Test
    fun `skal bruke standardverdier når ingen konfigurasjon er satt`() {
        val properties = HttpClientProperties()

        assertThat(properties.connectTimeout).isEqualTo(Duration.ofSeconds(2))
        assertThat(properties.readTimeout).isEqualTo(Duration.ofSeconds(25))
    }

    @Test
    fun `skal parse konfigurasjon fra properties`() {
        val source =
            mapOf(
                "tilleggsstonader.http-client.connect-timeout" to "PT5S",
                "tilleggsstonader.http-client.read-timeout" to "PT60S",
            )

        val properties = bindProperties(source)

        assertThat(properties.connectTimeout).isEqualTo(Duration.ofSeconds(5))
        assertThat(properties.readTimeout).isEqualTo(Duration.ofSeconds(60))
    }

    @Test
    fun `skal bruke standardverdi hvis kun en property er satt`() {
        val source =
            mapOf(
                "tilleggsstonader.http-client.connect-timeout" to "PT10S",
            )

        val properties = bindProperties(source)

        assertThat(properties.connectTimeout).isEqualTo(Duration.ofSeconds(10))
        assertThat(properties.readTimeout).isEqualTo(Duration.ofSeconds(25)) // standard
    }

    private fun bindProperties(source: Map<String, String>): HttpClientProperties {
        val propertySource: ConfigurationPropertySource = MapConfigurationPropertySource(source)
        val binder = Binder(propertySource)
        return binder
            .bind("tilleggsstonader.http-client", HttpClientProperties::class.java)
            .takeIf { it.isBound }
            ?.get()
            ?: HttpClientProperties()
    }
}
