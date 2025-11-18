package de.dkb.api.codeChallenge.notification.repository

import de.dkb.api.codeChallenge.notification.model.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<User, UUID>