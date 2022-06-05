package io.frankmayer.dwf.endpoint

import io.frankmayer.dwf.config.EndpointConfig
import io.frankmayer.dwf.lib.CacheMap
import io.frankmayer.dwf.lib.Result
import io.github.cdimascio.dotenv.dotenv
import java.time.Duration
import java.util.*

abstract class Endpoint(protected val config: EndpointConfig) {
    private var dotenv = dotenv()

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

    protected val neverFailed = "No fail"
    protected fun humanReadable(duration: Duration): String {
        return duration.toDays().toString() + " days"
    }

    protected fun env(varName: String): String? {
        val varGlobal = "${config.type}_${varName.uppercase(Locale.getDefault())}"
        val varSpecific = "${varGlobal}_${config.path}"

        return try {
            dotenv.get(varSpecific)
        } catch (_: Exception) {
            null
        } ?: try {
            dotenv.get(varGlobal)
        } catch (_: Exception) {
            null
        }
    }
}
