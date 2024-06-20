package io.hhplus.tdd.point

data class UserPointEntity(
    val id: Long,
    val point: Long,
    val updateMillis: Long,
) {
    companion object {
        fun of(userPoint: UserPoint): UserPointEntity {
            return UserPointEntity(id = userPoint.id, point = userPoint.point, updateMillis = userPoint.updateMillis)
        }
    }

    fun mapToUserPoint(): UserPoint {
        return UserPoint(id, point, updateMillis)
    }
}
