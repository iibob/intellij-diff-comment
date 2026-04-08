plugins {
    id("java")
    kotlin("jvm") version "1.9.22"
    id("org.jetbrains.intellij") version "1.17.2"
}

group = "com.github.iibob"
version = "1.1.0"

repositories {
    mavenCentral()
    gradlePluginPortal()

}

intellij {
//    version.set("2023.3")
//    type.set("PY")
//    指定本地已安装的 PyCharm 作为开发环境
    localPath.set("D:/APP/PyCharm/PyCharm Community Edition 2022.1.3")
}

tasks {
    patchPluginXml {
        sinceBuild.set("221")
        untilBuild.set("243.*")
    }
}