package io.hhplus.tdd.user.repository

import io.hhplus.tdd.user.repository.entity.UserEntity
import org.springframework.stereotype.Component

@Component
class UserTable {
    private val table = HashMap<Long, UserEntity>()

    fun findById(id: Long): UserEntity? {
        return table[id]
    }

    fun insert(name: String): UserEntity {
        val id = table.size.toLong()
        val user = UserEntity(id = id, name = name)
        table[id] = user
        return user
    }
}