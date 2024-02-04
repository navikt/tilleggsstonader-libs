rootProject.name = "tilleggsstonader-libs"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

include("http-client")
include("log")
include("pdl")
include("sikkerhet")
include("util")
include("unleash")
include("test-util")
