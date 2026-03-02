package no.nav.tilleggsstonader.libs.utils.dato

import no.nav.tilleggsstonader.kontrakter.felles.Datoperiode
import no.nav.tilleggsstonader.kontrakter.felles.alleDatoer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AlleDatoerGruppertPåUkeTest {
    @Test
    fun `datoer blir korrekt gruppert på uke og år`() {
        val datoerGruppertPåUke = Datoperiode(1 desember 2026, 1 februar 2030).alleDatoerGruppertPåUke()

        assertThat(datoerGruppertPåUke[UkeIÅr(53, 2026)])
            .isNotNull
            .isNotEmpty
            .containsExactly(*Datoperiode(28 desember 2026, 3 januar 2027).alleDatoer().toTypedArray())

        assertThat(datoerGruppertPåUke[UkeIÅr(1, 2027)])
            .isNotNull
            .isNotEmpty
            .containsExactly(*Datoperiode(4 januar 2027, 10 januar 2027).alleDatoer().toTypedArray())

        assertThat(datoerGruppertPåUke[UkeIÅr(52, 2027)])
            .isNotNull
            .isNotEmpty
            .containsExactly(*Datoperiode(27 desember 2027, 2 januar 2028).alleDatoer().toTypedArray())

        assertThat(datoerGruppertPåUke[UkeIÅr(52, 2028)])
            .isNotNull
            .isNotEmpty
            .containsExactly(*Datoperiode(25 desember 2028, 31 desember 2028).alleDatoer().toTypedArray())

        assertThat(datoerGruppertPåUke[UkeIÅr(1, 2029)])
            .isNotNull
            .isNotEmpty
            .containsExactly(*Datoperiode(1 januar 2029, 7 januar 2029).alleDatoer().toTypedArray())
    }
}
