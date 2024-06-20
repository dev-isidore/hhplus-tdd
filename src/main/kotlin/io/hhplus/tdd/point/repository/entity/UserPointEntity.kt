package io.hhplus.tdd.point.repository.entity

import io.hhplus.tdd.point.service.model.UserPoint

data class UserPointEntity(
    val id: Long,
    val point: Long,
    val updateMillis: Long,
) {
    fun mapToUserPoint(): UserPoint {
        return UserPoint(id, point, updateMillis)
    }
}
