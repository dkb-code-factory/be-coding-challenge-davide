package de.dkb.api.codeChallenge.notification.service.integration

import de.dkb.api.codeChallenge.notification.model.dto.NotificationDto
import de.dkb.api.codeChallenge.notification.model.dto.RegisterUserDto
import de.dkb.api.codeChallenge.notification.model.entity.NotificationTypeEntity
import de.dkb.api.codeChallenge.notification.model.entity.User
import de.dkb.api.codeChallenge.notification.model.enumeration.NotificationType
import de.dkb.api.codeChallenge.notification.repository.NotificationTypeRepository
import de.dkb.api.codeChallenge.notification.repository.UserRepository
import de.dkb.api.codeChallenge.notification.service.NotificationService
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.test.context.ActiveProfiles
import java.time.Duration
import java.util.*
import kotlin.test.assertEquals

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = ["notifications"])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NotificationServiceKafkaIntegrationTest {

    @Autowired
    lateinit var notificationService: NotificationService

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var notificationTypeRepository: NotificationTypeRepository

    @Autowired
    lateinit var embeddedKafkaBroker: EmbeddedKafkaBroker

    private lateinit var consumer: KafkaConsumer<String, String>

    @BeforeAll
    fun setupKafkaConsumer() {
        val props = KafkaTestUtils.consumerProps("test-group", "true", embeddedKafkaBroker)

        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"

        consumer = KafkaConsumer(props)
        consumer.subscribe(listOf("notifications"))
    }

    @BeforeEach
    fun cleanDatabase() {
        userRepository.deleteAll()
        notificationTypeRepository.deleteAll()
    }

    @AfterAll
    fun tearDown() {
        consumer.close()
    }

    @Test
    fun `should send notification to Kafka when user is allowed`() {
        // GIVEN a registered user + type
        val userId = UUID.randomUUID()
        val type = "type1"

        userRepository.save(User(id = userId))
        notificationTypeRepository.save(NotificationTypeEntity(id = type, category = "CATEGORY_A"))

        notificationService.registerUserWithTypes(
            RegisterUserDto(
                id = userId,
                notifications = listOf(type)
            )
        )

        // WHEN sending Kafka message
        val messageContent = "Test Kafka message"
        val dto = NotificationDto(
            userId = userId,
            notificationType = NotificationType.valueOf(type.lowercase()),
            message = messageContent
        )
        notificationService.sendNotification(dto)

        // THEN message should appear in Kafka
        val record = KafkaTestUtils.getSingleRecord(consumer, "notifications", Duration.ofSeconds(5))

        assertEquals(messageContent, record.value())
    }
}