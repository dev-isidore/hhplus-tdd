package io.hhplus.tdd.point

import io.hhplus.tdd.database.UserPointRepository
import io.hhplus.tdd.database.UserRepository
import io.hhplus.tdd.user.User
import io.hhplus.tdd.user.UserService
import io.hhplus.tdd.user.exception.UserNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PointServiceTest {
    private val pointService = PointService(userService = UserServiceStub(), userPointRepository = UserPointRepositoryStub())

    //region getCurrentUserPoint
    @Test
    fun `when user does not exist then throw exception`() {
        val notExistingUserId = 2L

        val exception = assertThrows<UserNotFoundException> {
            pointService.getCurrentUserPoint(notExistingUserId)
        }
        assertThat(exception).message().contains("$notExistingUserId does not exist.")
    }

    @Test
    fun `get current user point`() {
        val userId = 0L

        val currentUserPoint = pointService.getCurrentUserPoint(userId)

        assertThat(currentUserPoint.id).isEqualTo(userId)
        assertThat(currentUserPoint.point).isEqualTo(1000L)
        assertThat(currentUserPoint.updateMillis).isEqualTo(1000L)
    }
    //endregion
}

class UserServiceStub : UserService(UserRepositoryStub()) {
    override fun checkUserExists(id: Long): Boolean {
        return when(id) {
            0L -> true
            1L -> true
            else -> false
        }
    }
}

class UserPointRepositoryStub : UserPointRepository {
    override fun selectById(id: Long): UserPoint {
        return when (id) {
            0L -> UserPoint(0L, 1000L, 1000L)
            1L -> UserPoint(1L, 2000L, 2000L)
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