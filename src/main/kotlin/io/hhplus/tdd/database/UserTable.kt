package io.hhplus.tdd.database

import io.hhplus.tdd.user.User
import org.springframework.stereotype.Component

@Component
class UserTable : UserRepository {
    private val table = HashMap<Long, User>()

    override fun findById(id: Long): User? {
        return table[id]
    }

    override fun insert(name: String): User {
        val id = table.size.toLong()
        val user = User(id = id, name = name)
        table[id] = user
        return user
    }
}