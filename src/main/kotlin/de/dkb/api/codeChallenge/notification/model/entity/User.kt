package de.dkb.api.codeChallenge.notification.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "users")
data class User(
    @Id
    @Column(columnDefinition = "uuid")
    val id: UUID

    /**
     * TODO (Production Refactor - Removal of `notifications` field):
     *
     *    Identify all direct usages of `users.notifications`:
     *    - Service layer
     *    - Controllers exposing or mutating notification preferences.
     *    - Batch jobs or scheduled tasks referencing the field.
     *    - Reporting queries or analytics jobs.
     *    - Confirm no Kafka producers still emit events containing `notifications`.
     *    - Confirm consumers no longer depend on this property.
     *    - Check that no active pipelines or dashboards read the column.
     */


) {

    // Default constructor for Hibernate
    constructor() : this(UUID.randomUUID())
}