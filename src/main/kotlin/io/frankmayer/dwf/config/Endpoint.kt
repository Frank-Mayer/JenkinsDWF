package io.frankmayer.dwf.config

data class Endpoint(
    val path: String = "j",
    val url: String = "http://localhost/jenkins",
    val type: EndpointType = EndpointType.JENKINS,
)
