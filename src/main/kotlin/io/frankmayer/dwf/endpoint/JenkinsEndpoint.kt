package io.frankmayer.dwf.endpoint

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import io.frankmayer.dwf.api.jenkins.Job
import io.frankmayer.dwf.config.EndpointConfig
import io.frankmayer.dwf.lib.Result
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
        val reqProp = hashMapOf<String, String>()
        if (token != null) {
            reqProp["Authorization"] = "Basic $token"
        }

        return fetch(
            "${config.url}/job/$project/api/json?pretty=false&tree=lastBuild[timestamp],lastUnsuccessfulBuild[timestamp],lastFailedBuild[timestamp]",
            reqProp
        ).map({
            val job = objectMapper.readValue(it, Job::class.java)

            if (job.lastBuild == null) {
                Result.success(humanReadable(Duration.ZERO))
            } else {
                val lastFailed = job.lastFailedBuild ?: job.lastUnsuccessfulBuild

                if (lastFailed == null) {
                    Result.success(neverFailed)
                } else if (job.lastBuild.timestamp < lastFailed.timestamp) {
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
        },
            {
                Result.failure("Jenkins responded with $it")
            })
    }
}
