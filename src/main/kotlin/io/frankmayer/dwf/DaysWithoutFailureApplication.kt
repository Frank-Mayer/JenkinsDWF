package io.frankmayer.dwf

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import io.frankmayer.dwf.config.Config
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.File


@SpringBootApplication
class DaysWithoutFailureApplication

fun main() {
    val mapper = ObjectMapper(YAMLFactory())
    mapper.findAndRegisterModules()
    val configFile = File("./config.yaml")

    val config = if (configFile.exists()) {
        mapper.readValue(configFile, Config::class.java)
    } else {
        configFile.createNewFile()
        Config()
    }

    if (configFile.canWrite()) {
        mapper.writeValue(configFile, config)
    }

    System.setProperty(
        "spring.autoconfigure.exclude",
        "org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration"
    )
    System.setProperty("server.error.whitelabel.enabled", "false")

    System.setProperty("server.servlet.context-path", config.basepath)
    System.setProperty("server.port", config.port.toString())
    System.setProperty("server.address", config.address)

    runApplication<DaysWithoutFailureApplication>()
}
