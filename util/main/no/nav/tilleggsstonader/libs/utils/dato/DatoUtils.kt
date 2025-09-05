package no.nav.tilleggsstonader.libs.utils.dato

import java.time.LocalDate

infix fun Int.januar(år: Int) = LocalDate.of(år, 1, this)

infix fun Int.februar(år: Int) = LocalDate.of(år, 2, this)

infix fun Int.mars(år: Int) = LocalDate.of(år, 3, this)

infix fun Int.april(år: Int) = LocalDate.of(år, 4, this)

infix fun Int.mai(år: Int) = LocalDate.of(år, 5, this)

infix fun Int.juni(år: Int) = LocalDate.of(år, 6, this)

infix fun Int.juli(år: Int) = LocalDate.of(år, 7, this)

infix fun Int.august(år: Int) = LocalDate.of(år, 8, this)

infix fun Int.september(år: Int) = LocalDate.of(år, 9, this)

infix fun Int.oktober(år: Int) = LocalDate.of(år, 10, this)

infix fun Int.november(år: Int) = LocalDate.of(år, 11, this)

infix fun Int.desember(år: Int) = LocalDate.of(år, 12, this)
