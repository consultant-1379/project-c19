plugins {
    java
    id("org.kordamp.gradle.jacoco") version "0.47.0"
    id("org.sonarqube") version "3.3"
}

config {
    coverage {
        jacoco {
            enabled
            aggregateExecFile
            aggregateReportHtmlFile
            aggregateReportXmlFile
            additionalSourceDirs
            additionalClassDirs
        }
    }
}

group = "com.ericsson"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/org.jacoco/org.jacoco.core
    implementation("org.jacoco:org.jacoco.core:0.8.7")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

subprojects {
    apply(plugin = "org.kordamp.gradle.jacoco")
}

sonarqube {
    properties {
        property("sonar.java.coveragePlugin", "jacoco")
        property("sonar.projectKey", "Software-Delivery-Dashboard")
        property("sonar.host.url", "http://localhost:9000")
        property("sonar.login", "41b2b0f024e0f024762a47703336b9c7bec424c7")
    }
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report

    reports {
        xml.isEnabled = true
        html.isEnabled = false
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.0".toBigDecimal()
            }
        }
    }
}
