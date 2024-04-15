package no.nav.tilleggsstonader.libs.http.client

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.tilleggsstonader.kontrakter.felles.ObjectMapperProvider.objectMapper
import no.nav.tilleggsstonader.libs.log.SecureLogger.secureLogger
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ProblemDetail
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.net.URI

/**
 * Abstract klasse for å kalle rest-tjenester med metrics og utpakking av ev. body.
 */
abstract class AbstractRestClient(val restTemplate: RestTemplate) {

    protected val log: Logger = LoggerFactory.getLogger(javaClass)

    inline fun <reified T : Any> getForEntity(
        uri: String,
        httpHeaders: HttpHeaders? = null,
        uriVariables: Map<String, *> = emptyMap<String, String>(),
    ): T = execute(uri, HttpMethod.GET, HttpEntity(null, httpHeaders), uriVariables)

    inline fun <reified T : Any> postForEntity(
        uri: String,
        payload: Any,
        httpHeaders: HttpHeaders? = null,
        uriVariables: Map<String, *> = emptyMap<String, String>(),
    ): T = execute(uri, HttpMethod.POST, HttpEntity(payload, httpHeaders), uriVariables)

    inline fun <reified T : Any> postForEntityNullable(
        uri: String,
        payload: Any,
        httpHeaders: HttpHeaders? = null,
        uriVariables: Map<String, *> = emptyMap<String, String>(),
    ): T? = executeNullable(uri, HttpMethod.POST, HttpEntity(payload, httpHeaders), uriVariables)

    inline fun <reified T : Any> putForEntityNullable(
        uri: String,
        payload: Any,
        httpHeaders: HttpHeaders? = null,
        uriVariables: Map<String, *> = emptyMap<String, String>(),
    ): T? = executeNullable(uri, HttpMethod.PUT, HttpEntity(payload, httpHeaders), uriVariables)

    inline fun <reified T : Any> putForEntity(
        uri: String,
        payload: Any,
        httpHeaders: HttpHeaders? = null,
        uriVariables: Map<String, *> = emptyMap<String, String>(),
    ): T = execute(uri, HttpMethod.PUT, HttpEntity(payload, httpHeaders), uriVariables)

    inline fun <reified T : Any> patchForEntity(
        uri: String,
        payload: Any,
        httpHeaders: HttpHeaders? = null,
        uriVariables: Map<String, *> = emptyMap<String, String>(),
    ): T = execute(uri, HttpMethod.PATCH, HttpEntity(payload, httpHeaders), uriVariables)

    inline fun <reified T : Any> deleteForEntity(
        uri: String,
        payload: Any? = null,
        httpHeaders: HttpHeaders? = null,
        uriVariables: Map<String, *> = emptyMap<String, String>(),
    ): T = execute(uri, HttpMethod.DELETE, HttpEntity(payload, httpHeaders), uriVariables)

    inline fun <reified T : Any> execute(
        urlTemplate: String,
        method: HttpMethod,
        entity: HttpEntity<*>,
        uriVariables: Map<String, *> = emptyMap<String, String>(),
    ): T {
        return executeNullable<T>(urlTemplate, method, entity, uriVariables)
            ?: error("Mangler body")
    }

    inline fun <reified T : Any> executeNullable(
        urlTemplate: String,
        method: HttpMethod,
        entity: HttpEntity<*>,
        uriVariables: Map<String, *>,
    ): T? {
        try {
            return restTemplate.exchange<T>(urlTemplate, method, entity, uriVariables).body
        } catch (e: RestClientResponseException) {
            val url = expand(urlTemplate, uriVariables)
            secureLogger.warn("Feil ved kall method=$method mot url=$url", e)
            readProblemDetail(e)?.let { throw ProblemDetailException(it, e) } ?: throw e
        } catch (e: Exception) {
            val url = expand(urlTemplate, uriVariables)
            secureLogger.warn("Feil ved kall method=$method mot url=$url", e)
            throw RuntimeException("Feil ved kall method=$method mot url=$url", e)
        }
    }

    fun expand(urlTemplate: String, uriVariables: Map<String, *>): URI {
        return restTemplate.uriTemplateHandler.expand(urlTemplate, uriVariables)
    }

    fun readProblemDetail(e: RestClientResponseException): ProblemDetail? {
        val responseBody = e.responseBodyAsString
        return if (responseBody.contains("\"detail\"")) {
            objectMapper.readValue<ProblemDetail>(responseBody)
        } else {
            null
        }
    }

    override fun toString(): String = this::class.simpleName + " [operations=" + restTemplate + "]"
}
