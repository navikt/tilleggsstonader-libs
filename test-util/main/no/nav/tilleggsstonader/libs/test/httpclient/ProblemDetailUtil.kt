package no.nav.tilleggsstonader.libs.test.httpclient

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.tilleggsstonader.kontrakter.felles.ObjectMapperProvider.objectMapper
import no.nav.tilleggsstonader.libs.http.client.ProblemDetailException
import no.nav.tilleggsstonader.libs.test.assertions.catchThrowableOfType
import org.springframework.http.ProblemDetail
import org.springframework.web.client.RestClientResponseException

object ProblemDetailUtil {
    fun catchProblemDetailException(fn: () -> Unit): ProblemDetailException =
        catchThrowableOfType<ProblemDetailException> {
            execWithErrorHandler(fn)
        }

    fun catchHttpException(fn: () -> Unit): RestClientResponseException =
        catchThrowableOfType<RestClientResponseException> {
            execWithErrorHandler(fn)
        }

    fun <T> execWithErrorHandler(fn: () -> T): T {
        try {
            return fn()
        } catch (e: RestClientResponseException) {
            readProblemDetail(e)?.let { throw ProblemDetailException(it, e) }
                ?: throw e
        }
    }

    private fun readProblemDetail(e: RestClientResponseException): ProblemDetail? {
        val responseBody = e.responseBodyAsString // "detail"
        return if (responseBody.contains("\"detail\"")) {
            objectMapper.readValue<ProblemDetail>(responseBody)
        } else {
            null
        }
    }
}
