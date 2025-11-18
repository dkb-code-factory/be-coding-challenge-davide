package de.dkb.api.codeChallenge.notification.service

import de.dkb.api.codeChallenge.notification.repository.NotificationTypeRepository
import de.dkb.api.codeChallenge.notification.model.entity.UserCategoryEntity
import de.dkb.api.codeChallenge.notification.repository.UserCategoryRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*


@Service
class CategoryService(
    private val userCategoryRepository: UserCategoryRepository,
    private val notificationTypeRepository: NotificationTypeRepository
) {

    private val logger = LoggerFactory.getLogger(CategoryService::class.java)

    /**
     * Determines if a user is allowed to receive a notification of a given type.
     */
    @Transactional(readOnly = true)
    fun isUserAllowedForType(userId: UUID, type: String): Boolean {
        val allowed = userCategoryRepository.isUserAllowedForType(userId, type)

        if (!allowed) {
            if (!notificationTypeRepository.existsById(type)) {
                logger.warn("Unknown notification type={} for userId={}", type, userId)
            } else {
                logger.debug("UserId={} not subscribed to category for type={}", userId, type)
            }
        }

        return allowed
    }

    /**
     * Assign a list of categories to a user.
     * Ignores duplicates.
     */
    @Transactional
    fun assignCategoriesToUser(userId: UUID, categories: List<String>) {
        categories.forEach { category ->
            if (!userCategoryRepository.existsByUserIdAndCategory(userId, category)) {
                val entity = UserCategoryEntity(userId = userId, category = category)
                userCategoryRepository.save(entity)
                logger.info("Assigned category={} to user={}", category, userId)
            }
        }
    }
}
