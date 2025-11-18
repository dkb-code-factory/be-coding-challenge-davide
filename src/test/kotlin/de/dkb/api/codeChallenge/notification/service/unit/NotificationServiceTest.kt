package de.dkb.api.codeChallenge.notification.service.unit

import de.dkb.api.codeChallenge.notification.model.dto.NotificationDto
import de.dkb.api.codeChallenge.notification.model.dto.RegisterUserDto
import de.dkb.api.codeChallenge.notification.model.entity.NotificationTypeEntity
import de.dkb.api.codeChallenge.notification.model.entity.User
import de.dkb.api.codeChallenge.notification.model.enumeration.NotificationType
import de.dkb.api.codeChallenge.notification.repository.NotificationTypeRepository
import de.dkb.api.codeChallenge.notification.repository.UserRepository
import de.dkb.api.codeChallenge.notification.service.CategoryService
import de.dkb.api.codeChallenge.notification.service.NotificationService
import de.dkb.api.codeChallenge.notification.kafka.KafkaNotificationPublisher
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import java.util.*

@ActiveProfiles("test")
class NotificationServiceTest {

    private lateinit var userRepository: UserRepository
    private lateinit var categoryService: CategoryService
    private lateinit var notificationTypeRepository: NotificationTypeRepository
    private lateinit var kafkaPublisher: KafkaNotificationPublisher
    private lateinit var notificationService: NotificationService

    @BeforeEach
    fun setup() {
        userRepository = mockk()
        categoryService = mockk()
        notificationTypeRepository = mockk()
        kafkaPublisher = mockk()

        notificationService = NotificationService(
            userRepository,
            categoryService,
            notificationTypeRepository,
            kafkaPublisher
        )
    }

    @Test
    fun `registerUserWithTypes saves new user and assigns categories`() {
        val userId = UUID.randomUUID()
        val dto = RegisterUserDto(userId, listOf("type1"))

        every { userRepository.existsById(userId) } returns false
        every { userRepository.save(any<User>()) } returnsArgument 0
        every { notificationTypeRepository.findById("type1") } returns Optional.of(
            NotificationTypeEntity(
                id = "type1",
                category = "CATEGORY_A"
            )
        )
        every { categoryService.assignCategoriesToUser(userId, any()) } just Runs

        notificationService.registerUserWithTypes(dto)

        verify { userRepository.save(match { it.id == userId }) }
        verify { categoryService.assignCategoriesToUser(userId, listOf("CATEGORY_A")) }
    }

    @Test
    fun `sendNotification skips unknown user`() {
        val userId = UUID.randomUUID()
        val dto = NotificationDto(userId, NotificationType.type1, "Message")

        every { userRepository.existsById(userId) } returns false

        notificationService.sendNotification(dto)

        verify { categoryService wasNot Called }
        verify { kafkaPublisher wasNot Called }
    }

    @Test
    fun `sendNotification skips user not allowed`() {
        val userId = UUID.randomUUID()
        val dto = NotificationDto(userId, NotificationType.type1, "Message")

        every { userRepository.existsById(userId) } returns true
        every { categoryService.isUserAllowedForType(userId, "type1") } returns false

        notificationService.sendNotification(dto)

        verify { categoryService.isUserAllowedForType(userId, "type1") }
        verify { kafkaPublisher wasNot Called }
    }

    @Test
    fun `sendNotification publishes to Kafka when user is allowed`() {
        val userId = UUID.randomUUID()
        val dto = NotificationDto(userId, NotificationType.type1, "Message")

        every { userRepository.existsById(userId) } returns true
        every { categoryService.isUserAllowedForType(userId, "type1") } returns true
        every { kafkaPublisher.publish(userId, "type1", "Message") } just Runs

        notificationService.sendNotification(dto)

        verify { kafkaPublisher.publish(userId, "type1", "Message") }
    }
}