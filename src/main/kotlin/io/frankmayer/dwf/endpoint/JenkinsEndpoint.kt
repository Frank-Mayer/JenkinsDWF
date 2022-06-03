package io.frankmayer.dwf.endpoint

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import io.frankmayer.dwf.api.jenkins.Job
import io.frankmayer.dwf.config.EndpointConfig
import io.github.cdimascio.dotenv.dotenv
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class JenkinsEndpoint(private val config: EndpointConfig) : Endpoint {
    private var dotenv = dotenv()
    private val jenkinsToken: String = dotenv.get("JENKINS_TOKEN")
    private val jenkinsUser: String = dotenv.get("JENKINS_USER")
    private var token: String = Base64.getEncoder().encodeToString(("$jenkinsUser:$jenkinsToken").toByteArray())
    private val objectMapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    override fun get(project: String): Long? {
        val url =
            URL("${config.url}/job/$project/api/json?pretty=false&tree=lastBuild[timestamp],lastUnsuccessfulBuild[timestamp],lastFailedBuild[timestamp]")
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("Authorization", "Basic $token")
        connection.setRequestProperty("Accept", "application/json")

        val responseCode = connection.responseCode
        if (responseCode < 200 || responseCode >= 400) {
            return null
        }

        val job = objectMapper.readValue(connection.inputStream, Job::class.java)

        if (job.lastBuild == null) {
            return -1
        }

        val lastFailed = job.lastFailedBuild ?: job.lastUnsuccessfulBuild ?: return -1

        return if (job.lastBuild.timestamp > lastFailed.timestamp) {
            lastFailed.timestamp
        } else {
            0
        }
    }
}
