package io.frankmayer.dwf.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController


@RestController
class MainController {
    @GetMapping("/{endpoint}/{file}")
    fun index(@PathVariable endpoint: String, @PathVariable file: String): String {
        return "$endpoint/$file"
    }
}
