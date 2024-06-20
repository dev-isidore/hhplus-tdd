package io.hhplus.tdd.point.dto.response

import io.hhplus.tdd.point.service.model.UserPoint

data class UserPointResponse(
    val id: Long,
    val point: Long,
    val updateMillis: Long,
) {
    companion object {
        fun of(userPoint: UserPoint): UserPointResponse {
            return UserPointResponse(
                id = userPoint.id,
                point = userPoint.point,
                updateMillis = userPoint.updateMillis
            )
        }
    }
}