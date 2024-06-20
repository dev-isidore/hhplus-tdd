package io.hhplus.tdd.database

import io.hhplus.tdd.user.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class UserTableRepository(@Autowired private val userTable: UserTable) : UserRepository {
    override fun findById(id: Long): User? {
        return userTable.findById(id)?.mapToUser()
    }

    override fun insert(name: String): User {
        return userTable.insert(name).mapToUser()
    }
}