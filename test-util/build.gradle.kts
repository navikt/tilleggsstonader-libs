val tilleggsstønaderKontrakterVersion = "2024.10.31-11.26.7172148519ea"

dependencies {
    implementation("org.assertj:assertj-core")
    implementation("org.springframework:spring-web")
    implementation(project(":http-client"))

    implementation("no.nav.tilleggsstonader.kontrakter:tilleggsstonader-kontrakter:$tilleggsstønaderKontrakterVersion")

    testImplementation(project(":util"))
}

tasks.sourcesJar {
    duplicatesStrategy = DuplicatesStrategy.WARN
}
