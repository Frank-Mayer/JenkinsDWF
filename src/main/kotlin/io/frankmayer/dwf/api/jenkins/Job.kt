package io.frankmayer.dwf.api.jenkins

import com.fasterxml.jackson.annotation.JsonProperty

data class Job(
    @JsonProperty("lastBuild") val lastBuild: Build?,
    @JsonProperty("lastFailedBuild") val lastFailedBuild: Build?,
    @JsonProperty("lastUnsuccessfulBuild") val lastUnsuccessfulBuild: Build?
)
