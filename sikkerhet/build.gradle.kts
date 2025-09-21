val tokenSupportVersion = "5.0.37"

plugins {
    kotlin("plugin.spring") version "2.2.20"
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
