package no.nav.tilleggsstonader.libs.utils.dato

import java.time.LocalDate
import java.time.temporal.WeekFields

fun LocalDate.ukenummer() = get(WeekFields.ISO.weekOfWeekBasedYear())
