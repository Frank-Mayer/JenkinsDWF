package io.frankmayer.dwf.endpoint

import io.frankmayer.dwf.config.EndpointConfig
import io.frankmayer.dwf.lib.CacheMap
import io.frankmayer.dwf.lib.Result
import io.github.cdimascio.dotenv.dotenv
import org.slf4j.LoggerFactory
import java.net.HttpURLConnection;
import java.io.InputStream
import java.net.URL
import java.time.Duration
import java.util.*
import kotlin.collections.HashMap

abstract class Endpoint(protected val config: EndpointConfig) {
    private var dotenv = dotenv()
    private val envCache = HashMap<String, String?>()
    protected var logger = LoggerFactory.getLogger(this::class.java)

    private val cache = CacheMap<String, Result<String, String>> { _, duration ->
        duration.toHours() > 12L
    }

    fun get(project: String, workflow: String?): Result<String, String> {
        return cache.getOrPut("$project/$workflow") {
            try {
                request(project, workflow)
            } catch (e: Exception) {
                val msg = e.message ?: e.localizedMessage
                logger.warn(msg)
                Result.failure(msg)
            }
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

        if (envCache.containsKey(varSpecific)) {
            return envCache[varSpecific]
        }

        val value = try {
            dotenv.get(varSpecific)
        } catch (_: Exception) {
            null
        } ?: try {
            dotenv.get(varGlobal)
        } catch (_: Exception) {
            null
        }

        envCache[varSpecific] = value
        return value
    }

    protected fun fetch(url: String, reqProp: HashMap<String, String>? = null, method: String = "GET",): Result<InputStream, Int> {
        val obj = URL(url)
        val conn = obj.openConnection() as HttpURLConnection
        conn.readTimeout = 5000

        if (reqProp != null) {
            for (prop in reqProp) {
                conn.addRequestProperty(prop.key, prop.value)
            }
        } else {
            conn.addRequestProperty("Accept-Type", "application/json")
        }

        conn.requestMethod = method

        var status = conn.responseCode

        return if (status in 300..399) {
            val location = conn.getHeaderField("Location")
            if (location != null) {
                fetch(location, reqProp, method)
            } else {
                logger.debug("Redirected to $url but no location header found")
                Result.failure(status)
            }
        } else if (status in 200..299) {
            Result.success(conn.inputStream)
        } else {
            logger.debug("Failed to fetch $url with status $status")
            Result.failure(status)
        }
    }
}
