package no.nav.tilleggsstonader.libs.unleash

import io.getunleash.variant.Variant
import org.springframework.beans.factory.DisposableBean

interface UnleashService : DisposableBean {
    fun isEnabled(toggle: ToggleId): Boolean = isEnabled(toggle, false)

    fun isEnabled(
        toggle: ToggleId,
        properties: Map<String, String>,
    ): Boolean

    fun isEnabled(
        toggle: ToggleId,
        defaultValue: Boolean,
    ): Boolean

    fun getVariant(
        toggle: ToggleId,
        defaultValue: Variant,
    ): Variant

    fun getVariant(toggle: ToggleId): Variant = getVariant(toggle, Variant.DISABLED_VARIANT)
}
