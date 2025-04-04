package no.nav.tilleggsstonader.libs.unleash

import io.getunleash.variant.Variant

/**
 * Dummyservice når unleash er disabled
 */
internal class DummyUnleashService : UnleashService {
    override fun isEnabled(
        toggle: ToggleId,
        properties: Map<String, String>,
    ): Boolean = isEnabled(toggle, false)

    override fun isEnabled(
        toggle: ToggleId,
        defaultValue: Boolean,
    ): Boolean = System.getenv(toggle.toggleId).run { toBoolean() }

    override fun getVariant(
        toggle: ToggleId,
        defaultValue: Variant,
    ): Variant {
        error("Støtter ikke variant i ${this.javaClass::getSimpleName}")
    }

    override fun destroy() {
        // Dummy featureToggleService trenger ikke destroy, då den ikke har en unleash å lukke
    }
}
