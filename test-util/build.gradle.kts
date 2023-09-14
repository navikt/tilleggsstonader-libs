dependencies {
    implementation("org.assertj:assertj-core")
    testImplementation(project(":util"))
}

tasks.sourcesJar {
    duplicatesStrategy = DuplicatesStrategy.WARN
}
