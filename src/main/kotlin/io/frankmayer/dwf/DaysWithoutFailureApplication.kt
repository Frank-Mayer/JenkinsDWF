package io.frankmayer.dwf

import com.google.gson.GsonBuilder
import io.frankmayer.dwf.config.Config
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.File

@SpringBootApplication
class DaysWithoutFailureApplication

fun main() {
    val json = GsonBuilder().setPrettyPrinting().create()

    val configFile = File("./config.json")
    val config = if (configFile.exists()) {
        json.fromJson(configFile.readText(), Config::class.java)
    } else {
        configFile.createNewFile()
        Config()
    }

    if (configFile.canWrite()) {
        configFile.writeText(json.toJson(config) + "\n")
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
