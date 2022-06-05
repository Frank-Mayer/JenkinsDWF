package io.frankmayer.dwf.lib

import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

class CacheMap<K, V>(private val expired: (K, Duration) -> Boolean) {
    private val cache = ConcurrentHashMap<K, V>()
    private val lastWriteAccess = ConcurrentHashMap<K, Long>()

    private fun updateLastWriteAccess(key: K) {
        lastWriteAccess[key] = System.currentTimeMillis()
    }

    private fun clearExpired(key: K) {
        if (lastWriteAccess.containsKey(key) && expired(
                key,
                Duration.ofMillis(System.currentTimeMillis() - lastWriteAccess[key]!!)
            )
        ) {
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

    fun has(key: K): Boolean {
        clearExpired(key)
        return cache.containsKey(key)
    }

    fun get(key: K): V? {
        clearExpired(key)
        return cache[key]
    }
}
