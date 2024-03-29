package no.nav.tilleggsstonader.libs.http.client

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.client.RestClientResponseException

class ProblemDetailException(
    val detail: ProblemDetail,
    val responseException: RestClientResponseException,
    val httpStatus: HttpStatus = HttpStatus.valueOf(responseException.statusCode.value()),
) : RuntimeException(responseException)
