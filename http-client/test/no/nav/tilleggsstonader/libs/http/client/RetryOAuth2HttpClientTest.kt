package no.nav.tilleggsstonader.libs.http.client

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.http.Fault
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder
import com.github.tomakehurst.wiremock.stubbing.Scenario
import no.nav.security.token.support.client.core.http.OAuth2HttpHeaders
import no.nav.security.token.support.client.core.http.OAuth2HttpRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder
import org.springframework.boot.http.client.HttpClientSettings
import org.springframework.web.client.RestClient
import java.net.URI
import java.time.Duration
import java.time.temporal.ChronoUnit

internal class RetryOAuth2HttpClientTest {
    private val clientHttpRequestFactorySettings =
        HttpClientSettings
            .defaults()
            .withConnectTimeout(Duration.of(1, ChronoUnit.SECONDS))
            .withReadTimeout(Duration.of(1, ChronoUnit.SECONDS))
    private val requestFactory = ClientHttpRequestFactoryBuilder.detect().build(clientHttpRequestFactorySettings)
    private val restClient =
        RestClient
            .builder()
            .requestFactory(requestFactory)
            .build()
    private val client = RetryOAuth2HttpClient(restClient)
    private val logger = LoggerFactory.getLogger(RetryOAuth2HttpClient::class.java) as Logger
    private val listAppender = ListAppender<ILoggingEvent>()

    @BeforeEach
    internal fun setUp() {
        wireMockServer.resetAll()
        logger.detachAppender(listAppender)
        listAppender.list.clear()
        listAppender.start()
        logger.addAppender(listAppender)
    }

    @Test
    internal fun `200 - skal kun kalle en gang`() {
        stub(successResponse())
        assertThat(post()).isNull()
        wireMockServer.verify(1, RequestPatternBuilder.allRequests())
    }

    @Test
    internal fun `404 - skal kun kalle en gang`() {
        stub(WireMock.serverError().withStatus(404))
        post()
        wireMockServer.verify(1, RequestPatternBuilder.allRequests())
    }

    @Test
    internal fun `503 - skal prøve på nytt`() {
        stub(WireMock.aResponse().withStatus(503))
        post()
        wireMockServer.verify(2, RequestPatternBuilder.allRequests())
    }

    @Test
    internal fun `socketException - skal prøve på nytt`() {
        stub(WireMock.serverError().withFault(Fault.CONNECTION_RESET_BY_PEER))
        post()
        wireMockServer.verify(3, RequestPatternBuilder.allRequests())
    }

    @Test
    internal fun `timeout - skal prøve på nytt ved timeout`() {
        stub(WireMock.serverError().withFixedDelay(2000))
        post()
        wireMockServer.verify(3, RequestPatternBuilder.allRequests())
    }

    @Test
    internal fun `fault - skal prøve på nytt hvis servern feiler med å svare`() {
        stub(WireMock.aResponse().withBody("{}").withFault(Fault.EMPTY_RESPONSE))
        post()
        wireMockServer.verify(3, RequestPatternBuilder.allRequests())
    }

    @Test
    internal fun `skal logge info når retry lykkes`() {
        wireMockServer.stubFor(
            WireMock
                .post(WireMock.anyUrl())
                .inScenario("retry-lykkes")
                .whenScenarioStateIs(Scenario.STARTED)
                .willSetStateTo("andre-forsok")
                .willReturn(successResponse().withFixedDelay(1200)),
        )
        wireMockServer.stubFor(
            WireMock
                .post(WireMock.anyUrl())
                .inScenario("retry-lykkes")
                .whenScenarioStateIs("andre-forsok")
                .willReturn(successResponse()),
        )

        val exception = post()

        assertThat(exception).isNull()
        assertThat(loggmeldingerPå(Level.INFO)).hasSize(1)
        assertThat(loggmeldingerPå(Level.WARN)).isEmpty()
    }

    @Test
    internal fun `skal logge warning når alle retryforsok feiler`() {
        stub(successResponse().withFixedDelay(1200))

        val exception = post()

        assertThat(exception).isNotNull()
        assertThat(loggmeldingerPå(Level.INFO)).hasSize(2)
        assertThat(loggmeldingerPå(Level.WARN)).hasSize(1)
    }

    private fun stub(responseDefinitionBuilder: ResponseDefinitionBuilder?) {
        wireMockServer.stubFor(
            WireMock
                .post(WireMock.anyUrl())
                .willReturn(responseDefinitionBuilder),
        )
    }

    private fun successResponse(): ResponseDefinitionBuilder =
        WireMock
            .aResponse()
            .withHeader("Content-Type", "application/json")
            .withBody(SUCCESS_BODY)

    private fun post(): Exception? =
        try {
            client.post(
                OAuth2HttpRequest
                    .builder(URI.create(wireMockServer.baseUrl()))
                    .oAuth2HttpHeaders(OAuth2HttpHeaders.builder().build())
                    .build(),
            )
            null
        } catch (e: Exception) {
            e
        }

    private fun loggmeldingerPå(level: Level): List<ILoggingEvent> = listAppender.list.filter { it.level == level }

    companion object {
        private lateinit var wireMockServer: WireMockServer
        private const val SUCCESS_BODY = """{"access_token":"token","expires_in":3600,"token_type":"Bearer"}"""

        @BeforeAll
        @JvmStatic
        fun initClass() {
            wireMockServer = WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort())
            wireMockServer.start()
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            wireMockServer.stop()
        }
    }
}
