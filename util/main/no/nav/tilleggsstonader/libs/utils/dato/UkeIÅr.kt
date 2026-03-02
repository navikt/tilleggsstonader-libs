package no.nav.tilleggsstonader.libs.utils.dato

import no.nav.tilleggsstonader.kontrakter.felles.Periode
import no.nav.tilleggsstonader.kontrakter.felles.alleDatoer
import java.time.LocalDate

data class UkeIÅr(
    val ukenummer: Int,
    val år: Int,
)

fun Periode<LocalDate>.alleDatoerGruppertPåUke() =
    alleDatoer()
        .groupBy { UkeIÅr(it.ukenummer(), it.årForUke()) }
