val tilleggsstønaderKontrakterVersion = "2025.06.16-09.26.7154f1aaff32"

dependencies {
    implementation("org.assertj:assertj-core")
    implementation("org.springframework:spring-web")
    implementation(project(":http-client"))

    implementation("no.nav.tilleggsstonader.kontrakter:kontrakter-felles:$tilleggsstønaderKontrakterVersion")

    testImplementation(project(":util"))
}

tasks.sourcesJar {
    duplicatesStrategy = DuplicatesStrategy.WARN
}
