package io.hhplus.tdd.user.repository.entity

import io.hhplus.tdd.user.service.model.User

data class UserEntity(val id: Long, val name: String) {
    fun mapToUser(): User {
        return User(id, name)
    }
}