package io.frankmayer.dwf.endpoint

interface Endpoint {
    fun get(project: String): Long?
}
