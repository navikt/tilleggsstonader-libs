package no.nav.tilleggsstonader.libs.unleash

private val unleashService: UnleashService =
    DefaultUnleashService(
        apiUrl = "https://tilleggsstonader-unleash-api.nav.cloud.nais.io",
        apiToken = System.getenv("UNLEASH_API_TOKEN"),
        appName = "tilleggsstonader-sak",
        strategies = emptyList(),
    )

fun main() {
    println(unleashService.getVariant(Toggle.ROUTING_TILSYN_BARN))
    println(unleashService.isEnabled(Toggle.ROUTING_TILSYN_BARN))
}

enum class Toggle(
    override val toggleId: String,
) : ToggleId {
    ROUTING_TILSYN_BARN("sak.soknad-routing.tilsyn-barn"),
}
