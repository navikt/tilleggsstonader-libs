val tokenSupportVersion = "5.0.34"
val wiremockVersion = "3.0.1"
val tilleggsstønaderKontrakterVersion = "2025.08.04-12.40.fe9f706295de"

plugins {
    kotlin("plugin.spring") version "2.2.0"
}

dependencies {
    implementation(project(":log"))

    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-web")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("no.nav.tilleggsstonader.kontrakter:kontrakter-felles:$tilleggsstønaderKontrakterVersion")

    api("org.apache.httpcomponents.client5:httpclient5")

    // Token support security
    api("no.nav.security:token-client-core:$tokenSupportVersion")
    api("no.nav.security:token-client-spring:$tokenSupportVersion")
    api("no.nav.security:token-validation-spring:$tokenSupportVersion")

    testImplementation("com.github.tomakehurst:wiremock-jre8-standalone:$wiremockVersion")
    testImplementation("org.springframework.boot:spring-boot-test")
}

tasks.sourcesJar {
    duplicatesStrategy = DuplicatesStrategy.WARN
}
