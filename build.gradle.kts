import org.springframework.boot.gradle.tasks.run.BootRun
import java.net.URI

plugins {
	kotlin("jvm") version "2.0.20"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.2"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "de.hsudbrock"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
	mavenLocal()
}

dependencies {
	implementation("com.expediagroup", "graphql-kotlin-spring-server", "8.3.0")
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	implementation("org.eclipse.lmos:lmos-kotlin-sdk-client:0.1.0-SNAPSHOT")

	implementation("org.jetbrains.kotlin:kotlin-reflect")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

springBoot {
	mainClass.set("ai.ancf.lmos.arc.bridge.ArcViewWoTBridgeApplicationKt")
}

tasks.register("downloadOtelAgent") {
	doLast {
		val agentUrl =
			"https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar"
		val agentFile = file("${project.buildDir}/libs/opentelemetry-javaagent.jar")

		// Ensure directory exists before downloading
		agentFile.parentFile.mkdirs()

		if (!agentFile.exists()) {
			println("Downloading OpenTelemetry Java Agent...")
			agentFile.writeBytes(URI(agentUrl).toURL().readBytes())
			println("Download completed: ${agentFile.absolutePath}")
		} else {
			println("OpenTelemetry Java Agent already exists: ${agentFile.absolutePath}")
		}
	}
}

tasks.named<BootRun>("bootRun") {
	dependsOn("downloadOtelAgent")
	jvmArgs = listOf(
		"-javaagent:${project.buildDir}/libs/opentelemetry-javaagent.jar"
	)
	systemProperty("otel.java.global-autoconfigure.enabled", "true")
	systemProperty("otel.traces.exporter", "otlp")
	systemProperty("otel.exporter.otlp.endpoint", "http://localhost:4318")
	systemProperty("otel.service.name", "arc-wot-bridge")
	systemProperty("otel.instrumentation.graphql-java.enabled", "false")
	//systemProperty("otel.javaagent.debug", "true")
	//systemProperty("otel.instrumentation.common.default-enabled", "false")
	//systemProperty("otel.instrumentation.opentelemetry-api.enabled", "true")
	//systemProperty("otel.instrumentation.opentelemetry-instrumentation-annotations.enabled", "true")

}

