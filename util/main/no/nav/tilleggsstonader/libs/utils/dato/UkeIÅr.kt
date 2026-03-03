package no.nav.tilleggsstonader.libs.utils.dato

import no.nav.tilleggsstonader.kontrakter.felles.Periode
import no.nav.tilleggsstonader.kontrakter.felles.alleDatoer
import java.time.LocalDate

data class UkeIÅr(
    val ukenummer: Int,
    val år: Int,
): Comparable<UkeIÅr> {
    override fun compareTo(other: UkeIÅr): Int {
        return when {
            this.år != other.år -> this.år - other.år
            else -> this.ukenummer - other.ukenummer
        }
    }
}

fun Periode<LocalDate>.alleDatoerGruppertPåUke() =
    alleDatoer()
        .groupBy { it.tilUkeIÅr() }

fun LocalDate.tilUkeIÅr() = UkeIÅr(this.ukenummer(), this.årForUke())