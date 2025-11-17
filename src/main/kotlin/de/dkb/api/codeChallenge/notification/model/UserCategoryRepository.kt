package de.dkb.api.codeChallenge.notification.model

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserCategoryRepository : JpaRepository<UserCategoryEntity, Long> {

    fun findAllByUserId(userId: UUID): List<UserCategoryEntity>
}