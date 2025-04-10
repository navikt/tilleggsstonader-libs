package no.nav.tilleggsstonader.libs.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

val ZONE_ID_OSLO: ZoneId = ZoneId.of("Europe/Oslo")

val DATE_FORMAT_NORSK = DateTimeFormatter.ofPattern("dd.MM.yyyy")
val GOSYS_DATE_TIME = DateTimeFormatter.ofPattern("dd.MM.yyyy' 'HH:mm")

fun osloNow(): LocalDateTime = LocalDateTime.now(ZONE_ID_OSLO)

fun osloDateNow(): LocalDate = LocalDate.now(ZONE_ID_OSLO)

fun LocalDate.norskFormat() = this.format(DATE_FORMAT_NORSK)

fun LocalDateTime.medGosysTid(): String = this.format(GOSYS_DATE_TIME)
