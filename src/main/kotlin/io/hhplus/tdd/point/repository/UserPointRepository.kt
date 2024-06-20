package io.hhplus.tdd.point.repository

import io.hhplus.tdd.point.service.model.UserPoint

interface UserPointRepository {
    fun selectById(id: Long): UserPoint
    fun insertOrUpdate(id: Long, amount: Long): UserPoint
}