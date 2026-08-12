plugins {
    kotlin("plugin.spring") version "2.3.21"
}

dependencies {
    implementation(project(":log"))

    implementation("org.springframework.kafka:spring-kafka") {
        // Ekskluderer da en transitivt avhengighet i spotless-annotations har denne med dynamisk versjon som feiler bygget
        exclude(group = "ch.qos.logback", module = "logback-core")
    }
    implementation("org.slf4j:slf4j-api")

}

tasks.sourcesJar {
    duplicatesStrategy = DuplicatesStrategy.WARN
}
