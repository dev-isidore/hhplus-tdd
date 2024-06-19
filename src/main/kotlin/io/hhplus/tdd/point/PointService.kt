package io.hhplus.tdd.point

import io.hhplus.tdd.database.PointHistoryRepository
import io.hhplus.tdd.database.UserPointRepository
import io.hhplus.tdd.point.exception.NegativeAmountException
import io.hhplus.tdd.user.exception.UserNotFoundException
import io.hhplus.tdd.user.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PointService(
    @Autowired private val userService: UserService,
    @Autowired private val userPointRepository: UserPointRepository,
    @Autowired private val pointHistoryRepository: PointHistoryRepository,
) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    fun getCurrentUserPoint(id: Long): UserPoint {
        if(!userService.checkUserExists(id)) {
            logger.warn("User $id does not exist")
            throw UserNotFoundException("$id does not exist.")
        }
        return userPointRepository.selectById(id)
    }

    fun getUserPointHistories(id: Long): List<PointHistory> {
        if(!userService.checkUserExists(id)) {
            logger.warn("User $id does not exist")
            throw UserNotFoundException("$id does not exist.")
        }
        return pointHistoryRepository.selectAllByUserId(id)
    }

    fun chargeUserPoint(id: Long, amount: Long): UserPoint {
        if(amount < 0) {
            throw NegativeAmountException("amount:$amount cannot be negative")
        }
        val currentUserPoint = getCurrentUserPoint(id)
        val chargeUserPoint = userPointRepository.insertOrUpdate(id, currentUserPoint.charge(amount).point)
        pointHistoryRepository.insert(id, amount, TransactionType.CHARGE, chargeUserPoint.updateMillis)
        return chargeUserPoint
    }

    fun useUserPoint(id: Long, amount: Long): UserPoint {
        if(amount < 0) {
            throw NegativeAmountException("amount:$amount cannot be negative")
        }
        val currentUserPoint = getCurrentUserPoint(id)
        val usedUserPoint = userPointRepository.insertOrUpdate(id, currentUserPoint.use(amount).point)
        pointHistoryRepository.insert(id, amount, TransactionType.USE, usedUserPoint.updateMillis)
        return usedUserPoint
    }
}