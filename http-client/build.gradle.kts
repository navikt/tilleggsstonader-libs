val tokenSupportVersion = "3.2.0"
val wiremockVersion = "2.35.0"
val tilleggsstønaderKontrakterVersion = "2023.12.04-12.03.40d61264fa02"

plugins {
    kotlin("plugin.spring") version "1.9.21"
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
