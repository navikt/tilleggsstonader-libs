package no.nav.tilleggsstonader.libs.utils

object CollectionUtil {
    inline fun <reified T> List<T>.singleOrNullOrError(): T? {
        require(this.size < 2) {
            "Forventet ikke fler enn 1 element av typen ${T::class.simpleName}"
        }
        return firstOrNull()
    }
}
