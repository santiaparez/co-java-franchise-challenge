plugins {
  id("org.springframework.boot") version "3.3.4"
  id("io.spring.dependency-management") version "1.1.6"
  java
  jacoco
  id("org.sonarqube")
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories { mavenCentral() }

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("software.amazon.awssdk:dynamodb:2.25.54")
  implementation("software.amazon.awssdk:dynamodb-enhanced:2.25.54")
  implementation("software.amazon.awssdk:netty-nio-client:2.25.54")
  implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.6.0")

  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("io.projectreactor:reactor-test")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("io.projectreactor:reactor-test")
  testImplementation("org.mockito:mockito-core")
  testImplementation("org.mockito:mockito-junit-jupiter")

}

tasks.test { useJUnitPlatform() }

jacoco {
  toolVersion = "0.8.12"
}

tasks.jacocoTestReport {
  dependsOn(tasks.test)
  reports {
    xml.required.set(true)
    csv.required.set(false)
    html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/html"))
  }
}

tasks.check {
  dependsOn(tasks.jacocoTestReport)
}

sonar {
  properties {
    property(
      "sonar.coverage.jacoco.xmlReportPaths",
      layout.buildDirectory.file("reports/jacoco/test/jacocoTestReport.xml").get().asFile.absolutePath
    )
  }
}
