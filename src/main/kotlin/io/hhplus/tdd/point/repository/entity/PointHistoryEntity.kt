package io.hhplus.tdd.point.repository.entity

import io.hhplus.tdd.point.service.model.PointHistory
import io.hhplus.tdd.point.service.model.TransactionType

data class PointHistoryEntity(
    val id: Long,
    val userId: Long,
    val type: TransactionType,
    val amount: Long,
    val timeMillis: Long,
) {
    fun mapToPointHistory(): PointHistory {
        return PointHistory(id, userId, type, amount, timeMillis)
    }
}