package io.frankmayer.dwf.api.github

import io.frankmayer.dwf.lib.extensions.*

data class WorkflowRun(
    val id: Long = -1L,
    val name: String = String.empty,
    val path: String = String.empty,
    val run_number: Long = -1L,
    val status: String = String.empty,
    val conclusion: String = String.empty,
    val run_started_at: String = String.empty,
)
