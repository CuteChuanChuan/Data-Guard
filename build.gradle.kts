plugins {
    id("java")
    id("com.diffplug.spotless") version "6.23.3"
    checkstyle
    pmd
    id("com.github.spotbugs") version "5.2.5"
}

group = "org.raymondhung"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains:annotations:24.0.1")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.mockito:mockito-core:5.8.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.8.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.1")
    testImplementation("net.datafaker:datafaker:2.0.2")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

spotless {
    java {
        googleJavaFormat("1.18.1")
        removeUnusedImports()
        endWithNewline()
    }

    kotlinGradle {
        target("*.gradle.kts")
        ktlint()
    }
}

checkstyle {
    toolVersion = "10.12.5"
    configFile = file("config/checkstyle/checkstyle.xml")
    isIgnoreFailures = false
    maxWarnings = 0
}

pmd {
    isConsoleOutput = true
    toolVersion = "6.55.0"
    rulesMinimumPriority.set(5)
    ruleSetFiles = files("config/pmd/basic.xml")
}

spotbugs {
    ignoreFailures.set(false)
    showStackTraces.set(true)
    showProgress.set(true)
    effort.set(com.github.spotbugs.snom.Effort.MAX)
    reportLevel.set(com.github.spotbugs.snom.Confidence.LOW)
}

tasks.build {
    dependsOn("spotlessCheck", "checkstyleMain", "pmdMain", "spotbugsMain")
}

tasks.withType<Checkstyle>().configureEach {
    reports {
        xml.required.set(false)
        html.required.set(true)
        html.outputLocation.set(file("build/reports/checkstyle.html"))
    }
}

tasks.register("format") {
    dependsOn("spotlessApply")
    description = "Format all source code"
    group = "formatting"
}

tasks.register("lint") {
    dependsOn("checkstyleMain", "checkstyleTest", "pmdMain", "spotbugsMain")
    description = "Run all linting checks"
    group = "verification"
}
