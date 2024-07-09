package no.nav.tilleggsstonader.libs.http.client

import org.springframework.web.client.RestTemplate

abstract class AbstractPingableRestClient(restTemplate: RestTemplate) : AbstractRestClient(restTemplate), Pingable {
    abstract val pingUri: String

    override fun ping() {
        getForEntity<String>(pingUri)
    }

    override fun toString(): String {
        return this::class.simpleName + "[restTemplate=" + restTemplate + "]"
    }
}
