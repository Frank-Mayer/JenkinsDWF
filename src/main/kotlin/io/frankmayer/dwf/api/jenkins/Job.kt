package io.frankmayer.dwf.api.jenkins

import com.fasterxml.jackson.annotation.JsonProperty

data class Job(
    @JsonProperty("lastBuild") val lastBuild: Build? = null,
    @JsonProperty("lastFailedBuild") val lastFailedBuild: Build? = null,
    @JsonProperty("lastUnsuccessfulBuild") val lastUnsuccessfulBuild: Build? = null
)
