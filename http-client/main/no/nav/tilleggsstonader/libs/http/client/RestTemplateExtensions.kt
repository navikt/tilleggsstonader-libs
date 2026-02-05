package no.nav.tilleggsstonader.libs.http.client

import no.nav.tilleggsstonader.kontrakter.felles.JsonMapperProvider.jsonMapper
import no.nav.tilleggsstonader.libs.log.SecureLogger.secureLogger
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ProblemDetail
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import tools.jackson.module.kotlin.readValue
import java.net.URI

inline fun <reified T : Any> RestTemplate.getForEntity(
    uri: String,
    httpHeaders: HttpHeaders? = null,
    uriVariables: Map<String, *> = emptyMap<String, String>(),
): T = execute(uri, HttpMethod.GET, HttpEntity(null, httpHeaders), uriVariables)

inline fun <reified T : Any> RestTemplate.postForEntity(
    uri: String,
    payload: Any,
    httpHeaders: HttpHeaders? = null,
    uriVariables: Map<String, *> = emptyMap<String, String>(),
): T = execute(uri, HttpMethod.POST, HttpEntity(payload, httpHeaders), uriVariables)

inline fun <reified T : Any> RestTemplate.postForEntityNullable(
    uri: String,
    payload: Any,
    httpHeaders: HttpHeaders? = null,
    uriVariables: Map<String, *> = emptyMap<String, String>(),
): T? = executeNullable(uri, HttpMethod.POST, HttpEntity(payload, httpHeaders), uriVariables)

inline fun <reified T : Any> RestTemplate.putForEntityNullable(
    uri: String,
    payload: Any,
    httpHeaders: HttpHeaders? = null,
    uriVariables: Map<String, *> = emptyMap<String, String>(),
): T? = executeNullable(uri, HttpMethod.PUT, HttpEntity(payload, httpHeaders), uriVariables)

inline fun <reified T : Any> RestTemplate.putForEntity(
    uri: String,
    payload: Any,
    httpHeaders: HttpHeaders? = null,
    uriVariables: Map<String, *> = emptyMap<String, String>(),
): T = execute(uri, HttpMethod.PUT, HttpEntity(payload, httpHeaders), uriVariables)

inline fun <reified T : Any> RestTemplate.patchForEntity(
    uri: String,
    payload: Any,
    httpHeaders: HttpHeaders? = null,
    uriVariables: Map<String, *> = emptyMap<String, String>(),
): T = execute(uri, HttpMethod.PATCH, HttpEntity(payload, httpHeaders), uriVariables)

inline fun <reified T : Any> RestTemplate.deleteForEntity(
    uri: String,
    payload: Any? = null,
    httpHeaders: HttpHeaders? = null,
    uriVariables: Map<String, *> = emptyMap<String, String>(),
): T = execute(uri, HttpMethod.DELETE, HttpEntity(payload, httpHeaders), uriVariables)

inline fun <reified T : Any> RestTemplate.execute(
    urlTemplate: String,
    method: HttpMethod,
    entity: HttpEntity<*>,
    uriVariables: Map<String, *> = emptyMap<String, String>(),
): T =
    executeNullable<T>(urlTemplate, method, entity, uriVariables)
        ?: error("Mangler body")

inline fun <reified T : Any> RestTemplate.executeNullable(
    urlTemplate: String,
    method: HttpMethod,
    entity: HttpEntity<*>,
    uriVariables: Map<String, *>,
): T? {
    try {
        return exchange<T>(urlTemplate, method, entity, uriVariables).body
    } catch (e: RestClientResponseException) {
        val url = expand(urlTemplate, uriVariables)
        secureLogger.warn("Feil ved kall method=$method mot url=$url", e)
        val problemDetail = readProblemDetail(e)
        if (problemDetail != null) {
            throw ProblemDetailException(problemDetail, e)
        }
        throw e
    } catch (e: Exception) {
        val url = expand(urlTemplate, uriVariables)
        secureLogger.warn("Feil ved kall method=$method mot url=$url", e)
        throw RuntimeException("Feil ved kall method=$method mot url=$url", e)
    }
}

fun RestTemplate.expand(
    urlTemplate: String,
    uriVariables: Map<String, *>,
): URI = uriTemplateHandler.expand(urlTemplate, uriVariables)

fun readProblemDetail(e: RestClientResponseException): ProblemDetail? {
    val responseBody = e.responseBodyAsString
    return if (responseBody.contains("\"detail\"")) {
        jsonMapper.readValue<ProblemDetail>(responseBody)
    } else {
        null
    }
}
