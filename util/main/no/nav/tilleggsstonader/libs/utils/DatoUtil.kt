package no.nav.tilleggsstonader.libs.utils

import java.time.LocalDateTime
import java.time.ZoneId

val ZONE_ID_OSLO: ZoneId = ZoneId.of("Europe/Oslo")

fun osloNow(): LocalDateTime = LocalDateTime.now(ZONE_ID_OSLO)
