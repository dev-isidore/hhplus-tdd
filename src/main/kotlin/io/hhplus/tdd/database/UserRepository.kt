package io.hhplus.tdd.database

import io.hhplus.tdd.user.User

interface UserRepository {
    fun findById(id: Long): User?
    fun insert(name: String): User
}