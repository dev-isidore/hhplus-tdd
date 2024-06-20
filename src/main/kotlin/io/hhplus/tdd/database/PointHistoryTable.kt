package io.hhplus.tdd.database

import io.hhplus.tdd.point.PointHistoryEntity
import io.hhplus.tdd.point.TransactionType
import org.springframework.stereotype.Component

/**
 * 해당 Table 클래스는 변경하지 않고 공개된 API 만을 사용해 데이터를 제어합니다.
 */
@Component
class PointHistoryTable {
    private val table = mutableListOf<PointHistoryEntity>()
    private var cursor: Long = 1L

    fun insert(
        id: Long,
        amount: Long,
        transactionType: TransactionType,
        updateMillis: Long,
    ): PointHistoryEntity {
        Thread.sleep(Math.random().toLong() * 300L)
        val history = PointHistoryEntity(
            id = cursor++,
            userId = id,
            amount = amount,
            type = transactionType,
            timeMillis = updateMillis,
        )
        table.add(history)
        return history
    }

    fun selectAllByUserId(userId: Long): List<PointHistoryEntity> {
        return table.filter { it.userId == userId }
    }
}