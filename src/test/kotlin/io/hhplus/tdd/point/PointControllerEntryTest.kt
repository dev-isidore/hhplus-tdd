package io.hhplus.tdd.point

import io.hhplus.tdd.database.PointHistoryRepository
import io.hhplus.tdd.database.UserPointRepository
import io.hhplus.tdd.database.UserRepository
import io.hhplus.tdd.user.exception.UserNotFoundException
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
class PointControllerEntryTest() {
    companion object {
        val NOT_EXISTING_USER_ID = -1
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userPointRepository: UserPointRepository

    @Autowired
    private lateinit var pointHistoryRepository: PointHistoryRepository

    //region GET /point/{id}
    @Test
            /**
             * Long 형식의 입력이 아닌 경우 400 응답을 해야 한다.
             */
    fun `현재 포인트 조회시 Long 형식 아이디가 아닌 경우`() {
        mockMvc.perform(get("/point/id"))
            .andExpect(
                status().isBadRequest
            )
    }

    @Test
            /**
             * 존재하지 않는 아이디의 경우 예외를 받아 404 응답을 해야 한다.
             */
    fun `현재 포인트 조회시 존재하지 않는 아이디의 경우`() {
        mockMvc.perform(get("/point/$NOT_EXISTING_USER_ID"))
            .andExpect(
                status().isNotFound
            ).andExpectAll(
                jsonPath("$.code").value("404"),
                jsonPath("$.message").value("$NOT_EXISTING_USER_ID does not exist.")
            )
    }

    @Test
            /**
             * 현재 포인트 조회 결과를 올바른 응답 객체에 담아 응답해야 한다.
             */
    fun `현재 포인트 조회시 정상 경로`() {
        val user = userRepository.insert("testCurrentPointUserName")
        val userPoint = userPointRepository.insertOrUpdate(user.id, 1000L)

        mockMvc.perform(get("/point/${user.id}"))
            .andExpect(status().isOk)
            .andExpectAll(
                jsonPath("$.id").value(user.id),
                jsonPath("$.point").value(userPoint.point),
                jsonPath("$.updateMillis").value(userPoint.updateMillis)
            )
    }
    //endregion

    //region GET /point/{id}/histories
    @Test
            /**
             * Long 형식의 입력이 아닌 경우 400 응답을 해야 한다.
             */
    fun `포인트 이력 조회시 Long 형식 아이디가 아닌 경우`() {
        mockMvc.perform(get("/point/id/histories"))
            .andExpect(
                status().isBadRequest
            )
    }

    @Test
            /**
             * 존재하지 않는 아이디의 경우 예외를 받아 404 응답을 해야 한다.
             */
    fun `포인트 이력 조회시 존재하지 않는 아이디의 경우`() {
        mockMvc.perform(get("/point/${NOT_EXISTING_USER_ID}/histories"))
            .andExpect(
                status().isNotFound
            ).andExpectAll(
                jsonPath("$.code").value("404"),
                jsonPath("$.message").value("$NOT_EXISTING_USER_ID does not exist.")
            )
    }

    @Test
            /**
             * 포인트 응답 결과를 올바른 응답 객체에 담아 응답해야 한다.
             * 빈 응답의 경우 빈 리스트를 반환한다.
             */
    fun `포인트 이력 조회시 빈 응답의 경우`() {
        val user = userRepository.insert("testPointEmptyHistoriesUserName")

        mockMvc.perform(get("/point/${user.id}/histories"))
            .andExpect(
                status().isOk
            ).andExpect(
                content().string("[]")
            )
    }

    @Test
            /**
             * 포인트 응답 결과를 올바른 응답 객체에 담아 응답해야 한다.
             */
    fun `포인트 이력 조회시 정상 경로`() {
        val user = userRepository.insert("testPointHistoriesUserName")
        val history0 = pointHistoryRepository.insert(user.id, 1000L, TransactionType.CHARGE, 1000L)
        val history1 = pointHistoryRepository.insert(user.id, 1100L, TransactionType.CHARGE, 1100L)
        val history2 = pointHistoryRepository.insert(user.id, 1500L, TransactionType.USE, 1200L)

        mockMvc.perform(get("/point/${user.id}/histories"))
            .andExpect(
                status().isOk
            ).andExpectAll(
                jsonPath("$[0].id").value(history0.id),
                jsonPath("$[0].userId").value(user.id),
                jsonPath("$[0].type").value(TransactionType.CHARGE.name),
                jsonPath("$[0].amount").value(1000L),
                jsonPath("$[0].timeMillis").value(1000L),
            ).andExpectAll(
                jsonPath("$[1].id").value(history1.id),
                jsonPath("$[1].userId").value(user.id),
                jsonPath("$[1].type").value(TransactionType.CHARGE.name),
                jsonPath("$[1].amount").value(1100L),
                jsonPath("$[1].timeMillis").value(1100L),
            ).andExpectAll(
                jsonPath("$[2].id").value(history2.id),
                jsonPath("$[2].userId").value(user.id),
                jsonPath("$[2].type").value(TransactionType.USE.name),
                jsonPath("$[2].amount").value(1500L),
                jsonPath("$[2].timeMillis").value(1200L),
            )
    }
    //endregion
}