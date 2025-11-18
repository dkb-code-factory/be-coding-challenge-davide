package de.dkb.api.codeChallenge.notification.service.unit

import de.dkb.api.codeChallenge.notification.model.entity.UserCategoryEntity
import de.dkb.api.codeChallenge.notification.repository.NotificationTypeRepository
import de.dkb.api.codeChallenge.notification.repository.UserCategoryRepository
import de.dkb.api.codeChallenge.notification.service.CategoryService
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import java.util.*
import kotlin.test.assertTrue

@ActiveProfiles("test")
class CategoryServiceTest {

    private lateinit var userCategoryRepository: UserCategoryRepository
    private lateinit var notificationTypeRepository: NotificationTypeRepository
    private lateinit var categoryService: CategoryService

    @BeforeEach
    fun setup() {
        userCategoryRepository = mockk()
        notificationTypeRepository = mockk()
        categoryService = CategoryService(userCategoryRepository, notificationTypeRepository)
    }

    @Test
    fun `isUserAllowedForType returns true when allowed`() {
        val userId = UUID.randomUUID()
        every { userCategoryRepository.isUserAllowedForType(userId, "type1") } returns true

        val result = categoryService.isUserAllowedForType(userId, "type1")

        assertTrue(result)
        verify { userCategoryRepository.isUserAllowedForType(userId, "type1") }
    }

    @Test
    fun `isUserAllowedForType when type unknown`() {
        val userId = UUID.randomUUID()
        every { userCategoryRepository.isUserAllowedForType(userId, "unknownType") } returns false
        every { notificationTypeRepository.existsById("unknownType") } returns false

        categoryService.isUserAllowedForType(userId, "unknownType")
    }


    @Test
    fun `assignCategoriesToUser saves new categories only`() {
        val userId = UUID.randomUUID()
        val categories = listOf("CATEGORY_A", "CATEGORY_B")

        every { userCategoryRepository.existsByUserIdAndCategory(userId, any()) } returnsMany listOf(false, true)
        every { userCategoryRepository.save(any<UserCategoryEntity>()) } returnsArgument 0

        categoryService.assignCategoriesToUser(userId, categories)

        verify(exactly = 1) { userCategoryRepository.save(match { it.category == "CATEGORY_A" }) }
        verify(exactly = 0) { userCategoryRepository.save(match { it.category == "CATEGORY_B" }) }
    }
}