package no.nav.tilleggsstonader.libs.http.client

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.anyUrl
import com.github.tomakehurst.wiremock.client.WireMock.okJson
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import no.nav.tilleggsstonader.kontrakter.felles.ObjectMapperProvider.objectMapper
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate
import java.net.URI

internal class AbstractRestClientTest {

    class TestClient(val uri: URI) : AbstractRestClient(RestTemplate()) {

        fun test() {
            getForEntity<Any>(uri.toString())
        }

        fun testMedUriVariables() {
            getForEntity<Any>("$uri/api/test/{id}/data", uriVariables = mapOf("id" to "123"))
        }
    }

    companion object {

        private lateinit var wireMockServer: WireMockServer
        private lateinit var client: TestClient

        @BeforeAll
        @JvmStatic
        fun initClass() {
            val notifier = ConsoleNotifier(false) // enabler logging ved feil
            wireMockServer = WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort().notifier(notifier))
            wireMockServer.start()
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            wireMockServer.stop()
        }
    }

    @AfterEach
    fun tearDownEachTest() {
        wireMockServer.resetAll()
    }

    @BeforeEach
    fun setupEachTest() {
        client = TestClient(URI.create("http://localhost:${wireMockServer.port()}"))
    }

    @Test
    fun `skal kunne kalle på tjeneste med uriVariables`() {
        wireMockServer.stubFor(
            WireMock.get(urlEqualTo("api/test/123/data"))
                .willReturn(okJson(objectMapper.writeValueAsString(mapOf("test" to "ok")))),
        )

        assertDoesNotThrow {
            client.testMedUriVariables()
        }
    }

    @Test
    internal fun `feil med problemDetail kaster ProblemDetailException`() {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Feil")
        val body = objectMapper.writeValueAsString(problemDetail)
        wireMockServer.stubFor(
            WireMock.get(anyUrl())
                .willReturn(aResponse().withStatus(500).withBody(body)),
        )
        val catchThrowable = catchThrowable { client.test() }
        assertThat(catchThrowable).isInstanceOfAny(ProblemDetailException::class.java)
        assertThat(catchThrowable).hasCauseInstanceOf(HttpServerErrorException::class.java)
        val ressursException = catchThrowable as ProblemDetailException
        assertThat(ressursException.httpStatus).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
        assertThat(ressursException.detail.detail).isEqualTo(problemDetail.detail)
    }

    @Test
    internal fun `feil med body som inneholder feltet status men ikke er en ressurs`() {
        val body = objectMapper.writeValueAsString(mapOf("status" to "nei"))
        wireMockServer.stubFor(
            WireMock.get(anyUrl())
                .willReturn(aResponse().withStatus(500).withBody(body)),
        )
        val catchThrowable = catchThrowable { client.test() }
        assertThat(catchThrowable).isInstanceOfAny(HttpServerErrorException::class.java)
        assertThat((catchThrowable as HttpServerErrorException).statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @Test
    internal fun `feil uten ressurs kaster videre spring exception`() {
        wireMockServer.stubFor(
            WireMock.get(anyUrl())
                .willReturn(aResponse().withStatus(500)),
        )
        val catchThrowable = catchThrowable { client.test() }
        assertThat(catchThrowable).isInstanceOfAny(HttpServerErrorException::class.java)
        assertThat((catchThrowable as HttpServerErrorException).statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
