package io.hhplus.tdd.database

import io.hhplus.tdd.point.PointHistory
import io.hhplus.tdd.point.TransactionType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class PointHistoryTableRepository(@Autowired private val pointHistoryTable: PointHistoryTable) : PointHistoryRepository {
    override fun insert(id: Long, amount: Long, transactionType: TransactionType, updateMillis: Long): PointHistory {
        return pointHistoryTable.insert(id, amount, transactionType, updateMillis).mapToPointHistory()
    }

    override fun selectAllByUserId(userId: Long): List<PointHistory> {
        return pointHistoryTable.selectAllByUserId(userId).map{it.mapToPointHistory() }
    }
}