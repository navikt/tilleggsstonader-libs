package no.nav.tilleggsstonader.libs.feil

import org.springframework.http.HttpStatus
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Brukes primært som feil som er årsaket av en saksbehandler, som logges som info, og feil blir logge i vanlig logg
 */
class ApiFeil internal constructor(
    val feil: String,
    val httpStatus: HttpStatus,
) : RuntimeException(feil)

/**
 * Generelle feil. Logger som Error.
 * @param sensitivFeilmelding logges kun i securelog
 */
class Feil internal constructor(
    message: String,
    val sensitivFeilmelding: String? = null,
    val httpStatus: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
    throwable: Throwable? = null,
) : RuntimeException(message, throwable)

fun feil(
    message: String,
    sensitivFeilmelding: String? = null,
    httpStatus: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
): Nothing = throw Feil(message = message, sensitivFeilmelding = sensitivFeilmelding, httpStatus = httpStatus)

@OptIn(ExperimentalContracts::class)
inline fun feilHvis(
    boolean: Boolean,
    httpStatus: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
    noinline sensitivFeilmelding: (() -> String)? = null,
    lazyMessage: () -> String,
) {
    contract {
        returns() implies !boolean
    }
    if (boolean) {
        feil(
            message = lazyMessage(),
            sensitivFeilmelding = sensitivFeilmelding?.invoke(),
            httpStatus = httpStatus,
        )
    }
}

fun brukerfeil(
    feil: String,
    httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
): Nothing = throw ApiFeil(feil = feil, httpStatus = httpStatus)

@OptIn(ExperimentalContracts::class)
inline fun brukerfeilHvis(
    boolean: Boolean,
    httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    lazyMessage: () -> String,
) {
    contract {
        returns() implies !boolean
    }
    if (boolean) {
        brukerfeil(
            feil = lazyMessage(),
            httpStatus = httpStatus,
        )
    }
}

inline fun feilHvisIkke(
    boolean: Boolean,
    httpStatus: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
    noinline sensitivFeilmelding: (() -> String)? = null,
    lazyMessage: () -> String,
) {
    feilHvis(!boolean, httpStatus, sensitivFeilmelding) { lazyMessage() }
}

inline fun brukerfeilHvisIkke(
    boolean: Boolean,
    httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    lazyMessage: () -> String,
) {
    brukerfeilHvis(!boolean, httpStatus) { lazyMessage() }
}

inline fun <T> List<T>.singleEllerFeil(
    httpStatus: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
    noinline sensitivFeilmelding: (() -> String)? = null,
    lazyMessage: () -> String,
): T =
    when (size) {
        1 -> this[0]
        else ->
            feil(
                message = lazyMessage(),
                sensitivFeilmelding = sensitivFeilmelding?.invoke() ?: lazyMessage(),
                httpStatus = httpStatus,
            )
    }

inline fun <T> Iterable<T>.singleEllerFeil(
    predicate: (T) -> Boolean,
    httpStatus: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
    noinline sensitivFeilmelding: (() -> String)? = null,
    lazyMessage: () -> String,
): T = filter(predicate).singleEllerFeil(httpStatus, sensitivFeilmelding, lazyMessage)

class ManglerTilgang(
    val melding: String,
    val frontendFeilmelding: String,
) : RuntimeException(melding)
