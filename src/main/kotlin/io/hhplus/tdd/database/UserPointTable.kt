package io.hhplus.tdd.database

import io.hhplus.tdd.point.UserPointEntity
import org.springframework.stereotype.Component

/**
 * 해당 Table 클래스는 변경하지 않고 공개된 API 만을 사용해 데이터를 제어합니다.
 */
@Component
class UserPointTable {
    private val table = HashMap<Long, UserPointEntity>()

    fun selectById(id: Long): UserPointEntity {
        Thread.sleep(Math.random().toLong() * 200L)
        return table[id] ?: UserPointEntity(id = id, point = 0, updateMillis = System.currentTimeMillis())
    }

    fun insertOrUpdate(id: Long, amount: Long): UserPointEntity {
        Thread.sleep(Math.random().toLong() * 300L)
        val userPointEntity = UserPointEntity(id = id, point = amount, updateMillis = System.currentTimeMillis())
        table[id] = userPointEntity
        return userPointEntity
    }
}