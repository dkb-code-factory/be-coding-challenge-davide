package de.dkb.api.codeChallenge.notification.model.dto

import de.dkb.api.codeChallenge.notification.model.enumeration.NotificationType
import java.util.UUID

data class NotificationDto(
    val userId: UUID,
    val notificationType: NotificationType,
    val message: String,
)