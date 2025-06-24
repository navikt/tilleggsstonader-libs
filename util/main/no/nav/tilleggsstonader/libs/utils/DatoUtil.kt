package no.nav.tilleggsstonader.libs.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val DATE_FORMAT_NORSK = DateTimeFormatter.ofPattern("dd.MM.yyyy")
val GOSYS_DATE_TIME = DateTimeFormatter.ofPattern("dd.MM.yyyy' 'HH:mm")

fun LocalDate.norskFormat() = this.format(DATE_FORMAT_NORSK)

fun LocalDateTime.medGosysTid(): String = this.format(GOSYS_DATE_TIME)
