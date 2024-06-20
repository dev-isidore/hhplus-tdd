package io.hhplus.tdd.user.repository

import io.hhplus.tdd.user.service.model.User

interface UserRepository {
    fun findById(id: Long): User?
    fun insert(name: String): User
}