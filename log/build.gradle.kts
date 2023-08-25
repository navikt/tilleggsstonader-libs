plugins {
    kotlin("plugin.spring") version "1.9.10"
}

dependencies {
    implementation("org.springframework.boot:spring-boot")
    implementation("org.springframework:spring-web")
    implementation("org.slf4j:slf4j-api")
    implementation("jakarta.servlet:jakarta.servlet-api")
}
