package de.dkb.api.codeChallenge.notification.model.dto

import java.util.UUID

/**
 * DTO for backward-compatible user registration.
 * Accepts notification types as before, but backend stores categories.
 */
data class RegisterUserDto(
    val id: UUID,
    val notifications: List<String>
)