package no.nav.tilleggsstonader.libs.unleash

import io.getunleash.Variant
import org.springframework.beans.factory.DisposableBean

interface UnleashService : DisposableBean {
    fun isEnabled(toggle: ToggleId): Boolean {
        return isEnabled(toggle, false)
    }

    fun isEnabled(
        toggle: ToggleId,
        properties: Map<String, String>,
    ): Boolean

    fun isEnabled(
        toggle: ToggleId,
        defaultValue: Boolean,
    ): Boolean

    fun getVariant(toggle: ToggleId, defaultValue: Variant): Variant

    fun getVariant(toggle: ToggleId): Variant {
        return getVariant(toggle, Variant.DISABLED_VARIANT)
    }
}
