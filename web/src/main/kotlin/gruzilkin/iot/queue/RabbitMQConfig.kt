package gruzilkin.iot.queue

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQConfig {
    @Bean
    fun myQueue(): Queue {
        return Queue("sensor.data", true)
    }

    @Bean
    fun exchange(): TopicExchange {
        return TopicExchange("amq.topic")
    }

    @Bean
    fun binding(queue: Queue, exchange: TopicExchange): Binding {
        return BindingBuilder.bind(queue).to(exchange).with("sensor.data.#")
    }
}