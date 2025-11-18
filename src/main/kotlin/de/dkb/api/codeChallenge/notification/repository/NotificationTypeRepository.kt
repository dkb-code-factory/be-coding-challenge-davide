package de.dkb.api.codeChallenge.notification.repository

import de.dkb.api.codeChallenge.notification.model.entity.NotificationTypeEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NotificationTypeRepository : JpaRepository<NotificationTypeEntity, String> {}