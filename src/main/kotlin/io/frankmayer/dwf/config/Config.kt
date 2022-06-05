package io.frankmayer.dwf.config

import java.time.ZoneId

data class Config(
    val address: String = "localhost",
    val port: UInt = 8080u,
    val basepath: String = "/",
    val debug: Boolean = false,
    val endpoints: List<EndpointConfig> = listOf(EndpointConfig())
) {
    companion object {
        @JvmStatic
        var instance: Config? = null
    }
}
