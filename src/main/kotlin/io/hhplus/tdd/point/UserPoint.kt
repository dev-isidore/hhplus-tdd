package io.hhplus.tdd.point

import io.hhplus.tdd.point.exception.ExcessiveAmountException

class UserPoint(
    val id: Long,
    val point: Long,
    val updateMillis: Long,
) {
    fun charge(amount: Long): UserPoint {
        return UserPoint(id, point + amount, updateMillis)
    }

    fun use(amount: Long): UserPoint {
        if (amount > this.point) {
            throw ExcessiveAmountException("amount:$amount is bigger than current point")
        }
        return UserPoint(id, point - amount, updateMillis)
    }
}