val tokenSupportVersion = "4.1.3"

plugins {
    kotlin("plugin.spring") version "1.9.23"
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
