package gruzilkin.iot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.transaction.annotation.EnableTransactionManagement


@SpringBootApplication
@EnableTransactionManagement
class IotApplication

fun main(args: Array<String>) {
	runApplication<IotApplication>(*args)
}
