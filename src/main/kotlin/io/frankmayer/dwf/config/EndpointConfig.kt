package io.frankmayer.dwf.config

import java.time.ZoneId

data class EndpointConfig(
    val path: String = "j",
    val url: String = "http://localhost/jenkins",
    val type: EndpointType = EndpointType.JENKINS,
    val timezone: ZoneId = ZoneId.systemDefault(),
)
