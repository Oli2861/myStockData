import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
}

group = "com.mystockdata"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17
var coroutinesVersion = "1.6.1"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/snapshot") }
    maven { url = uri("https://repo.spring.io/milestone") }
}

extra["springCloudVersion"] = "2021.0.3-SNAPSHOT"

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutinesVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${coroutinesVersion}")

    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    // Jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Spring Webflux
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // Spring Cloud Stream for RabbitMQ
    implementation("org.springframework.cloud:spring-cloud-stream")
    implementation("org.springframework.cloud:spring-cloud-stream-binder-rabbit")

    // Awaitility for scheduling tasks
    implementation("org.awaitility:awaitility:3.1.2")

    // MongoDB
    implementation("org.springframework.data:spring-data-mongodb")
    implementation("org.mongodb:mongodb-driver-sync")
    implementation("org.mongodb:mongodb-driver-reactivestreams")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.amqp:spring-rabbit-test")
    testImplementation("junit:junit:4.13.2")

    // Flapdoodle as embedded MongoDB for testing purposes
    testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo:3.4.6")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
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
