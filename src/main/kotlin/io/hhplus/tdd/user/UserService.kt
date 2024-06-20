package io.hhplus.tdd.user

import io.hhplus.tdd.database.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService (
    @Autowired private val userRepository: UserRepository
){
    fun checkUserExists(id: Long): Boolean {
        return userRepository.findById(id) != null
    }
}