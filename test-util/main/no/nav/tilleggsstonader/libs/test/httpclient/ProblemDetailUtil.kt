package no.nav.tilleggsstonader.libs.test.httpclient

import no.nav.tilleggsstonader.kontrakter.felles.ObjectMapperProvider.jsonMapper
import no.nav.tilleggsstonader.libs.http.client.ProblemDetailException
import no.nav.tilleggsstonader.libs.test.assertions.catchThrowableOfType
import org.springframework.http.ProblemDetail
import org.springframework.web.client.RestClientResponseException
import tools.jackson.module.kotlin.readValue

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
            jsonMapper.readValue<ProblemDetail>(responseBody)
        } else {
            null
        }
    }
}
