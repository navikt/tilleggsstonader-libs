val tokenSupportVersion = "5.0.14"
val wiremockVersion = "3.0.1"
val tilleggsstønaderKontrakterVersion = "2024.12.20-14.17.472e1112c9fb"

plugins {
    kotlin("plugin.spring") version "2.1.0"
}

dependencies {
    implementation(project(":log"))

    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-web")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("no.nav.tilleggsstonader.kontrakter:tilleggsstonader-kontrakter:$tilleggsstønaderKontrakterVersion")

    api("org.apache.httpcomponents.client5:httpclient5")

    // Token support security
    api("no.nav.security:token-client-core:$tokenSupportVersion")
    api("no.nav.security:token-client-spring:$tokenSupportVersion")
    api("no.nav.security:token-validation-spring:$tokenSupportVersion")

    testImplementation("com.github.tomakehurst:wiremock-jre8-standalone:$wiremockVersion")
}

tasks.sourcesJar {
    duplicatesStrategy = DuplicatesStrategy.WARN
}
