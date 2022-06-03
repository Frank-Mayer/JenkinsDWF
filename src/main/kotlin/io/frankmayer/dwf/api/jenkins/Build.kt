package io.frankmayer.dwf.api.jenkins

import com.fasterxml.jackson.annotation.JsonProperty

data class Build(
    @JsonProperty("timestamp") val timestamp: Long
)
