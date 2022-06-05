package io.frankmayer.dwf.endpoint

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import io.frankmayer.dwf.api.jenkins.Job
import io.frankmayer.dwf.config.EndpointConfig
import io.frankmayer.dwf.lib.Result
import java.net.HttpURLConnection
import java.net.URL
import java.time.Duration
import java.util.*

class JenkinsEndpoint(config: EndpointConfig) : Endpoint(config) {
    private val jenkinsToken = env("TOKEN")
    private val jenkinsUser = env("USER")
    private var token: String? = if (jenkinsUser != null && jenkinsToken != null) {
        Base64.getEncoder().encodeToString(("$jenkinsUser:$jenkinsToken").toByteArray())
    } else {
        null
    }
    private val objectMapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    override fun request(project: String, workflow: String?): Result<String, String> {
        val url =
            URL("${config.url}/job/$project/api/json?pretty=false&tree=lastBuild[timestamp],lastUnsuccessfulBuild[timestamp],lastFailedBuild[timestamp]")
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("Accept", "application/json")
        if (token != null) {
            connection.setRequestProperty("Authorization", "Basic $token")
        }

        val responseCode = connection.responseCode
        if (responseCode < 200 || responseCode >= 400) {
            return Result.failure("Jenkins responded with $responseCode")
        }

        val job = objectMapper.readValue(connection.inputStream, Job::class.java)

        if (job.lastBuild == null) {
            return Result.success(humanReadable(Duration.ZERO))
        }

        val lastFailed = job.lastFailedBuild ?: job.lastUnsuccessfulBuild ?: return Result.success(neverFailed)

        return if (job.lastBuild.timestamp < lastFailed.timestamp) {
            Result.success(humanReadable(Duration.ZERO))
        } else {
            Result.success(
                humanReadable(
                    Duration.ofMillis(
                        System.currentTimeMillis() - lastFailed.timestamp
                    )
                )
            )
        }
    }
}
