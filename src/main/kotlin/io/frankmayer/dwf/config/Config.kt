package io.frankmayer.dwf.config

import java.time.ZoneId

data class Config(
    var address: String = "localhost",
    var port: Int = 8080,
    var basepath: String = "/",
    var logLevel: String = "WARN",
    var endpoints: List<EndpointConfig> = listOf(EndpointConfig())
) {
    companion object {
        @JvmStatic
        var instance: Config? = null
    }
}
