package no.nav.tilleggsstonader.libs.unleash

interface UserIdProvider {
    fun userId(): String?
}
