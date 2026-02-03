val tokenSupportVersion = "6.0.0"
val wiremockVersion = "3.0.1"
val tilleggsstønaderKontrakterVersion = "2026.01.30-09.17.cce4015d2343-dev"

plugins {
    kotlin("plugin.spring") version "2.3.0"
}

dependencies {
    implementation(project(":log"))

    implementation("org.springframework:spring-context")
    implementation("org.springframework.boot:spring-boot-restclient")
    implementation("org.springframework.boot:spring-boot-starter-web")


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
