package io.hhplus.tdd.point.dto.response

import io.hhplus.tdd.point.service.model.PointHistory

data class PointHistoryResponse(
    val id: Long,
    val userId: Long,
    val type: String,
    val amount: Long,
    val timeMillis: Long,
) {
    companion object {
        fun of(pointHistory: PointHistory): PointHistoryResponse {
            return PointHistoryResponse(
                id = pointHistory.id,
                userId = pointHistory.userId,
                type = pointHistory.type.name,
                amount = pointHistory.amount,
                timeMillis = pointHistory.timeMillis,
            )
        }
    }
}