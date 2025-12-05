plugins {
    kotlin("plugin.spring") version "2.2.21"
}

dependencies {
    implementation(project(":log"))

    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.slf4j:slf4j-api")

}

tasks.sourcesJar {
    duplicatesStrategy = DuplicatesStrategy.WARN
}
