val tokenSupportVersion = "5.0.5"

plugins {
    kotlin("plugin.spring") version "2.0.20"
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
