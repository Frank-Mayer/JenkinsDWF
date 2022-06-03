package io.frankmayer.dwf.config

data class EndpointConfig(
    val path: String = "j",
    val url: String = "http://localhost/jenkins",
    val type: EndpointType = EndpointType.JENKINS,
)
