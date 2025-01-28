package no.nav.tilleggsstonader.libs.sikkerhet

import no.nav.security.token.support.core.context.TokenValidationContext
import no.nav.security.token.support.core.exceptions.JwtTokenMissingException
import no.nav.security.token.support.core.exceptions.JwtTokenValidatorException
import no.nav.security.token.support.core.jwt.JwtTokenClaims
import no.nav.security.token.support.spring.SpringTokenValidationContextHolder
import org.springframework.web.context.request.RequestContextHolder

object EksternBrukerUtils {
    const val ISSUER_TOKENX = "tokenx"

    private val TOKEN_VALIDATION_CONTEXT_ATTRIBUTE = SpringTokenValidationContextHolder::class.java.name

    private val FNR_REGEX = """[0-9]{11}""".toRegex()

    fun hentFnrFraToken(): String {
        val claims = claims()
        val fnr =
            claims.getStringClaim("pid")
                ?: throw JwtTokenValidatorException("Finner ikke pid på token")
        if (!FNR_REGEX.matches(fnr)) {
            error("$fnr er ikke gyldig fnr")
        }
        return fnr
    }

    fun personIdentErLikInnloggetBruker(personIdent: String): Boolean = personIdent == hentFnrFraToken()

    fun getBearerTokenForLoggedInUser(): String =
        getFromContext { validationContext, issuer ->
            validationContext.getJwtToken(issuer)?.encodedToken ?: throw JwtTokenMissingException()
        }

    private fun claims(): JwtTokenClaims =
        getFromContext { validationContext, issuer ->
            validationContext.getClaims(issuer)
        }

    private fun <T> getFromContext(fn: (TokenValidationContext, String) -> T): T {
        val validationContext = getTokenValidationContext()
        return when {
            validationContext.hasTokenFor(ISSUER_TOKENX) -> fn.invoke(validationContext, ISSUER_TOKENX)
            else -> error("Finner ikke token for ekstern bruker - issuers=${validationContext.issuers}")
        }
    }

    private fun getTokenValidationContext(): TokenValidationContext =
        RequestContextHolder
            .currentRequestAttributes()
            .getAttribute(TOKEN_VALIDATION_CONTEXT_ATTRIBUTE, 0) as TokenValidationContext
}
