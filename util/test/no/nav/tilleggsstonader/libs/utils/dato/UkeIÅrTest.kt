package no.nav.tilleggsstonader.libs.utils.dato

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.DayOfWeek

class UkeIÅrTest {
    @Test
    fun `Sjekker at siste uken i 2025 er mindre enn første i 2026`() {
        assertThat(UkeIÅr(52, 2025)).isLessThan(UkeIÅr(1, 2026))
    }

    @Test
    fun `Sjekker at like uker på tvers av år fungerer`() {
        assertThat(UkeIÅr(52, 2025)).isLessThan(UkeIÅr(52, 2026))
    }

    @Test
    fun `Sjekker at samme uke og år er like`() {
        assertThat(UkeIÅr(1, 2025)).isEqualTo(UkeIÅr(1, 2025))
        assertThat(UkeIÅr(52, 2026)).isEqualTo(UkeIÅr(52, 2026))
    }

    @Test
    fun `Sjekker at lavere uke i samme år er mindre`() {
        assertThat(UkeIÅr(1, 2025)).isLessThan(UkeIÅr(2, 2025))
        assertThat(UkeIÅr(10, 2025)).isLessThan(UkeIÅr(11, 2025))
    }

    @Test
    fun `Sjekker at høyere uke i samme år er større`() {
        assertThat(UkeIÅr(2, 2025)).isGreaterThan(UkeIÅr(1, 2025))
        assertThat(UkeIÅr(11, 2025)).isGreaterThan(UkeIÅr(10, 2025))
    }

    @Test
    fun `Sjekker at lavere år alltid er mindre uansett uke`() {
        assertThat(UkeIÅr(52, 2024)).isLessThan(UkeIÅr(1, 2025))
        assertThat(UkeIÅr(1, 2024)).isLessThan(UkeIÅr(52, 2025))
    }

    @Test
    fun `Sjekker at høyere år alltid er større uansett uke`() {
        assertThat(UkeIÅr(1, 2026)).isGreaterThan(UkeIÅr(52, 2025))
        assertThat(UkeIÅr(52, 2026)).isGreaterThan(UkeIÅr(1, 2025))
    }

    @Test
    fun `Kan parse år og uke fra string`() {
        assertThat(UkeIÅr.fraString("2026-15")).isEqualTo(UkeIÅr(15, 2026))
        assertThat(UkeIÅr.fraString("2026-05")).isEqualTo(UkeIÅr(5, 2026))
    }

    @Test
    fun `Serialisering og deserialisering av UkeIÅr fører til samme objekt`() {
        val ukeIÅr = UkeIÅr.fraString("2024-24")
        assertThat(
            UkeIÅr.fraString(ukeIÅr.toString()),
        ).isEqualTo(ukeIÅr)
    }

    @Test
    fun `alleDager returnerer 7 dager for en gitt uke`() {
        val dager = UkeIÅr(1, 2025).alleDager()
        assertThat(dager).hasSize(7)
    }

    @Test
    fun `alleDager starter på mandag og slutter på søndag`() {
        val dager = UkeIÅr(1, 2025).alleDager()
        assertThat(dager.first()).isEqualTo(java.time.LocalDate.of(2024, 12, 30)) // Uke 1 2025 starter 30. des 2024
        assertThat(dager.last()).isEqualTo(java.time.LocalDate.of(2025, 1, 5))
    }

    @Test
    fun `alleDager returnerer riktige dager for en uke midt i året`() {
        val dager = UkeIÅr(19, 2026).alleDager()
        assertThat(dager).hasSize(7)
        assertThat(dager.first()).isEqualTo(java.time.LocalDate.of(2026, 5, 4))
        assertThat(dager.last()).isEqualTo(java.time.LocalDate.of(2026, 5, 10))
    }

    @Test
    fun `alleDager matcher tilUkeIÅr for alle dagene i uken`() {
        val uke = UkeIÅr(19, 2026)
        assertThat(uke.alleDager().map { it.tilUkeIÅr() }).containsOnly(uke)
    }

    @Test
    fun `forrigeUke returnerer uken før inneværende uke`() {
        assertThat(UkeIÅr(10, 2026).forrigeUke()).isEqualTo(UkeIÅr(9, 2026))
        assertThat(UkeIÅr(2, 2026).forrigeUke()).isEqualTo(UkeIÅr(1, 2026))
    }

    @Test
    fun `forrigeUke håndterer årsskifte - uke 1 gir siste uke i forrige år`() {
        // Uke 1 i 2026 -> uke 53 i 2025 finnes ikke, siste uke er 52
        assertThat(UkeIÅr(1, 2026).forrigeUke()).isEqualTo(UkeIÅr(52, 2025))
        // Uke 1 i 2015 -> 2015 har 53 uker
        assertThat(UkeIÅr(1, 2015).forrigeUke()).isEqualTo(UkeIÅr(52, 2014))
    }

    @Test
    fun `Feiler ved ugyldig format`() {
        assertThatThrownBy { UkeIÅr.fraString("2026/15") }
            .isInstanceOf(IllegalArgumentException::class.java)

        assertThatThrownBy { UkeIÅr.fraString("år-15") }
            .isInstanceOf(IllegalArgumentException::class.java)

        assertThatThrownBy { UkeIÅr.fraString("2026-uke") }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `ukedager-funksjoner returnerer korrekt dag`() {
        val uke = UkeIÅr.nå()
        assertThat(uke.mandag().dayOfWeek).isEqualTo(DayOfWeek.MONDAY)
        assertThat(uke.tirsdag().dayOfWeek).isEqualTo(DayOfWeek.TUESDAY)
        assertThat(uke.onsdag().dayOfWeek).isEqualTo(DayOfWeek.WEDNESDAY)
        assertThat(uke.torsdag().dayOfWeek).isEqualTo(DayOfWeek.THURSDAY)
        assertThat(uke.fredag().dayOfWeek).isEqualTo(DayOfWeek.FRIDAY)
        assertThat(uke.lørdag().dayOfWeek).isEqualTo(DayOfWeek.SATURDAY)
        assertThat(uke.søndag().dayOfWeek).isEqualTo(DayOfWeek.SUNDAY)
    }
}
