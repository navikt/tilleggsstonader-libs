package no.nav.tilleggsstonader.libs.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

class DatoUtilKtTest {
    @Nested
    inner class NorskFormat {
        @Test
        fun `skal formatere dato`() {
            assertThat(LocalDate.of(2025, 1, 14).norskFormat()).isEqualTo("14.01.2025")
        }
    }

    @Nested
    inner class GosysFormat {
        @Test
        fun `skal formatere dato og tid`() {
            assertThat(LocalDateTime.of(2025, 1, 14, 8, 1, 1).medGosysTid()).isEqualTo("14.01.2025 08:01")
        }
    }
}
