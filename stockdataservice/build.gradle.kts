import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.google.protobuf.gradle.*

plugins {
    id("org.springframework.boot") version "2.7.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    id("com.google.protobuf") version "0.8.12"
    idea
}

group = "com.mystockdata"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17
var coroutinesVersion = "1.6.1"
val protobufVersion = "3.21.1"

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

    // Spring Webflux
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // Spring Cloud Stream for RabbitMQ
    implementation("org.springframework.cloud:spring-cloud-stream")
    implementation("org.springframework.cloud:spring-cloud-starter-stream-rabbit")

    // Jsoup
    implementation("org.jsoup:jsoup:1.14.3")

    // Jaxb
    implementation("javax.xml.bind:jaxb-api:2.3.1")
    implementation("javax.activation:activation:1.1.1")
    implementation("org.glassfish.jaxb:jaxb-runtime:2.3.1")

    // Java WebSocket
    implementation("org.java-websocket:Java-WebSocket:1.5.3")

    // Protobuf
    implementation("com.google.protobuf:protobuf-kotlin:$protobufVersion")

    // InfluxDB Kotlin Client Library
    implementation("com.influxdb:influxdb-client-kotlin:6.3.0")

    // Apache Commons CSV
    implementation("org.apache.commons:commons-csv:1.8")

    // MongoDB
    implementation("org.springframework.data:spring-data-mongodb")
    implementation("org.mongodb:mongodb-driver-sync")
    implementation("org.mongodb:mongodb-driver-reactivestreams")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("junit:junit:4.13.2")
    testImplementation ("org.mockito:mockito-core:4.5.1")

    // Flapdoodle as embedded MongoDB for testing purposes
    testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo:3.4.6")
}

sourceSets {
    main {
        java {
            this.srcDirs.add(File("build/generated/source/proto/main/java"))
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
    generatedFilesBaseDir = "$projectDir/gen"
}
idea {
    module {
        // proto files and generated Java files are automatically added as
        // source dirs.
        // If you have additional sources, add them here:
        sourceDirs.add(file("build/generated/source/proto/main/java"));
    }
}


extra["springCloudVersion"] = "2020.0.4"
dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.withType<KotlinCompile> {
    dependsOn.add(":generateProto")
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}