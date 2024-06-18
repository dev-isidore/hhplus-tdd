package io.hhplus.tdd.point

data class UserPoint(
    val id: Long,
    val point: Long,
    val updateMillis: Long,
) {
    fun charge(amount: Long): UserPoint {
        return UserPoint(id, point + amount, updateMillis)
    }
}
