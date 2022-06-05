package io.frankmayer.dwf.controller

import io.frankmayer.dwf.config.Config
import io.frankmayer.dwf.config.EndpointConfig
import io.frankmayer.dwf.config.EndpointType
import io.frankmayer.dwf.endpoint.Endpoint
import io.frankmayer.dwf.endpoint.GitHubEndpoint
import io.frankmayer.dwf.endpoint.JenkinsEndpoint
import io.frankmayer.dwf.render.Svg
import org.apache.commons.io.FilenameUtils
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class MainController {
    private val endpointCache = mutableMapOf<EndpointType, Endpoint>()
    private val svg = Svg()
    private var config: Config? = null

    @GetMapping("/{endpoint}/{file}")
    @Async
    fun index(@PathVariable("endpoint") endpointName: String, @PathVariable file: String): String {
        if (!ensureConfigLoaded()) {
            return "Config not loaded"
        }

        val endpointConfig =
            config!!.endpoints.find { it.path == endpointName } ?: return "Endpoint $endpointName not found"

        val endpoint = getEndpoint(endpointConfig)

        val extension = FilenameUtils.getExtension(file)
        val project = FilenameUtils.removeExtension(file)

        return render(endpoint, project, null, extension)
    }

    @GetMapping("/{endpoint}/{project}/{file}")
    @Async
    fun index(
        @PathVariable("endpoint") endpointName: String,
        @PathVariable("project") projectName: String,
        @PathVariable file: String
    ): String {
        if (!ensureConfigLoaded()) {
            return "Config not loaded"
        }

        val endpointConfig =
            config!!.endpoints.find { it.path == endpointName } ?: return "Endpoint $endpointName not found"

        val endpoint = getEndpoint(endpointConfig)

        val extension = FilenameUtils.getExtension(file)
        val workflow = FilenameUtils.removeExtension(file)

        return render(endpoint, projectName, workflow, extension)
    }

    private fun render(
        endpoint: Endpoint,
        project: String,
        workflow: String?,
        extension: String
    ): String {
        if (!ensureConfigLoaded()) {
            return "Config not loaded"
        }

        return endpoint.get(project, workflow).map({
            when (extension) {
                "svg" -> svg.render(it)
                else -> it
            }
        }, { it })
    }

    private fun ensureConfigLoaded(): Boolean {
        if (config == null) {
            config = Config.instance ?: return false
        }

        return true
    }

    private fun getEndpoint(endpoint: EndpointConfig): Endpoint {
        return endpointCache.getOrPut(endpoint.type) {
            when (endpoint.type) {
                EndpointType.JENKINS -> JenkinsEndpoint(endpoint)
                EndpointType.GITHUB -> GitHubEndpoint(endpoint)
            }
        }
    }
}
