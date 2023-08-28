package no.nav.tilleggsstonader.libs.log

import java.util.UUID

object IdUtils {
    fun generateId(): String {
        val uuid = UUID.randomUUID()
        return java.lang.Long.toHexString(uuid.mostSignificantBits) + java.lang.Long.toHexString(uuid.leastSignificantBits)
    }
}
