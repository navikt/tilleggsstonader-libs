package no.nav.tilleggsstonader.libs.http.client

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.tilleggsstonader.kontrakter.felles.ObjectMapperProvider.objectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.net.URI

/**
 * Abstract klasse for å kalle rest-tjenester med metrics og utpakking av ev. body.
 */
abstract class AbstractRestClient(val operations: RestTemplate) {

    protected val secureLogger: Logger = LoggerFactory.getLogger("secureLogger")
    protected val log: Logger = LoggerFactory.getLogger(javaClass)

    inline fun <reified T : Any> getForEntity(
        uri: String,
        httpHeaders: HttpHeaders? = null,
        vararg uriVariables: Any,
    ): T {
        return execute(uri, HttpMethod.GET, uriVariables) {
            operations.exchange(uri, HttpMethod.GET, HttpEntity(null, httpHeaders), uriVariables)
        }
    }

    inline fun <reified T : Any> postForEntity(
        uri: String,
        payload: Any,
        httpHeaders: HttpHeaders? = null,
        vararg uriVariables: Any,
    ): T {
        return execute(uri, HttpMethod.POST, uriVariables) {
            operations.exchange(uri, HttpMethod.POST, HttpEntity(payload, httpHeaders), uriVariables)
        }
    }

    inline fun <reified T : Any> putForEntity(
        uri: String,
        payload: Any,
        httpHeaders: HttpHeaders? = null,
        vararg uriVariables: Any,
    ): T {
        return execute(uri, HttpMethod.PUT, uriVariables) {
            operations.exchange(uri, HttpMethod.PUT, HttpEntity(payload, httpHeaders), uriVariables)
        }
    }

    inline fun <reified T : Any> patchForEntity(
        uri: String,
        payload: Any,
        httpHeaders: HttpHeaders? = null,
        vararg uriVariables: Any,
    ): T {
        return execute(uri, HttpMethod.PATCH, uriVariables) {
            operations.exchange(uri, HttpMethod.PATCH, HttpEntity(payload, httpHeaders), uriVariables)
        }
    }

    inline fun <reified T : Any> deleteForEntity(
        uri: String,
        payload: Any? = null,
        httpHeaders: HttpHeaders? = null,
        vararg uriVariables: Any,
    ): T {
        return execute(uri, HttpMethod.DELETE, uriVariables) {
            operations.exchange(uri, HttpMethod.DELETE, HttpEntity(payload, httpHeaders), uriVariables)
        }
    }

    fun <T> execute(
        urlTemplate: String,
        method: HttpMethod,
        vararg uriVariables: Any,
        function: () -> ResponseEntity<T>,
    ): T {
        try {
            return function().body ?: error("Mangler body")
        } catch (e: RestClientResponseException) {
            readProblemDetail(e)?.let { throw ProblemDetailException(it, e) } ?: throw e
        } catch (e: Exception) {
            val url: URI = operations.uriTemplateHandler.expand(urlTemplate, *uriVariables)
            secureLogger.warn("Feil ved kall method=$method mot url=$url", e)
            throw RuntimeException("Feil ved kall method=$method mot url=$url", e)
        }
    }

    private fun readProblemDetail(e: RestClientResponseException): ProblemDetail? {
        val responseBody = e.responseBodyAsString
        return if (responseBody.contains("\"detail\"")) {
            objectMapper.readValue<ProblemDetail>(responseBody)
        } else {
            null
        }
    }

    override fun toString(): String = this::class.simpleName + " [operations=" + operations + "]"
}
