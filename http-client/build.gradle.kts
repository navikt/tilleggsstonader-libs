val tokenSupportVersion = "3.1.7"
val wiremockVersion = "3.0.1"
val tilleggsstønaderKontrakterVersion = "2023.10.18-14.58.72cf06e4d584"

plugins {
    kotlin("plugin.spring") version "1.9.10"
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
