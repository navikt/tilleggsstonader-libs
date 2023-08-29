package no.nav.tilleggsstonader.libs.utils

import no.nav.tilleggsstonader.libs.test.fnr.FnrGenerator
import no.nav.tilleggsstonader.libs.utils.fnr.Fødselsnummer
import org.junit.jupiter.api.Test

class FnrGeneratorTest {

    @Test
    fun `generer genererer kun gyldige fødselsnumre`() {
        repeat(10000) {
            Fødselsnummer(FnrGenerator.generer())
        }
    }
}
