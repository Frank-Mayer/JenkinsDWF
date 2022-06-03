package io.frankmayer.dwf.config

data class Config (
    val address: String = "localhost",
    val port: UInt = 8080u,
    val basepath: String = "/",
    val debug: Boolean = false,
    val locale: String = "en",
    val timezone: String = "UTC",
    val endpoints: List<EndpointConfig> = listOf(EndpointConfig())
)
