package io.frankmayer.dwf.lib

import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

class CacheMap<K, V>(private val expired: (K, Duration) -> Boolean) {
    private val cache = HashMap<K, V>()
    private val lastWriteAccess = HashMap<K, Long>()

    private fun updateLastWriteAccess(key: K) {
        lastWriteAccess[key] = System.currentTimeMillis()
    }

    private fun clearExpired(key: K) {
        if (lastWriteAccess.containsKey(key) && expired(key, Duration.ofMillis(System.currentTimeMillis() - lastWriteAccess[key]!!))) {
            remove(key)
        }
    }

    fun put(key: K, value: V) {
        cache[key] = value
        updateLastWriteAccess(key)
    }

    fun remove(key: K) {
        if (cache.containsKey(key)) {
            cache.remove(key)
        }
        if (lastWriteAccess.containsKey(key)) {
            lastWriteAccess.remove(key)
        }
    }

    fun clear() {
        cache.clear()
        lastWriteAccess.clear()
    }

    fun getOrPut(key: K, value: () -> V): V {
        clearExpired(key)

        return cache.getOrPut(key) {
            val v = value()
            updateLastWriteAccess(key)
            v
        }
    }
}
