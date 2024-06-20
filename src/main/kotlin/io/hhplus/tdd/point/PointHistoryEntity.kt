package io.hhplus.tdd.point

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