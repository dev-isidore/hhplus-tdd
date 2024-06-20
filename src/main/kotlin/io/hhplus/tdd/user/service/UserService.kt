package io.hhplus.tdd.user.service

import io.hhplus.tdd.user.repository.UserRepository
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