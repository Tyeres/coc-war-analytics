plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("io.github.cdimascio:dotenv-java:3.0.0")
    implementation(files("libs/json-simple-1.1.1.jar"))
    implementation(files("libs/mysql-connector-j-26.7.0.jar"))
}

tasks.test {
    useJUnitPlatform()
}