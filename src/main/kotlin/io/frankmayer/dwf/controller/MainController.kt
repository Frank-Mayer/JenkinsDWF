package io.frankmayer.dwf.controller

import io.frankmayer.dwf.DaysWithoutFailureApplication
import io.frankmayer.dwf.config.EndpointConfig
import io.frankmayer.dwf.config.EndpointType
import io.frankmayer.dwf.endpoint.Endpoint
import io.frankmayer.dwf.endpoint.JenkinsEndpoint
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.sql.Timestamp
import java.time.Duration
import java.time.LocalDateTime

@RestController
class MainController {
    private val endpointCache = mutableMapOf<EndpointType, Endpoint>()

    @GetMapping("/{endpoint}/{file}")
    @Async
    fun index(@PathVariable("endpoint") endpointName: String, @PathVariable file: String): String {
        val endpointConfig = DaysWithoutFailureApplication.config?.endpoints?.find { it.path == endpointName }
            ?: return "Endpoint $endpointName not found"

        val endpoint = getEndpoint(endpointConfig)

        val dt = when (val timestamp = endpoint.get(file)) {
            null -> return "Project $file not found"
            0L -> LocalDateTime.now()
            -1L -> return "N/A"
            else -> Timestamp(timestamp).toLocalDateTime()
        }

        return Duration.between(dt, LocalDateTime.now()).toDays().toString() + " days"
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
