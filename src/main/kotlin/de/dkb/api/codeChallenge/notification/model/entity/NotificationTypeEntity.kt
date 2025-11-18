package de.dkb.api.codeChallenge.notification.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "notification_types")
data class NotificationTypeEntity(

    @Id
    @Column(name = "id", length = 50)
    val id: String = "",

    @Column(name = "category", nullable = false, length = 50)
    val category: String = "",

    @Column(name = "created_at", insertable = false, updatable = false)
    val createdAt: Instant = Instant.now()
) {
    // Default constructor for JPA
    constructor() : this("", "", Instant.now())
}