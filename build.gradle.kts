import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.version
import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val appVersion = "1.0"
val kotlinVersion = "1.4.10"
val ktorVersion = "1.4.1"

group = "cookiedragon"

plugins {
    kotlin("jvm") version "1.4.10"
	id("com.github.johnrengelman.shadow") version "6.1.0"
}

repositories {
    mavenCentral()
    jcenter()
    "http://dl.bintray.com/kotlin".let {
        maven { setUrl("$it/ktor") }
        maven { setUrl("$it/kotlinx") }
    }
}

dependencies {
    fun ktor(s: String = "", v: String = ktorVersion) = "io.ktor:ktor$s:$v"

    implementation(kotlin("stdlib-jdk8", kotlinVersion))
	implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.6.10")
	implementation(ktor())
	implementation(ktor("-html-builder"))
	implementation(ktor("-server-netty"))
	implementation(ktor("-freemarker"))
	implementation(ktor("-auth"))
	implementation(ktor("-auth-jwt"))
	implementation(ktor("-client-apache"))
	implementation(ktor("-client-json"))
	implementation(ktor("-client-gson"))
	implementation("ch.qos.logback", "logback-classic", "1.2.1")
	implementation("org.slf4j", "slf4j-api", "1.7.25")
	implementation("org.jetbrains.exposed", "exposed-core", "0.20.1")
	implementation("org.jetbrains.exposed", "exposed-dao", "0.20.1")
	implementation("org.jetbrains.exposed", "exposed-jdbc", "0.20.1")
	implementation("org.jetbrains.exposed", "exposed-jodatime", "0.20.1")
	implementation("com.h2database", "h2", "1.4.200")
	implementation("com.zaxxer", "HikariCP", "3.4.5")
	implementation("mysql", "mysql-connector-java", "8.0.19")
	implementation("org.mindrot", "jbcrypt", "0.4")
	implementation("com.brsanthu", "google-analytics-java", "2.0.0")
	implementation("com.vladsch.flexmark", "flexmark-all", "0.62.2")
}



tasks {
    withType<Jar> {
        manifest {
            attributes(mapOf("Main-Class" to "dev.binclub.web.MainKt"))
        }

        archiveName = "binclub-web.jar"
    }
	withType<ShadowJar> {
		manifest {
			attributes(mapOf("Main-Class" to "dev.binclub.web.MainKt"))
		}
		
		archiveName = "binclub-final.jar"
	}
	build {
		finalizedBy(shadowJar)
	}
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
	freeCompilerArgs = listOf("-Xinline-classes")
}
