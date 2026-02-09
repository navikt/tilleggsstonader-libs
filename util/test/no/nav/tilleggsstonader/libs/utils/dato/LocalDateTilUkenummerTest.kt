package no.nav.tilleggsstonader.libs.utils.dato

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LocalDateTilUkenummerTest {
    @Test
    fun `skal kunne konvertere dato til uken`() {
        assertThat((22 desember 2025).ukenummer()).isEqualTo(52)
        assertThat((29 desember 2025).ukenummer()).isEqualTo(1)
        assertThat((1 januar 2026).ukenummer()).isEqualTo(1)
        assertThat((5 januar 2026).ukenummer()).isEqualTo(2)
        assertThat((5 august 2026).ukenummer()).isEqualTo(32)
        assertThat((28 desember 2026).ukenummer()).isEqualTo(53)
        assertThat((1 januar 2027).ukenummer()).isEqualTo(53)
        assertThat((4 januar 2027).ukenummer()).isEqualTo(1)
        assertThat((27 desember 2027).ukenummer()).isEqualTo(52)
        assertThat((31 desember 2028).ukenummer()).isEqualTo(52)
    }
}
