package de.dkb.api.codeChallenge.notification.kafka

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.*

@Component
class KafkaNotificationPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>
) {
    fun publish(userId: UUID, type: String, message: String) {
        kafkaTemplate.send("notifications", userId.toString(), message)
    }
}