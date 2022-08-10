import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.2"
    id("io.spring.dependency-management") version "1.0.12.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
}

group = "com.mystockdata"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17
var coroutinesVersion = "1.6.1"

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutinesVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${coroutinesVersion}")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    // Jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Webflux
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // Apache Commons CSV
    implementation("org.apache.commons:commons-csv:1.8")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("junit:junit:4.13.2")
    testImplementation ("org.mockito:mockito-core:4.5.1")

}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
