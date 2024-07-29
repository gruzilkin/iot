package gruzilkin.iot.queue

import com.google.gson.Gson
import gruzilkin.iot.services.SensorDataService
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component


@Component
class MessageConsumer(val sensorDataService: SensorDataService) {
    @RabbitListener(queues = ["sensor.data"])
    fun receiveMessage(message: String) {
        val event = Gson().fromJson(message, SensorDataEvent::class.java)
         sensorDataService.saveSensorData(event)
        println("Received message: $message")
    }
}