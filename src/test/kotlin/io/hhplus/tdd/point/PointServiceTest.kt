package io.hhplus.tdd.point

import io.hhplus.tdd.database.PointHistoryRepository
import io.hhplus.tdd.database.UserPointRepository
import io.hhplus.tdd.database.UserRepository
import io.hhplus.tdd.user.User
import io.hhplus.tdd.user.UserService
import io.hhplus.tdd.user.exception.UserNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PointServiceTest {
    private val pointService = PointService(
        userService = UserServiceStub(),
        userPointRepository = UserPointRepositoryStub(),
        pointHistoryRepository = PointHistoryRepositoryStub()
    )

    //region getCurrentUserPoint
    @Test
            /**
             * 현재 포인트 조회하려는 id가 없는 경우 예외를 던진다.
             */
    fun `현재 포인트 조회하려는 id의 회원이 없는 경우`() {
        val notExistingUserId = 3L

        val exception = assertThrows<UserNotFoundException> {
            pointService.getCurrentUserPoint(notExistingUserId)
        }
        assertThat(exception).message().contains("$notExistingUserId does not exist.")
    }

    @Test
            /**
             * 현재 포인트 조회 정상 경로
             */
    fun `현재 포인트 조회`() {
        val userId = 0L

        val currentUserPoint = pointService.getCurrentUserPoint(userId)

        assertThat(currentUserPoint.id).isEqualTo(userId)
        assertThat(currentUserPoint.point).isEqualTo(1000L)
        assertThat(currentUserPoint.updateMillis).isEqualTo(1000L)
    }
    //endregion

    //region getUserPointHistories
    @Test
            /**
             * 포인트 이력 조회하려는 id의 회원이 없는 경우 예외를 던진다.
             */
    fun `포인트 이력 조회하려는 id의 회원이 없는 경우`() {
        val notExistingUserId = 3L

        val exception = assertThrows<UserNotFoundException> {
            pointService.getUserPointHistories(notExistingUserId)
        }
        assertThat(exception).message().contains("$notExistingUserId does not exist.")
    }

    @Test
            /**
             * 포인트 이력 조회시 id의 회원은 있으나 이력이 없는 경우
             */
    fun `포인트 이력 조회시 조회하려는 회원의 포인트 이력이 없는 경우`() {
        val userId = 0L

        val userPointHistories = pointService.getUserPointHistories(userId)

        assertThat(userPointHistories.size).isEqualTo(0)
    }

    @Test
            /**
             * 포인트 이력 조회 정상 경로
             */
    fun`포인트 이력 조회`() {
        val userId1 = 1L
        val userId2 = 2L

        val userPointHistories1 = pointService.getUserPointHistories(userId1)
        val userPointHistories2 = pointService.getUserPointHistories(userId2)

        assertThat(userPointHistories1.size).isEqualTo(2)
        assertThat(userPointHistories1[0]).isEqualTo(PointHistory(0L, 1L, TransactionType.CHARGE, 1000L, 1000L))
        assertThat(userPointHistories1[1]).isEqualTo(PointHistory(2L, 1L, TransactionType.CHARGE, 1200L, 1100L))
        assertThat(userPointHistories2.size).isEqualTo(1)
        assertThat(userPointHistories2[0]).isEqualTo(PointHistory(1L, 2L, TransactionType.CHARGE, 1000L, 1000L))
    }
    //endregion
}

class UserServiceStub : UserService(UserRepositoryStub()) {
    override fun checkUserExists(id: Long): Boolean {
        return when(id) {
            0L -> true
            1L -> true
            2L -> true
            else -> false
        }
    }
}

class UserPointRepositoryStub : UserPointRepository {
    override fun selectById(id: Long): UserPoint {
        return when (id) {
            0L -> UserPoint(0L, 1000L, 1000L)
            1L -> UserPoint(1L, 2000L, 2000L)
            2L -> UserPoint(2L, 2000L, 2000L)
            else -> UserPoint(-1L, -1L, -1L)
        }
    }

    override fun insertOrUpdate(id: Long, amount: Long): UserPoint {
        TODO("Not yet implemented")
    }
}

class UserRepositoryStub : UserRepository {
    override fun findById(id: Long): User? {
        TODO("Not yet implemented")
    }

    override fun insert(name: String): User {
        TODO("Not yet implemented")
    }
}

class PointHistoryRepositoryStub : PointHistoryRepository {
    override fun insert(id: Long, amount: Long, transactionType: TransactionType, updateMillis: Long): PointHistory {
        TODO("Not yet implemented")
    }

    override fun selectAllByUserId(userId: Long): List<PointHistory> {
        return when(userId) {
            0L -> emptyList()
            1L -> listOf(PointHistory(0L, 1L, TransactionType.CHARGE, 1000L, 1000L), PointHistory(2L, 1L, TransactionType.CHARGE, 1200L, 1100L))
            2L -> listOf(PointHistory(1L, 2L, TransactionType.CHARGE, 1000L, 1000L))
            else -> emptyList()
        }
    }
}