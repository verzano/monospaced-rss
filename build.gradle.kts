plugins {
    java
    application
}

group "dev.verzano.monospaced"
version "0.1.0"

repositories {
    mavenLocal()
    jcenter()
}

dependencies {
    implementation("dev.verzano.monospaced", "monospaced-gui", "0.1.0")

//     TODO these two should come from the monospaced-gui...
    runtimeOnly("net.java.dev.jna", "jna", "5.6.0")
    runtimeOnly("org.jline", "jline", "3.18.0")

    implementation("com.google.code.gson", "gson", "2.8.6")

    implementation("com.rometools", "rome", "1.15.0")
    implementation("com.rometools", "rome-modules", "1.15.0")

    implementation("org.jsoup", "jsoup", "1.13.1")
}

application {
    mainClass.set("dev.verzano.monospaced.rss.MonospacedRss")
    applicationDefaultJvmArgs = listOf("-Djava.util.logging.config.file=conf/logging.properties")
}
