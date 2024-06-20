package io.hhplus.tdd.database

import io.hhplus.tdd.point.UserPoint
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class UserPointTableRepository(@Autowired private val userPointTable: UserPointTable):UserPointRepository {
    override fun selectById(id: Long): UserPoint {
        return userPointTable.selectById(id).mapToUserPoint()
    }

    override fun insertOrUpdate(id: Long, amount: Long): UserPoint {
        return userPointTable.insertOrUpdate(id, amount).mapToUserPoint()
    }
}