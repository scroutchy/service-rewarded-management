plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.spring") version "2.1.20"
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.sonarqube") version "6.0.1.5171"
    id("jacoco")
    id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1"
    `maven-publish`
}

group = "org.scr.project"
fun getGitTag() = System.getenv("CI_COMMIT_TAG") ?: System.getenv("CI_COMMIT_REF_SLUG") ?: "0.0.1-SNAPSHOT"

version = getGitTag()
private val commonsCinemaVersion = "2.1.4"
private val mockkVersion = "1.12.0"

repositories {
    mavenCentral()
    maven("https://gitlab.com/api/v4/projects/67204824/packages/maven")
    maven("https://packages.confluent.io/maven/")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("io.projectreactor.kafka:reactor-kafka:1.3.23")
    implementation("org.apache.avro:avro:1.12.0")
    implementation("io.confluent:kafka-avro-serializer:7.9.0")
    implementation("com.scr.project.commons.cinema:commons-cinema:${commonsCinemaVersion}")
    testImplementation("com.scr.project.commons.cinema.test:commons-cinema-test:${commonsCinemaVersion}")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mongodb")
    testImplementation("org.testcontainers:kafka")
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}

tasks.register("printCoverage") {
    group = "verification"
    description = "Prints the code coverage of the project"
    dependsOn(tasks.jacocoTestReport)
    doLast {
        val reportFile = layout.buildDirectory.file("reports/jacoco/test/jacocoTestReport.xml").get().asFile
        if (reportFile.exists()) {
            val factory = javax.xml.parsers.DocumentBuilderFactory.newInstance()
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
            val builder = factory.newDocumentBuilder()
            val document = builder.parse(reportFile)
            val counters = document.getElementsByTagName("counter")
            var covered = 0
            var missed = 0
            for (i in 0 until counters.length) {
                val counter = counters.item(i) as org.w3c.dom.Element
                covered += counter.getAttribute("covered").toInt()
                missed += counter.getAttribute("missed").toInt()
            }
            val totalCoverage = (covered * 100.0) / (covered + missed)
            println("Total Code Coverage: %.2f%%".format(totalCoverage))
        } else {
            println("JaCoCo report file not found!")
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy("jacocoTestReport", tasks.named("printCoverage"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

tasks.register<Jar>("copyAvroSchemas") {
    group = "build"
    description = "Packages Avro schema files into a publishable JAR with 'schemas' classifier"
    archiveClassifier.set("schemas") // Définit le classifier 'schemas'
    archiveBaseName.set("service-rewarded-management") // Nom de base (souvent nom du projet)
    archiveFileName.set("${archiveBaseName.get()}-${archiveVersion.get()}-${archiveClassifier.get()}.jar") // Nom complet du fichier
    destinationDirectory.set(layout.buildDirectory.dir("libs")) // build/libs
    from("src/main/avro") { /* ... */ }
    include("**/*.avsc")
}

publishing {
    publications {
        create<MavenPublication>("avroSchemas") {
            artifact(tasks.named<Jar>("bootJar"))
            artifact(tasks.named<Jar>("copyAvroSchemas"))
        }
    }

    repositories {
        maven {
            url = uri("${System.getenv("CI_API_V4_URL")}/projects/${System.getenv("CI_PROJECT_ID")}/packages/maven")
            credentials(HttpHeaderCredentials::class.java) {
                name = "Job-Token"
                value = System.getenv("CI_JOB_TOKEN")
            }
            authentication { create("header", HttpHeaderAuthentication::class.java) }
        }
    }
}

tasks.register("publishToGitLab") {
    group = "publishing"
    description = "Publish the project to GitLab Maven repository"
    dependsOn("publishMavenJavaPublicationToMavenRepository")
}