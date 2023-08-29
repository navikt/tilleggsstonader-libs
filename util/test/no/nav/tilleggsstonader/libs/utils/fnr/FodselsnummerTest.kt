package no.nav.tilleggsstonader.libs.utils.fnr

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class FodselsnummerTest {

    @Test
    internal fun `skal tillate helsyntetiske nummer fra dolly`() {
        val listeAvBrukere = listOf(
            SyntetiskBruker("15507600333", "55507608360", "Mann", LocalDate.of(1976, 10, 15)),
            SyntetiskBruker("29422059278", "69422056629", "Kvinne", LocalDate.of(2020, 2, 29)),
            SyntetiskBruker("15507600333", "55507608360", "Mann", LocalDate.of(1976, 10, 15)),
            SyntetiskBruker("29422059278", "69422056629", "Kvinne", LocalDate.of(2020, 2, 29)),
            SyntetiskBruker("05440355678", "45440356293", "Kvinne", LocalDate.of(2003, 4, 5)),
            SyntetiskBruker("12429400544", "52429405181", "Mann", LocalDate.of(1994, 2, 12)),
            SyntetiskBruker("12505209719", "52505209540", "Mann", LocalDate.of(1952, 10, 12)),
            SyntetiskBruker("21483609245", "61483601467", "Kvinne", LocalDate.of(1936, 8, 21)),
            SyntetiskBruker("17912099997", "57912075186", "Mann", LocalDate.of(2020, 11, 17)),
            SyntetiskBruker("29822099635", "69822075096", "Kvinne", LocalDate.of(2020, 2, 29)),
            SyntetiskBruker("05840399895", "45840375084", "Kvinne", LocalDate.of(2003, 4, 5)),
            SyntetiskBruker("12829499914", "52829400197", "Mann", LocalDate.of(1994, 2, 12)),
            SyntetiskBruker("12905299938", "52905200100", "Mann", LocalDate.of(1952, 10, 12)),
            SyntetiskBruker("21883649874", "61883600222", "Kvinne", LocalDate.of(1936, 8, 21)),
        )

        listeAvBrukere.forEach {
            assertThat(it.fnr).isEqualTo(Fodselsnummer(it.fnr).verdi)
                .withFailMessage { "Fødselsnummer ${it.fnr} er gyldig" }
            assertThat(it.dnr).isEqualTo(Fodselsnummer(it.dnr).verdi)
                .withFailMessage { "Dnr ${it.dnr} er gyldig" }
            assertThat(it.fødselsdato).isEqualTo(Fodselsnummer(it.fnr).fødselsdato)
                .withFailMessage { "Finner dato for ${it.fnr}" }
            assertThat(it.fødselsdato).isEqualTo(Fodselsnummer(it.dnr).fødselsdato)
                .withFailMessage { "Finner dato for ${it.dnr}" }
            assertThat(false).isEqualTo(Fodselsnummer(it.fnr).erDNummer)
                .withFailMessage { "${it.fnr} er ikke D-nummer" }
            assertThat(true).isEqualTo(Fodselsnummer(it.dnr).erDNummer)
                .withFailMessage { "${it.dnr} er D-nummer" }
        }
    }

    data class SyntetiskBruker(val fnr: String, val dnr: String, val kjønn: String, val fødselsdato: LocalDate)
}
