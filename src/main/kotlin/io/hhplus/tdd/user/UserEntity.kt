package io.hhplus.tdd.user

data class UserEntity(val id: Long, val name: String) {
    fun mapToUser(): User {
        return User(id, name)
    }
}