package no.nav.tilleggsstonader.libs.http.config

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.restclient.RestTemplateBuilder
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.TestPropertySource
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestTemplate
import java.time.Duration

@Configuration
@EnableConfigurationProperties(HttpClientProperties::class)
class TestRestTemplateConfigurasjon(
    private val httpClientProperties: HttpClientProperties,
) {
    @Bean
    fun testRestTemplate(): RestTemplate =
        RestTemplateBuilder()
            .connectTimeout(httpClientProperties.connectTimeout)
            .readTimeout(httpClientProperties.readTimeout)
            .build()
}

@SpringBootTest(classes = [TestRestTemplateConfigurasjon::class])
@TestPropertySource(
    properties = [
        "tilleggsstonader.http-client.connect-timeout=PT1S",
        "tilleggsstonader.http-client.read-timeout=PT2S",
    ],
)
class RestTemplateTimeoutTest {
    private lateinit var restTemplate: RestTemplate
    private lateinit var httpClientProperties: HttpClientProperties
    private lateinit var wireMockServer: WireMockServer

    @BeforeEach
    fun settOpp() {
        wireMockServer = WireMockServer(WireMockConfiguration.options().port(8089))
        wireMockServer.start()

        httpClientProperties =
            HttpClientProperties(
                connectTimeout = Duration.ofSeconds(1),
                readTimeout = Duration.ofSeconds(2),
            )

        restTemplate =
            RestTemplateBuilder()
                .connectTimeout(httpClientProperties.connectTimeout)
                .readTimeout(httpClientProperties.readTimeout)
                .build()
    }

    @AfterEach
    fun ryddOpp() {
        wireMockServer.stop()
    }

    @Test
    fun `skal timeoute når server svarer tregere enn valgt timeout`() {
        // Gitt trengt endepunkt
        wireMockServer.stubFor(
            get(urlEqualTo("/tregt-endepunkt"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withBody("OK")
                        .withFixedDelay(3000), // 3 sekunder forsinkelse, lenger enn 2 sekunder read-timeout
                ),
        )

        val startTid = System.currentTimeMillis()
        try {
            restTemplate.getForObject("http://localhost:8089/tregt-endepunkt", String::class.java)
            throw AssertionError("Forventet timeout-unntak")
        } catch (e: ResourceAccessException) {
            val varighet = System.currentTimeMillis() - startTid

            // Verifiser at timeouten skjedde innenfor ballpark av det vi forventer
            assertThat(varighet).isGreaterThan(1500) // Skal vente i hvert fall nær 2 sekunder
            assertThat(varighet).isLessThan(4000) // Skal ikke vente hele 4 sekunder
            assertThat(e.message).contains("Read timed out")
        }
    }

    @Test
    fun `skal lykkes når server svarer raskere enn valgt timeout`() {
        // Gitt raskt endepunkt
        wireMockServer.stubFor(
            get(urlEqualTo("/raskt-endepunkt"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withBody("Success")
                        .withFixedDelay(500), // 0.5 sekunders forsinkelse
                ),
        )

        // Skal gå fint
        val respons = restTemplate.getForObject("http://localhost:8089/raskt-endepunkt", String::class.java)
        assertThat(respons).isEqualTo("Success")
    }
}
