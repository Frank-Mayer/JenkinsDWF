package io.frankmayer.dwf.endpoint

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import io.frankmayer.dwf.api.github.Runs
import io.frankmayer.dwf.config.EndpointConfig
import io.frankmayer.dwf.lib.Result
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class GitHubEndpoint(config: EndpointConfig) : Endpoint(config) {
    private val githubToken = env("TOKEN")
    private val githubUser = env("USER")
    private var token: String? = if (githubUser != null && githubToken != null) {
        Base64.getEncoder().encodeToString(("$githubUser:$githubToken").toByteArray())
    } else {
        null
    }
    private val objectMapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneId.of("UTC"))

    override fun request(project: String, workflow: String?): Result<String, String> {
        if (workflow == null) {
            return Result.failure("Workflow name is required")
        }

        val reqProp = hashMapOf<String, String>()
        if (token != null) {
            reqProp["Authorization"] = "Basic $token"
        }

        return fetch("${config.url}/$project/actions/runs?status=completed", reqProp)
            .map({ resp ->
                val job = objectMapper.readValue(resp, Runs::class.java)

                if (job.workflow_runs == null || job.workflow_runs.isEmpty()) {
                    if (job.message != null) {
                        Result.failure(job.message)
                    } else {
                        Result.failure("No workflow runs found")
                    }
                } else {
                    val runs = job.workflow_runs
                        .filter {
                            it.status == "completed" && (it.name == workflow || it.path.endsWith(workflow))
                        }
                        .sortedByDescending {
                            it.run_number
                        }

                    val lastSuccess = runs.firstOrNull {
                        it.conclusion == "success"
                    }

                    if (lastSuccess == null) {
                        Result.success(humanReadable(Duration.ZERO))
                    } else {
                        val lastFail = runs.firstOrNull {
                            it.conclusion == "failure"
                        }

                        if (lastFail == null) {
                            Result.success(neverFailed)
                        } else {
                            val now = LocalDateTime.now(ZoneId.of("UTC"))
                            val lastFailDate = LocalDateTime.parse(lastFail.run_started_at, formatter)

                            Result.success(humanReadable(Duration.between(lastFailDate, now).abs()))
                        }
                    }
                }
            },
            {
                Result.failure("GitHub responded with $it")
            })
    }
}
