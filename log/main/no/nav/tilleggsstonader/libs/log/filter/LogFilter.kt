package no.nav.tilleggsstonader.libs.log.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import no.nav.tilleggsstonader.libs.log.IdUtils
import no.nav.tilleggsstonader.libs.log.NavHttpHeaders
import no.nav.tilleggsstonader.libs.log.mdc.MDCConstants.MDC_CALL_ID
import no.nav.tilleggsstonader.libs.log.mdc.MDCConstants.MDC_CONSUMER_ID
import no.nav.tilleggsstonader.libs.log.mdc.MDCConstants.MDC_REQUEST_ID
import no.nav.tilleggsstonader.libs.log.mdc.MDCConstants.MDC_USER_ID
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.io.EOFException

/**
 * @param sporBruker settes hvis vi skal sette cookie som sporer brukeren for å hjelpe ved feilsøing
 */
class LogFilter : HttpFilter() {

    override fun doFilter(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val consumerId = request.getHeader(NavHttpHeaders.NAV_CONSUMER_ID.asString())
        val callId = resolveCallId(request)
        MDC.put(MDC_CALL_ID, callId)
        MDC.put(MDC_CONSUMER_ID, consumerId)
        MDC.put(MDC_REQUEST_ID, resolveRequestId(request))
        response.setHeader(NavHttpHeaders.NAV_CALL_ID.asString(), callId)

        try {
            filterWithErrorHandling(request, response, filterChain)
        } finally {
            MDC.remove(MDC_CALL_ID)
            MDC.remove(MDC_USER_ID)
            MDC.remove(MDC_CONSUMER_ID)
            MDC.remove(MDC_REQUEST_ID)
        }
    }

    private fun filterWithErrorHandling(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (e: Exception) {
            if (e is EOFException) {
                LOG.warn(e.message, e)
            } else {
                LOG.error(e.message, e)
                if (response.isCommitted) {
                    LOG.error("failed with status={}", response.status)
                    throw e
                }
                response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            }
        }
    }

    private fun resolveCallId(request: HttpServletRequest): String {
        return NAV_CALL_ID_HEADER_NAMES
            .mapNotNull { request.getHeader(it) }
            .firstOrNull { it.isNotEmpty() }
            ?: IdUtils.generateId()
    }

    private fun resolveRequestId(request: HttpServletRequest): String {
        return NAV_REQUEST_ID_HEADER_NAMES
            .mapNotNull { request.getHeader(it) }
            .firstOrNull { it.isNotEmpty() }
            ?: IdUtils.generateId()
    }

    companion object {
        // there is no consensus in NAV about header-names for correlation ids, so we support 'em all!
        // https://nav-it.slack.com/archives/C9UQ16AH4/p1538488785000100
        private val NAV_CALL_ID_HEADER_NAMES =
            listOf(
                NavHttpHeaders.NAV_CALL_ID.asString(),
                "Nav-CallId",
                "Nav-Callid",
                "X-Correlation-Id",
            )

        private val NAV_REQUEST_ID_HEADER_NAMES =
            listOf(
                "X_Request_Id",
                "X-Request-Id",
            )
        private val LOG = LoggerFactory.getLogger(LogFilter::class.java)
    }
}
