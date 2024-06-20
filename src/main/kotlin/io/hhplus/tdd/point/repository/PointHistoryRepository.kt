package io.hhplus.tdd.point.repository

import io.hhplus.tdd.point.service.model.PointHistory
import io.hhplus.tdd.point.service.model.TransactionType

interface PointHistoryRepository {
    fun insert(id: Long, amount: Long, transactionType: TransactionType, updateMillis: Long): PointHistory
    fun selectAllByUserId(userId: Long): List<PointHistory>
}