plugins {
    java
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.8.8")
}

application {
    mainClass.set("PasswordManagerGUI")
}