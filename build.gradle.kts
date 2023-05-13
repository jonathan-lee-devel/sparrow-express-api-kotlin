import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.0.6"
	id("io.spring.dependency-management") version "1.1.0"
	jacoco
	id("org.sonarqube") version "3.5.0.2730"
	kotlin("jvm") version "1.7.22"
	kotlin("kapt") version "1.5.10"
	kotlin("plugin.spring") version "1.7.22"
}

group = "io.jonathanlee"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	kapt("org.mapstruct:mapstruct-processor:1.5.5.Final")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.4.Final")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
}

kapt {
	arguments {
		arg("mapstruct.defaultComponentModel", "spring")
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
	finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required
		xml.isEnabled = true
	}
	sourceDirectories.setFrom(files(project.projectDir))
	executionData.setFrom(
			fileTree(project.projectDir) {
				setIncludes(setOf("**/**/*.exec", "**/**/*.ec"))
			}
	)
}

tasks.bootBuildImage {
	setProperty("imageName", "jonathanleedev/sparrow-express-api-kotlin")
}

sonar {
	properties {
		property("sonar.host.url", "https://sonarcloud.io")
		property("sonar.organization", "io-jonathanlee")
		property("sonar.projectKey", "io-jonathanlee_sparrow-express-api-kotlin")
		property("sonar.exclusions", "**/SparrowExpressApiKotlinApplication.kt,**/*Config.kt,**/*Model.kt,**/*Dto.kt,**/*Exception.kt")
	}
}
