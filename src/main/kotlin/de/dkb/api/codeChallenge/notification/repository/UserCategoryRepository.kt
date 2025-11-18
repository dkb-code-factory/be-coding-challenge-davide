package de.dkb.api.codeChallenge.notification.repository

import de.dkb.api.codeChallenge.notification.model.entity.UserCategoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserCategoryRepository : JpaRepository<UserCategoryEntity, Long> {

    /**
     * Returns true if the user is allowed to receive a notification of the given type.
     */
    @Query(
        value = """
        SELECT EXISTS (
            SELECT 1
            FROM user_categories uc
            WHERE uc.user_id = :userId
            AND uc.category =(
            SELECT nt.category
            FROM notification_types nt
            WHERE nt.id = :type
            )
        )
    """,
        nativeQuery = true
    )
    fun isUserAllowedForType(userId: UUID, type: String): Boolean

    fun existsByUserIdAndCategory(userId: UUID, category: String): Boolean
}