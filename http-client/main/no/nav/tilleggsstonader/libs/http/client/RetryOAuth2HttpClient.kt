package no.nav.tilleggsstonader.libs.http.client

import no.nav.security.token.support.client.core.http.OAuth2HttpRequest
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenResponse
import no.nav.security.token.support.client.spring.oauth2.DefaultOAuth2HttpClient
import org.apache.hc.core5.http.NoHttpResponseException
import org.slf4j.LoggerFactory
import org.springframework.core.NestedExceptionUtils
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestClient
import java.net.SocketException
import java.net.SocketTimeoutException

class RetryOAuth2HttpClient(
    restClient: RestClient,
    private val maxRetries: Int = 2,
) : DefaultOAuth2HttpClient(restClient) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val secureLogger = LoggerFactory.getLogger("secureLogger")

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
                val response = super.post(req)
                return response
            } catch (e: Exception) {
                handleException(e, retries++, req)
            }
        }
    }

    private fun handleException(
        e: Exception,
        retries: Int,
        oAuth2HttpRequest: OAuth2HttpRequest,
    ) {
        if (shouldRetry(e) && retries < maxRetries) {
            logger.warn(
                "Kall mot url=${oAuth2HttpRequest.tokenEndpointUrl} feilet, cause=${
                    NestedExceptionUtils.getMostSpecificCause(e)::class
                }",
            )
            secureLogger.warn("Kall mot url=${oAuth2HttpRequest.tokenEndpointUrl} feilet med feil=${e.message}")
        } else {
            throw e
        }
    }

    private fun shouldRetry(e: Exception): Boolean = retryExceptions.contains(e.cause?.let { it::class })
}
