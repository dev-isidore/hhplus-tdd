package io.hhplus.tdd.database

import io.hhplus.tdd.user.User
import io.hhplus.tdd.user.UserEntity
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