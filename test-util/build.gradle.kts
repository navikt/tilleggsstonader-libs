dependencies {
    implementation(project(":util"))
}

tasks.sourcesJar {
    duplicatesStrategy = DuplicatesStrategy.WARN
}
