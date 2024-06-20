package io.hhplus.tdd.point

data class UserPointEntity(
    val id: Long,
    val point: Long,
    val updateMillis: Long,
) {
    fun mapToUserPoint(): UserPoint {
        return UserPoint(id, point, updateMillis)
    }
}
