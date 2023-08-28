package no.nav.tilleggsstonader.libs.log.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.util.StopWatch

class RequestTimeFilter : HttpFilter() {

    override fun doFilter(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val timer = StopWatch()
        try {
            timer.start()
            filterChain.doFilter(request, response)
        } finally {
            timer.stop()
            log(request, response.status, timer)
        }
    }

    private fun log(request: HttpServletRequest, code: Int, timer: StopWatch) {
        if (HttpStatus.valueOf(code).isError) {
            LOG.warn("{} - {} - ({}). Dette tok {}ms", request.method, request.requestURI, code, timer.totalTimeMillis)
        } else {
            if (!shouldNotFilter(request.requestURI)) {
                LOG.info(
                    "{} - {} - ({}). Dette tok {}ms",
                    request.method,
                    request.requestURI,
                    code,
                    timer.totalTimeMillis,
                )
            }
        }
    }

    private fun shouldNotFilter(uri: String): Boolean {
        return uri.contains("/internal") || uri == "/api/ping"
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(RequestTimeFilter::class.java)
    }
}
