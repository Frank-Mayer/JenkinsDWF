package io.frankmayer.dwf.api.github

data class Runs(
    val total_count: Int? = null,
    val workflow_runs: List<WorkflowRun>? = null,
    val message: String? = null
)
