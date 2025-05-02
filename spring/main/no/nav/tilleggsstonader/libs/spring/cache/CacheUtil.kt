package no.nav.tilleggsstonader.libs.spring.cache

import org.springframework.cache.Cache
import org.springframework.cache.CacheManager

/**
 * Forventer treff, skal ikke brukes hvis en cache inneholder nullverdi
 * this.getCache(cache) burde aldri kunne returnere null, då den lager en cache hvis den ikke finnes fra før
 */
fun <K : Any, T> CacheManager.getValue(
    cache: String,
    key: K,
    valueLoader: () -> T,
): T = this.getNullable(cache, key, valueLoader) ?: error("Finner ikke cache for cache=$cache key=$key")

/**
 * Kan inneholde
 * this.getCache(cache) burde aldri kunne returnere null, då den lager en cache hvis den ikke finnes fra før
 */
fun <K : Any, T> CacheManager.getNullable(
    cache: String,
    key: K,
    valueLoader: () -> T?,
): T? = getCacheOrThrow(cache).getOrPut(key, valueLoader)

fun CacheManager.getCacheOrThrow(cache: String) = this.getCache(cache) ?: error("Finner ikke cache=$cache")

/**
 * Dette er en workaround for når man bruker CacheManager direkte. Om det kastes et exception i valueLoader-funksjonen,
 * blir exceptionet pakket inn i et `Cache.ValueRetrievalException`, noe som er uheldig om man forventer at
 * det skal bli propagert opp til feks et ControllerAdvice
 */
@Suppress("UNCHECKED_CAST")
private fun <K : Any, T> Cache.getOrPut(
    key: K,
    valueLoader: () -> T,
): T? = (get(key)?.get() as T?) ?: valueLoader().also { put(key, it) }

/**
 * Henter tidligere cachet verdier, og henter ucachet verdier med [valueLoader]
 */
@Suppress("UNCHECKED_CAST", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
fun <T : Any, R> CacheManager.getCachedOrLoad(
    cacheName: String,
    values: Collection<T>,
    valueLoader: (Collection<T>) -> Map<T, R>,
): Map<T, R> {
    val cache = this.getCacheOrThrow(cacheName)
    val previousValues: List<Pair<T, R?>> = values.distinct().map { it to cache.get(it)?.get() as R? }.toList()

    val cachedValues = previousValues.mapNotNull { if (it.second == null) null else it }.toMap() as Map<T, R>
    val valuesWithoutCache = previousValues.filter { it.second == null }.map { it.first }
    val loadedValues: Map<T, R> =
        valuesWithoutCache
            .takeIf { it.isNotEmpty() }
            ?.let { valueLoader(it) } ?: emptyMap()
    loadedValues.forEach { cache.put(it.key, it.value) }

    return cachedValues + loadedValues
}
