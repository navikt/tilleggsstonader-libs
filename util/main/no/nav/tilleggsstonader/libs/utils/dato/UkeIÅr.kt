package no.nav.tilleggsstonader.libs.utils.dato

import no.nav.tilleggsstonader.kontrakter.felles.Periode
import no.nav.tilleggsstonader.kontrakter.felles.alleDatoer
import java.time.LocalDate

data class UkeIÅr(
    val ukenummer: Int,
    val år: Int,
) : Comparable<UkeIÅr> {
    override fun compareTo(other: UkeIÅr): Int =
        when {
            this.år != other.år -> this.år - other.år
            else -> this.ukenummer - other.ukenummer
        }

    override fun toString(): String = "$år-$ukenummer"

    companion object {
        fun fraString(s: String): UkeIÅr {
            val split = s.split("-")
            require(split.size == 2) { "UkeIÅr må være på formatet år-ukenummer" }

            val år = requireNotNull(split[0].toIntOrNull()) { "Ugyldig år i UkeIÅr: $s" }
            val uke = requireNotNull(split[1].toIntOrNull()) { "Ugyldig ukenummer i UkeIÅr: $s" }

            return UkeIÅr(uke, år)
        }
    }
}

fun Periode<LocalDate>.alleDatoerGruppertPåUke() =
    alleDatoer()
        .groupBy { it.tilUkeIÅr() }

fun LocalDate.tilUkeIÅr() = UkeIÅr(this.ukenummer(), this.årForUke())
