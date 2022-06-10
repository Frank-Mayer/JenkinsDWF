package io.frankmayer.dwf

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import io.frankmayer.dwf.config.Config
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.File


@SpringBootApplication
class DaysWithoutFailureApplication

fun main() {
    val logger = LoggerFactory.getLogger(DaysWithoutFailureApplication::class.java)

    try {
        val envFile = File("./.env")
        if (!envFile.exists()) {
            envFile.createNewFile()
        }

        val mapper = ObjectMapper(
            YAMLFactory.builder()
                .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
                .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
                .build()
        )
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .findAndRegisterModules()

        val configFile = File("./config.yaml")

        val config = if (configFile.exists()) {
            mapper.readValue(configFile, Config::class.java)
        } else {
            configFile.createNewFile()
            Config()
        }

        Config.instance = config

        try {
            System.setProperty("logging.level.root", config.logLevel)
        } catch (e: Exception) {
            logger.error("Could not set log level to ${config.logLevel}, resetting to default")
            config.logLevel = "WARN"
        }

        try {
            System.setProperty("server.servlet.context-path", config.basepath)
        } catch (e: Exception) {
            logger.error("Could not set basepath to ${config.basepath}, resetting to default")
            config.basepath = "/"
        }

        try {
            System.setProperty("server.port", config.port.toString())
        } catch (e: Exception) {
            logger.error("Could not set port to ${config.port}, resetting to default")
            config.port = 8080
        }

        try {
            System.setProperty("server.address", config.address)
        } catch (e: Exception) {
            logger.error("Could not set address to ${config.address}")
            config.address = "localhost"
        }

        if (configFile.canWrite()) {
            mapper.writeValue(configFile, config)
        }
    } catch (e: Exception) {
        logger.error("Error while initializing application", e)
    }

    try {
        runApplication<DaysWithoutFailureApplication>()
    } catch (e: Exception) {
        logger.error("Runtime error", e)
    }
}
