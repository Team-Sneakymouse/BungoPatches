plugins {
  id("java")
  id("io.papermc.paperweight.userdev") version "1.7.1"
  id("xyz.jpenilla.run-paper") version "2.2.4"
}

java {
  toolchain.languageVersion = JavaLanguageVersion.of(21)
}

dependencies {
  compileOnly("space.vectrix.ignite:ignite-api:1.0.1")
  compileOnly("org.spongepowered:mixin:0.8.5")
  compileOnly("io.github.llamalad7:mixinextras-common:0.3.5")

  paperweight.paperDevBundle("1.20.6-R0.1-SNAPSHOT")
}


tasks {
  compileJava {
    options.release = 21
  }
  javadoc {
    options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
  }
}
