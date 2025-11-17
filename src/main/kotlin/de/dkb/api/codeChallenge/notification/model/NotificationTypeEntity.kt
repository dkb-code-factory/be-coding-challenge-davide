package de.dkb.api.codeChallenge.notification.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table


/**
 * Represents a notification type (type1, type2…) as stored in the DB.
 */
@Entity
@Table(name = "notification_types")
data class NotificationTypeEntity(

    @Id
    @Column(name = "id", length = 50)
    val id: String,

    @Column(name = "category", nullable = false, length = 50)
    val category: String,
)
