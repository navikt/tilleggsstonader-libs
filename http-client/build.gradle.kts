val tokenSupportVersion = "3.1.3"
val wiremockVersion = "2.34.0"

plugins {
    kotlin("plugin.spring") version "1.9.10"
}

dependencies {
    implementation(project(":log"))

    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-web")

    implementation("org.apache.httpcomponents.client5:httpclient5")

    // Token support security
    implementation("no.nav.security:token-client-core:$tokenSupportVersion")
    implementation("no.nav.security:token-client-spring:$tokenSupportVersion")
    implementation("no.nav.security:token-validation-spring:$tokenSupportVersion")

    testImplementation("com.github.tomakehurst:wiremock-jre8-standalone:$wiremockVersion")
}
