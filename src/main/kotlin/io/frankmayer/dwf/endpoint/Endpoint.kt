package io.frankmayer.dwf.endpoint

import io.frankmayer.dwf.config.EndpointConfig
import io.frankmayer.dwf.lib.CacheMap
import io.frankmayer.dwf.lib.Result
import java.time.Duration

abstract class Endpoint(protected val config: EndpointConfig) {
    private val cache = CacheMap<String, Result<String, String>> { _, duration ->
        duration.toHours() > 12L
    }

    fun get(project: String, workflow: String?): Result<String, String> {
        return cache.getOrPut("$project/$workflow") {
            request(project, workflow)
        }
    }

    /**
     * - Never failed = -1
     * - No success after last fail = 0
     * - Success after last fail = last build - last fail
     */
    protected abstract fun request(project: String, workflow: String?): Result<String, String>

    protected val neverFailed = "Never failed"
    protected val neverWorked = "Never worked"
    protected fun humanReadable(duration: Duration): String {
        return duration.toDays().toString() + " days"
    }
}
