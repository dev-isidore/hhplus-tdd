package io.hhplus.tdd.point.service

import io.hhplus.tdd.point.service.model.PointHistory
import io.hhplus.tdd.point.service.model.TransactionType
import io.hhplus.tdd.point.service.model.UserPoint
import io.hhplus.tdd.point.repository.PointHistoryRepository
import io.hhplus.tdd.point.repository.UserPointRepository
import io.hhplus.tdd.point.exception.NegativeAmountException
import io.hhplus.tdd.user.exception.UserNotFoundException
import io.hhplus.tdd.user.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@Service
class PointService(
    @Autowired private val userService: UserService,
    @Autowired private val userPointRepository: UserPointRepository,
    @Autowired private val pointHistoryRepository: PointHistoryRepository,
) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)
    private val concurrentLockMap: ConcurrentHashMap<Long, ReentrantLock> = ConcurrentHashMap()

    fun getCurrentUserPoint(id: Long): UserPoint {
        checkUser(id)
        return userPointRepository.selectById(id)
    }

    fun getUserPointHistories(id: Long): List<PointHistory> {
        checkUser(id)
        return pointHistoryRepository.selectAllByUserId(id)
    }

    fun chargeUserPoint(id: Long, amount: Long): UserPoint {
        val lock = concurrentLockMap.computeIfAbsent(id) { ReentrantLock() }
        lock.withLock {
            checkAmount(amount)
            val currentUserPoint = getCurrentUserPoint(id)
            val chargeUserPoint = userPointRepository.insertOrUpdate(id, currentUserPoint.charge(amount).point)
            pointHistoryRepository.insert(id, amount, TransactionType.CHARGE, chargeUserPoint.updateMillis)
            return chargeUserPoint
        }
    }

    fun useUserPoint(id: Long, amount: Long): UserPoint {
        val lock = concurrentLockMap.computeIfAbsent(id) { ReentrantLock() }
        lock.withLock {
            checkAmount(amount)
            val currentUserPoint = getCurrentUserPoint(id)
            val usedUserPoint = userPointRepository.insertOrUpdate(id, currentUserPoint.use(amount).point)
            pointHistoryRepository.insert(id, amount, TransactionType.USE, usedUserPoint.updateMillis)
            return usedUserPoint
        }
    }

    private fun checkUser(id: Long) {
        if(!userService.checkUserExists(id)) {
            logger.warn("User $id does not exist")
            throw UserNotFoundException("$id does not exist.")
        }
    }

    private fun checkAmount(amount: Long) {
        if(amount < 0) {
            throw NegativeAmountException("amount:$amount cannot be negative")
        }
    }
}