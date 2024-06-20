package io.hhplus.tdd.point

import io.hhplus.tdd.database.PointHistoryRepository
import io.hhplus.tdd.database.UserPointRepository
import io.hhplus.tdd.database.UserRepository
import io.hhplus.tdd.point.exception.NegativeAmountException
import io.hhplus.tdd.user.User
import io.hhplus.tdd.user.UserService
import io.hhplus.tdd.user.exception.UserNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PointServiceTest {
    companion object {
        val NOT_EXISITING_USER_ID = 3L
    }

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
        val exception = assertThrows<UserNotFoundException> {
            pointService.getCurrentUserPoint(NOT_EXISITING_USER_ID)
        }
        assertThat(exception).message().contains("$NOT_EXISITING_USER_ID does not exist.")
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
        val exception = assertThrows<UserNotFoundException> {
            pointService.getUserPointHistories(NOT_EXISITING_USER_ID)
        }
        assertThat(exception).message().contains("$NOT_EXISITING_USER_ID does not exist.")
    }

    @Test
            /**
             * 포인트 이력 조회시 id의 회원은 있으나 이력이 없는 경우 빈 리스트가 응답된다.
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
        assertThat(userPointHistories1[0]).usingRecursiveComparison().isEqualTo(PointHistory(0L, 1L, TransactionType.CHARGE, 1000L, 1000L))
        assertThat(userPointHistories1[1]).usingRecursiveComparison().isEqualTo(PointHistory(2L, 1L, TransactionType.CHARGE, 1000L, 1100L))
        assertThat(userPointHistories2.size).isEqualTo(1)
        assertThat(userPointHistories2[0]).usingRecursiveComparison().isEqualTo(PointHistory(1L, 2L, TransactionType.CHARGE, 2000L, 1200L))
    }
    //endregion

    //region chargeUserPoint
    @Test
            /**
             * 포인트 충전시 존재하지 않는 id로 시도시 예외를 던진다.
             */
    fun `포인트 충전시 존재하지 않는 아이디로 시도하는 경우`() {
        val exception = assertThrows<UserNotFoundException> {
            pointService.chargeUserPoint(NOT_EXISITING_USER_ID, 1000L)
        }
        assertThat(exception).message().contains("$NOT_EXISITING_USER_ID does not exist.")
    }

    @Test
            /**
             * 포인트 충전시 충전하려는 양이 음수인 경우 예외를 던진다.
             */
    fun `포인트 충전시 충전하려는 양이 음수인 경우`() {
        val amount = -1000L

        val exception = assertThrows<NegativeAmountException> {
            pointService.chargeUserPoint(0L, amount)
        }
        assertThat(exception).message().contains("amount:$amount cannot be negative")
    }

    @Test
            /**
             * 포인트 충전시 정상 경로를 따른다.
             */
    fun `포인트 충전시 정상 경로`() {
        val userId = 0L
        val currentUserPoint = UserPoint(0L, 1000L, 1000L)
        val amount = TEST_PARAM_AMOUNT

        val chargeUserPoint = pointService.chargeUserPoint(userId, amount)

        assertThat(chargeUserPoint.point).isEqualTo(currentUserPoint.point + amount)
        assertThat(chargeUserPoint.id).isEqualTo(userId)
    }
    //endregion

    //region useUserPoint
    @Test
            /**
             * 포인트 사용시 존재하지 않는 id로 시도시 예외를 던진다.
             */
    fun `포인트 사용시 존재하지 않는 아이디로 시도하는 경우`() {
        val exception = assertThrows<UserNotFoundException> {
            pointService.useUserPoint(NOT_EXISITING_USER_ID, 1000L)
        }
        assertThat(exception).message().contains("$NOT_EXISITING_USER_ID does not exist.")
    }

    @Test
            /**
             * 포인트 사용시 사용하려는 양이 음수인 경우 예외를 던진다.
             */
    fun `포인트 사용시 사용하려는 양이 음수인 경우`() {
        val amount = -1000L

        val exception = assertThrows<NegativeAmountException> {
            pointService.useUserPoint(0L, amount)
        }
        assertThat(exception).message().contains("amount:$amount cannot be negative")
    }

    @Test
            /**
             * 포인트 사용시 사용하려는 양이 충전된 값보다 큰 경우 예외를 던진다.
             */
    fun `포인트 사용시 사용하는 양이 기존 양보다 큰 경우`() {
        val amount = 10000L

        val exception = assertThrows<NegativeAmountException> {
            pointService.useUserPoint(0L, amount)
        }
        assertThat(exception).message().contains("amount:$amount is bigger than current point")
    }

    @Test
            /**
             * 포인트 사용시 사용한 결과를 반환한다.
             */
    fun `포인트 사용`() {
        val amount = TEST_PARAM_AMOUNT

        val usedUserPoint = pointService.useUserPoint(1L, amount)

        assertThat(usedUserPoint.point).isEqualTo(2000 - TEST_PARAM_AMOUNT)
        assertThat(usedUserPoint.id).isEqualTo(1L)
    }
    //endregion
}

private const val TEST_PARAM_AMOUNT = 1100L

class UserPointRepositoryStub : UserPointRepository {
    override fun selectById(id: Long): UserPoint {
        return when (id) {
            0L -> UserPoint(id, 1000L, 1000L)
            1L -> UserPoint(id, 2000L, 2000L)
            2L -> UserPoint(id, 2000L, 2000L)
            else -> throw RuntimeException("invalid input")
        }
    }

    override fun insertOrUpdate(id: Long, amount: Long): UserPoint {
        return when(id) {
            in 0L..2L -> UserPoint(id, amount, 1000L)
            else -> throw RuntimeException("invalid input")
        }
    }
}

class PointHistoryRepositoryStub : PointHistoryRepository {
    override fun insert(id: Long, amount: Long, transactionType: TransactionType, updateMillis: Long): PointHistory {
        if(amount != TEST_PARAM_AMOUNT) throw RuntimeException("invalid amount")
        return when(id) {
            in 0L..2L -> PointHistory(id, id, transactionType, amount, updateMillis)
            else -> throw RuntimeException("invalid input")
        }
    }

    override fun selectAllByUserId(userId: Long): List<PointHistory> {
        return when (userId) {
            0L -> emptyList()
            1L -> listOf(
                PointHistory(0L, 1L, TransactionType.CHARGE, 1000L, 1000L),
                PointHistory(2L, 1L, TransactionType.CHARGE, 1000L, 1100L)
            )
            2L -> listOf(PointHistory(1L, 2L, TransactionType.CHARGE, 2000L, 1200L))
            else -> emptyList()
        }
    }
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

class UserRepositoryStub : UserRepository {
    override fun findById(id: Long): User? {
        TODO("Not yet implemented")
    }

    override fun insert(name: String): User {
        TODO("Not yet implemented")
    }
}