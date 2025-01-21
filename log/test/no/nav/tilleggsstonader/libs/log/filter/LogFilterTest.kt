package no.nav.tilleggsstonader.libs.log.filter

import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import no.nav.tilleggsstonader.libs.log.NavHttpHeaders
import no.nav.tilleggsstonader.libs.log.mdc.MDCConstants
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.MDC

class LogFilterTest {

    private lateinit var httpServletRequest: HttpServletRequest
    private lateinit var httpServletResponse: HttpServletResponse
    private val logFilter = LogFilter(true)

    @BeforeEach
    fun setup() {
        httpServletRequest = mockHttpServletRequest
        httpServletResponse = mockHttpServletResponse
    }

    @Test
    fun cleanupOfMDCContext() {
        val initialContextMap = MDC.getCopyOfContextMap() ?: HashMap()

        logFilter.doFilter(
            httpServletRequest,
            httpServletResponse,
        ) { _, _ -> }

        assertThat(initialContextMap)
            .isEqualTo(MDC.getCopyOfContextMap() ?: HashMap<String, String>())
    }

    @Test
    fun addResponseHeaders() {
        logFilter.doFilter(httpServletRequest, httpServletResponse) { _, _ -> }

        assertThat(httpServletResponse.getHeader(NavHttpHeaders.NAV_CALL_ID.asString()))
            .isNotEmpty()
        assertThat(httpServletResponse.getHeader("Server")).isNull()
    }

    @Test
    fun handleExceptions() {
        logFilter.doFilter(httpServletRequest, httpServletResponse) { _, _ -> throw IllegalStateException("") }

        assertThat(httpServletResponse.status)
            .isEqualTo(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
    }

    @Test
    fun `skal ikke logge bruker hvis sporBruker=true`() {
        logFilter.doFilter(httpServletRequest, httpServletResponse) { _, _ ->
            assertThat(MDC.get(MDCConstants.MDC_USER_ID)).isNotNull()
        }
    }

    @Test
    fun `skal ikke logge bruker hvis sporBruker=false`() {
        val logFilter = LogFilter(sporBruker = false)

        logFilter.doFilter(httpServletRequest, httpServletResponse) { _, _ ->
            assertThat(MDC.get(MDCConstants.MDC_USER_ID)).isNull()
        }
    }

    private val mockHttpServletRequest = mockk<HttpServletRequest>(relaxed = true).also {
        every { it.method } returns "GET"
        every { it.requestURI } returns "/test/path"
    }

    private val mockHttpServletResponse = mockk<HttpServletResponse>(relaxed = true).also {
        val headers: MutableMap<String, String> = HashMap()
        val status = intArrayOf(0)
        every { it.status = any() } answers { status[0] = firstArg() }
        every { it.setHeader(any(), any()) } answers { headers[firstArg()] = secondArg() }
        every { it.status } answers { status[0] }
        every { it.getHeader(any()) } answers { headers[firstArg()] }
        every { it.headerNames } answers { headers.keys }
    }
}
