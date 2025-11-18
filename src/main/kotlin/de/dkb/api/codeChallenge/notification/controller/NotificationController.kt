package de.dkb.api.codeChallenge.notification.controller

import de.dkb.api.codeChallenge.notification.service.NotificationService
import de.dkb.api.codeChallenge.notification.model.dto.NotificationDto
import de.dkb.api.codeChallenge.notification.model.dto.RegisterUserDto
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class NotificationController(
    private val notificationService: NotificationService
) {

    @PostMapping("/register")
    fun registerUser(@RequestBody dto: RegisterUserDto) {
        notificationService.registerUserWithTypes(dto)
    }

    @PostMapping("/notify")
    fun sendNotification(@RequestBody notificationDto: NotificationDto) =
        notificationService.sendNotification(notificationDto)
}