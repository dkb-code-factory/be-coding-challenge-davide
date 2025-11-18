package de.dkb.api.codeChallenge.notification.model.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

/**
 * Stores the *category* a user subscribed to (CATEGORY_A or CATEGORY_B).
 */
@Entity
@Table(name = "user_categories")
data class UserCategoryEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    val userId: UUID = UUID.randomUUID(),

    @Column(name = "category", nullable = false, length = 50)
    val category: String = "",

    @Column(name = "subscribed_at", nullable = false)
    val subscribedAt: Instant = Instant.now(),

    ) {
    // Default constructor for JPA
    constructor() : this(null, UUID.randomUUID(), "", Instant.now())
}