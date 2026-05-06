package no.nav.tilleggsstonader.libs.http.client

import no.nav.security.token.support.client.core.OAuth2ClientException
import no.nav.security.token.support.client.core.http.OAuth2HttpClient
import no.nav.security.token.support.client.core.http.OAuth2HttpRequest
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenResponse
import no.nav.security.token.support.client.spring.oauth2.DefaultOAuth2HttpClient
import no.nav.tilleggsstonader.libs.log.SecureLogger.secureLogger
import org.apache.hc.core5.http.NoHttpResponseException
import org.slf4j.LoggerFactory
import org.springframework.core.NestedExceptionUtils
import org.springframework.http.HttpHeaders
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.net.SocketException
import java.net.SocketTimeoutException

class RetryOAuth2HttpClient(
    private val restClient: RestClient,
    private val maxRetries: Int = 2,
) : OAuth2HttpClient {
    private val logger = LoggerFactory.getLogger(javaClass)

    // ServiceUnavailable håndteres av apache http-client
    private val retryExceptions =
        setOf(
            SocketException::class,
            SocketTimeoutException::class,
            HttpServerErrorException.GatewayTimeout::class,
            NoHttpResponseException::class,
        )

    override fun post(req: OAuth2HttpRequest): OAuth2AccessTokenResponse {
        var retries = 0

        while (true) {
            try {
                return postRequest(req)
            } catch (e: Exception) {
                handleException(e, retries++, req)
            }
        }
    }

    /**
     * Kopi fra [DefaultOAuth2HttpClient]
     */
    private fun postRequest(req: OAuth2HttpRequest): OAuth2AccessTokenResponse =
        restClient
            .post()
            .uri(req.tokenEndpointUrl)
            .headers { it.addAll(headers(req)) }
            .body(
                LinkedMultiValueMap<String, String>().apply {
                    setAll(req.formParameters)
                },
            ).retrieve()
            .onStatus({ it.isError }) { _, response ->
                throw OAuth2ClientException("Received ${response.statusCode} from ${req.tokenEndpointUrl}")
            }.body<OAuth2AccessTokenResponse>() ?: throw OAuth2ClientException("No body in response from ${req.tokenEndpointUrl}")

    private fun headers(req: OAuth2HttpRequest): HttpHeaders = HttpHeaders().apply { putAll(req.oAuth2HttpHeaders.headers) }

    private fun handleException(
        e: Exception,
        retries: Int,
        oAuth2HttpRequest: OAuth2HttpRequest,
    ) {
        val mostSpecificCause = NestedExceptionUtils.getMostSpecificCause(e)
        if (shouldRetry(mostSpecificCause) && retries < maxRetries) {
            logger.info(
                "Kall mot url=${oAuth2HttpRequest.tokenEndpointUrl} feilet på forsøk=${retries + 1}, retryer, cause=${mostSpecificCause::class}",
            )
            secureLogger.info(
                "Kall mot url=${oAuth2HttpRequest.tokenEndpointUrl} feilet på forsøk=${retries + 1}, retryer, feil=${e.message}",
            )
            return
        }

        if (shouldRetry(mostSpecificCause) && retries > 0) {
            logger.warn(
                "Kall mot url=${oAuth2HttpRequest.tokenEndpointUrl} feilet etter ${retries + 1} forsøk, cause=${mostSpecificCause::class}",
            )
            secureLogger.warn("Kall mot url=${oAuth2HttpRequest.tokenEndpointUrl} feilet etter ${retries + 1} forsøk, feil=${e.message}")
        }

        throw e
    }

    private fun shouldRetry(throwable: Throwable): Boolean = retryExceptions.any { it.java.isInstance(throwable) }
}
