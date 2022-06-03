package io.frankmayer.dwf.controller

import io.frankmayer.dwf.config.Config
import io.frankmayer.dwf.config.EndpointConfig
import io.frankmayer.dwf.config.EndpointType
import io.frankmayer.dwf.endpoint.Endpoint
import io.frankmayer.dwf.endpoint.JenkinsEndpoint
import io.frankmayer.dwf.render.Svg
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.sql.Timestamp
import java.time.Duration
import java.time.ZonedDateTime
import org.apache.commons.io.FilenameUtils;

@RestController
class MainController {
    private val endpointCache = mutableMapOf<EndpointType, Endpoint>()
    private val svg = Svg()

    @GetMapping("/{endpoint}/{file}")
    @Async
    fun index(@PathVariable("endpoint") endpointName: String, @PathVariable file: String): String {
        val config = Config.instance ?: return "Config not loaded"
        val endpointConfig =
            config.endpoints.find { it.path == endpointName } ?: return "Endpoint $endpointName not found"

        val endpoint = getEndpoint(endpointConfig)

        val extension = FilenameUtils.getExtension(file)
        val project = FilenameUtils.removeExtension(file)

        val txt = when (val timestamp = endpoint.get(project)) {
            null -> return "Project $project not found"
            0L -> "Never failed"
            -1L -> "N/A"
            else -> {
                val epTime =
                    ZonedDateTime.of(Timestamp(timestamp).toLocalDateTime(), endpointConfig.timezone).toLocalDateTime()
                val serverTime = ZonedDateTime.now().withZoneSameInstant(config.timezone).toLocalDateTime()

                Duration.between(epTime, serverTime).toDays().toString() + " days"
            }
        }

        return when (extension) {
            "svg" -> svg.render(txt)
            else -> txt
        }
    }

    private fun getEndpoint(endpoint: EndpointConfig): Endpoint {
        return endpointCache.getOrPut(endpoint.type) {
            when (endpoint.type) {
                EndpointType.JENKINS -> JenkinsEndpoint(endpoint)
                EndpointType.GITHUB -> TODO()
            }
        }
    }
}
