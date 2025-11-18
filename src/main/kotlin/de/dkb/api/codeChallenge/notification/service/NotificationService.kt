package de.dkb.api.codeChallenge.notification.service

import de.dkb.api.codeChallenge.notification.kafka.KafkaNotificationPublisher
import de.dkb.api.codeChallenge.notification.model.entity.User
import de.dkb.api.codeChallenge.notification.repository.UserRepository
import de.dkb.api.codeChallenge.notification.model.dto.NotificationDto
import de.dkb.api.codeChallenge.notification.model.dto.RegisterUserDto
import de.dkb.api.codeChallenge.notification.repository.NotificationTypeRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NotificationService(
    private val userRepository: UserRepository,
    private val categoryService: CategoryService,
    private val notificationTypeRepository: NotificationTypeRepository,
    private val kafkaPublisher: KafkaNotificationPublisher
) {

    private val logger = LoggerFactory.getLogger(NotificationService::class.java)


    /**
     * Register user and assign categories derived from notification types.
     */
    @Transactional
    fun registerUserWithTypes(dto: RegisterUserDto) {
        val userId = dto.id

        // 1. Save the basic user
        if (!userRepository.existsById(userId)) {
            userRepository.save(User(id = userId))
            logger.info("Registered new user {}", userId)
        } else {
            logger.debug("User {} already exists. Skipping registration.", userId)
        }

        // 2. Convert notification types -> categories
        val categories = dto.notifications.mapNotNull { type ->
            notificationTypeRepository.findById(type).orElse(null)?.category
        }.distinct()

        if (categories.isEmpty()) {
            logger.warn("No valid categories found for user {} with types {}", userId, dto.notifications)
        }

        // 3. Assign categories to user
        categoryService.assignCategoriesToUser(userId, categories)
    }

    /**
     * Sends a notification to a user if they are allowed to receive it.
     */
    @Transactional(readOnly = true)
    fun sendNotification(notification: NotificationDto) {
        val userId = notification.userId
        val type = notification.notificationType.name
        val message = notification.message

        if (!userRepository.existsById(userId)) {
            logger.warn("Cannot send notification to unknown userId={}", userId)
            return
        }

        if (!categoryService.isUserAllowedForType(userId, type)) {
            logger.info("UserId={} not allowed for type={}. Skipping notification.", userId, type)
            return
        }

        // Logic to send notification to user
        kafkaPublisher.publish(userId, type, message)
        logger.info("Sending notification type={} to userId={}: {}", type, userId, message)
    }

}