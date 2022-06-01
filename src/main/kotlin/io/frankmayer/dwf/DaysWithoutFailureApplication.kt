package io.frankmayer.dwf

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DaysWithoutFailureApplication

fun main(args: Array<String>) {
	runApplication<DaysWithoutFailureApplication>(*args)
}
