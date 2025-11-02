val tokenSupportVersion = "5.0.39"

plugins {
    kotlin("plugin.spring") version "2.2.21"
}

dependencies {
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-web")

    api("no.nav.security:token-client-core:$tokenSupportVersion")
    api("no.nav.security:token-validation-spring:$tokenSupportVersion")
}

tasks.sourcesJar {
    duplicatesStrategy = DuplicatesStrategy.WARN
}
