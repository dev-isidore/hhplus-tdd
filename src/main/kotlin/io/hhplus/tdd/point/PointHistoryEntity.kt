package io.hhplus.tdd.point

data class PointHistoryEntity(
    val id: Long,
    val userId: Long,
    val type: TransactionType,
    val amount: Long,
    val timeMillis: Long,
) {
    companion object {
        fun of(pointHistory: PointHistory): PointHistoryEntity {
            return PointHistoryEntity(
                id = pointHistory.id,
                userId = pointHistory.userId,
                type = pointHistory.type,
                amount = pointHistory.amount,
                timeMillis = pointHistory.timeMillis
            )
        }
    }

    fun mapToPointHistory(): PointHistory {
        return PointHistory(id, userId, type, amount, timeMillis)
    }
}