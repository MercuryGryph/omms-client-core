/*
 * This file was generated by the Gradle 'init' task.
 *
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    api("com.google.code.gson:gson:2.9.0")
}

group = "net.zhuruoling"
version = "0.15.0"
description = "omms-client-core"
java.sourceCompatibility = JavaVersion.VERSION_1_8


publishing {
    repositories {
        mavenLocal()
    }
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}
