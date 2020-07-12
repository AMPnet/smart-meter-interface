import java.text.SimpleDateFormat
import java.util.Date
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.3.72"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion

    id("org.springframework.boot") version "2.4.0-M1"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    id("org.asciidoctor.convert") version "1.5.8"
    id("com.google.cloud.tools.jib") version "2.4.0"
    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
    id("io.gitlab.arturbosch.detekt").version("1.9.1")
    idea
    jacoco
}

group = "com.ampnet"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    // implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    implementation("com.github.AMPnet:jwt:0.0.7")
    implementation("io.github.microutils:kotlin-logging:1.7.10")

    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    // runtimeOnly("io.r2dbc:r2dbc-postgresql")
    // runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-webtestclient")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

jib {
    to {
        image = "ampnet/smart-meter-interface:$version"
        auth {
            username = System.getenv("DOCKER_USERNAME") ?: "DOCKER_USERNAME"
            password = System.getenv("DOCKER_PASSWORD") ?: "DOCKER_PASSWORD"
        }
        tags = setOf("latest")
    }
    container {
        creationTime = "USE_CURRENT_TIMESTAMP"
    }
}

jacoco.toolVersion = "0.8.5"
tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
        xml.destination = file("$buildDir/reports/jacoco/report.xml")
        csv.isEnabled = false
        html.destination = file("$buildDir/reports/jacoco/html")
    }
    sourceDirectories.setFrom(listOf(file("${project.projectDir}/src/main/kotlin")))
    dependsOn(tasks.test)
}
tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.2".toBigDecimal() // set higher minimum
            }
        }
    }
}
tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

task("qualityCheck") {
    dependsOn(tasks.ktlintCheck, tasks.detekt, tasks.jacocoTestReport, tasks.jacocoTestCoverageVerification)
}

tasks.asciidoctor {
    attributes(mapOf(
        "snippets" to file("build/generated-snippets"),
        "version" to version,
        "date" to SimpleDateFormat("yyyy-MM-dd").format(Date())
    ))
    dependsOn(tasks.test)
}

tasks.register<Copy>("copyDocs") {
    from(file("$buildDir/asciidoc/html5"))
    into(file("src/main/resources/static/docs"))
    dependsOn(tasks.asciidoctor)
}
