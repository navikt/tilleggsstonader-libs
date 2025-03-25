package no.nav.tilleggsstonader.libs.unleash

import io.getunleash.DefaultUnleash
import io.getunleash.UnleashContext
import io.getunleash.strategy.Strategy
import io.getunleash.util.UnleashConfig
import io.getunleash.variant.Variant

internal class DefaultUnleashService(
    apiUrl: String,
    apiToken: String,
    appName: String,
    userIdProvider: UserIdProvider?,
    strategies: List<Strategy>,
) : UnleashService {
    private val defaultUnleash: DefaultUnleash =
        DefaultUnleash(
            UnleashConfig
                .builder()
                .appName(appName)
                .unleashAPI("$apiUrl/api")
                .apiKey(apiToken)
                .unleashContextProvider {
                    val builder = UnleashContext.builder().appName(appName)
                    if (userIdProvider != null) {
                        builder.userId(userIdProvider.userId())
                    }
                    builder.build()
                }.build(),
            *strategies.toTypedArray(),
        )

    override fun isEnabled(
        toggle: ToggleId,
        defaultValue: Boolean,
    ): Boolean = defaultUnleash.isEnabled(toggle.toggleId, defaultValue)

    override fun isEnabled(
        toggle: ToggleId,
        properties: Map<String, String>,
    ): Boolean {
        val builder = UnleashContext.builder()
        properties.forEach { property -> builder.addProperty(property.key, property.value) }
        return defaultUnleash.isEnabled(toggle.toggleId, builder.build())
    }

    override fun getVariant(
        toggle: ToggleId,
        defaultValue: Variant,
    ): Variant = defaultUnleash.getVariant(toggle.toggleId, defaultValue)

    override fun destroy() {
        // Spring trigger denne ved shutdown. Gjøres for å unngå at unleash fortsetter å gjøre kall ut
        defaultUnleash.shutdown()
    }
}
