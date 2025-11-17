package de.dkb.api.codeChallenge.notification.model


import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NotificationTypeRepository : JpaRepository<NotificationTypeEntity, String> {

    fun findAllByCategory(category: String): List<NotificationTypeEntity>
}