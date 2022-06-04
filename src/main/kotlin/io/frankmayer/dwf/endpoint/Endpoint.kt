package io.frankmayer.dwf.endpoint

import io.frankmayer.dwf.lib.CacheMap

abstract class Endpoint {
    private val cache = CacheMap<String, Long?> { _, duration ->
        duration.toHours() > 12L
    }

    fun get(project: String): Long? {
        return cache.getOrPut(project) { request(project) }
    }

    protected abstract fun request(project: String): Long?
}
