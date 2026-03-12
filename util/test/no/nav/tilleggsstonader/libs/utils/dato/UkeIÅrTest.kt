package no.nav.tilleggsstonader.libs.utils.dato

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

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
}
