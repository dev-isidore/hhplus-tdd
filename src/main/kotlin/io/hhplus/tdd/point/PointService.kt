package io.hhplus.tdd.point

import io.hhplus.tdd.database.PointHistoryRepository
import io.hhplus.tdd.database.UserPointRepository
import io.hhplus.tdd.user.exception.UserNotFoundException
import io.hhplus.tdd.user.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PointService(
    @Autowired private val userService: UserService,
    @Autowired private val userPointRepository: UserPointRepository,
    @Autowired private val pointHistoryRepository: PointHistoryRepository,
) {
    fun getCurrentUserPoint(id: Long): UserPoint {
        if(!userService.checkUserExists(id)) {
            throw UserNotFoundException("$id does not exist.")
        }
        return userPointRepository.selectById(id)
    }

    fun getUserPointHistories(id: Long): List<PointHistory> {
        if(!userService.checkUserExists(id)) {
            throw UserNotFoundException("$id does not exist.")
        }
        return pointHistoryRepository.selectAllByUserId(id)
    }
}