plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization").version("2.0.0")
}

group = "com.skliaruk"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("io.ktor:ktor-client-core:2.3.12")
    implementation("io.ktor:ktor-client-cio:2.3.12")
    implementation("io.ktor:ktor-client-auth:2.3.0")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.0")
    implementation("com.github.ajalt.clikt:clikt:4.0.0") // for CLI parsing
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0") // for date handling
    implementation("io.ktor:ktor-server-auth:2.3.0")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.skliaruk.MainKt"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}