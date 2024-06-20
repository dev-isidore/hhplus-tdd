package io.hhplus.tdd.point

import io.hhplus.tdd.point.exception.NegativeAmountException
import io.hhplus.tdd.point.service.PointService
import io.hhplus.tdd.point.service.model.PointHistory
import io.hhplus.tdd.point.service.model.TransactionType
import io.hhplus.tdd.point.service.model.UserPoint
import io.hhplus.tdd.user.exception.UserNotFoundException
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest
@AutoConfigureMockMvc
class PointControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc
    @MockBean
    private lateinit var pointService: PointService

    companion object {
        val NOT_EXISTING_USER_ID = -1L
    }

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
        `when`(pointService.getCurrentUserPoint(eq(NOT_EXISTING_USER_ID))).thenThrow(UserNotFoundException("test message"))

        mockMvc.perform(get("/point/$NOT_EXISTING_USER_ID"))
            .andExpect(
                status().isNotFound
            ).andExpectAll(
                jsonPath("$.code").value("404"),
                jsonPath("$.message").value("test message")
            )
    }

    @Test
            /**
             * 현재 포인트 조회 결과를 올바른 응답 객체에 담아 응답해야 한다.
             */
    fun `현재 포인트 조회시 정상 경로`() {
        val userId = 0L
        val userPointAmount = 1000L
        val updateMillis = System.currentTimeMillis()

        `when`(pointService.getCurrentUserPoint(eq(userId))).thenReturn(
            UserPoint(
                userId,
                userPointAmount,
                updateMillis
            )
        )

        mockMvc.perform(get("/point/${userId}"))
            .andExpect(status().isOk)
            .andExpectAll(
                jsonPath("$.id").value(userId),
                jsonPath("$.point").value(userPointAmount),
                jsonPath("$.updateMillis").value(updateMillis)
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
        `when`(pointService.getUserPointHistories(eq(NOT_EXISTING_USER_ID))).thenThrow(UserNotFoundException("test message"))

        mockMvc.perform(get("/point/-1/histories"))
            .andExpect(
                status().isNotFound
            ).andExpectAll(
                jsonPath("$.code").value("404"),
                jsonPath("$.message").value("test message")
            )
    }

    @Test
            /**
             * 포인트 응답 결과를 올바른 응답 객체에 담아 응답해야 한다.
             * 빈 응답의 경우 빈 리스트를 반환한다.
             */
    fun `포인트 이력 조회시 빈 응답의 경우`() {
        val userId = 0L

        `when`(pointService.getUserPointHistories(eq(userId))).thenReturn(emptyList())

        mockMvc.perform(get("/point/$userId/histories"))
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
        val userId = 0L
        var iota = 0L
        val pointHistories = listOf(
            PointHistory(
                id = iota++,
                userId = userId,
                amount = 1000L,
                type = TransactionType.CHARGE,
                timeMillis = 1000L
            ),
            PointHistory(
                id = iota++,
                userId = userId,
                amount = 1100L,
                type = TransactionType.CHARGE,
                timeMillis = 1100L
            ),
            PointHistory(
                id = iota,
                userId = userId,
                amount = 1500L,
                type = TransactionType.USE,
                timeMillis = 1200L
            )
        )
        `when`(pointService.getUserPointHistories(eq(userId))).thenReturn(pointHistories)

        mockMvc.perform(get("/point/$userId/histories"))
            .andExpect(
                status().isOk
            ).andExpectAll(
                jsonPath("$[0].id").value(0),
                jsonPath("$[0].userId").value(userId),
                jsonPath("$[0].type").value(TransactionType.CHARGE.name),
                jsonPath("$[0].amount").value(1000L),
                jsonPath("$[0].timeMillis").value(1000L),
            ).andExpectAll(
                jsonPath("$[1].id").value(1),
                jsonPath("$[1].userId").value(userId),
                jsonPath("$[1].type").value(TransactionType.CHARGE.name),
                jsonPath("$[1].amount").value(1100L),
                jsonPath("$[1].timeMillis").value(1100L),
            ).andExpectAll(
                jsonPath("$[2].id").value(2),
                jsonPath("$[2].userId").value(userId),
                jsonPath("$[2].type").value(TransactionType.USE.name),
                jsonPath("$[2].amount").value(1500L),
                jsonPath("$[2].timeMillis").value(1200L),
            )
    }
    //endregion

    //region PATCH /point/{id}/charge
    @Test
            /**
             * Long 형식의 입력이 아닌 경우 400 응답을 해야 한다.
             */
    fun `포인트 충전시 Long 형식 아이디가 아닌 경우`() {
        mockMvc.perform(
            patch("/point/id/charge")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("1000")
        ).andExpect(
            status().isBadRequest
        )
    }

    @Test
            /**
             * Long 형식의 입력이 아닌 경우 400 응답을 해야 한다.
             */
    fun `포인트 충전시 Long 형식 amount가 아닌 경우`() {
        val userId = 0L

        mockMvc.perform(
            patch("/point/${userId}/charge")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("\"textInput\"")
        ).andExpect(
            status().isBadRequest
        )
    }

    @Test
            /**
             * amount는 양수 입력만 받아야 한다.
             */
    fun `포인트 충전시 amount가 음수인 경우`() {
        val userId = 0L

        mockMvc.perform(
            patch("/point/${userId}/charge")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("-1000")
        ).andExpect(
            status().isBadRequest
        )
    }

    @Test
            /**
             * 존재하지 않는 아이디의 경우 예외를 받아 404 응답을 해야 한다.
             */
    fun `포인트 충전시 존재하지 않는 아이디의 경우`() {
        `when`(pointService.chargeUserPoint(eq(NOT_EXISTING_USER_ID), eq(1000L))).thenThrow(UserNotFoundException("test message"))

        mockMvc.perform(
            patch("/point/${NOT_EXISTING_USER_ID}/charge")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("1000")
        ).andExpect(
            status().isNotFound
        ).andExpectAll(
            jsonPath("$.code").value("404"),
            jsonPath("$.message").value("test message")
        )
    }

    @Test
            /**
             * 포인트 충전 결과를 올바른 응답 객체에 담아 응답해야 한다.
             */
    fun `포인트 충전시 정상 경로`() {
        val userId = 0L
        val userPointAmount = 1000L
        val updateMillis = System.currentTimeMillis()
        `when`(pointService.chargeUserPoint(eq(userId), eq(1000L))).thenReturn(
            UserPoint(
                userId,
                userPointAmount,
                updateMillis
            )
        )

        mockMvc.perform(
            patch("/point/$userId/charge")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("1000")
        ).andExpect(
            status().isOk
        ).andExpectAll(
            jsonPath("$.id").value(userId),
            jsonPath("$.point").value(userPointAmount),
            jsonPath("$.updateMillis").value(updateMillis)
        )
    }
    //endregion

    //region PATCH /point/{id}/use
    @Test
            /**
             * Long 형식의 입력이 아닌 경우 400 응답을 해야 한다.
             */
    fun `포인트 사용시 Long 형식 아이디가 아닌 경우`() {
        mockMvc.perform(
            patch("/point/id/use")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("1000")
        ).andExpect(
            status().isBadRequest
        )
    }

    @Test
            /**
             * Long 형식의 입력이 아닌 경우 400 응답을 해야 한다.
             */
    fun `포인트 사용시 Long 형식 amount가 아닌 경우`() {
        val userId = 0L

        mockMvc.perform(
            patch("/point/${userId}/use")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("\"textInput\"")
        ).andExpect(
            status().isBadRequest
        )
    }

    @Test
            /**
             * amount는 양수 입력만 받아야 한다.
             */
    fun `포인트 사용시 amount가 음수인 경우`() {
        val userId = 0L
        val amount = -1000L

        mockMvc.perform(
            patch("/point/${userId}/use")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(amount.toString())
        ).andExpect(
            status().isBadRequest
        ).andExpectAll(
            jsonPath("$.code").value("400"),
            jsonPath("$.message").value("amount:$amount cannot be negative")
        )
    }

    @Test
            /**
             * 존재하지 않는 아이디의 경우 예외를 받아 404 응답을 해야 한다.
             */
    fun `포인트 사용시 존재하지 않는 아이디의 경우`() {
        `when`(pointService.useUserPoint(eq(NOT_EXISTING_USER_ID), eq(1000L))).thenThrow(UserNotFoundException("test message"))

        mockMvc.perform(
            patch("/point/${NOT_EXISTING_USER_ID}/use")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("1000")
        ).andExpect(
            status().isNotFound
        ).andExpectAll(
            jsonPath("$.code").value("404"),
            jsonPath("$.message").value("test message")
        )
    }

    @Test
            /**
             * amount가 사용가능한 포인트보다 큰 경우 예외를 던지고 이를 400 응답에 담아야 한다.
             */
    fun `포인트 사용시 amount가 포인트보다 큰 경우`() {
        val userId = 0L
        `when`(pointService.useUserPoint(eq(userId), eq(1000L))).thenThrow(
            NegativeAmountException("test message")
        )

        mockMvc.perform(
            patch("/point/${userId}/use")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("1000")
        ).andExpect(
            status().isBadRequest
        ).andExpectAll(
            jsonPath("$.code").value("400"),
            jsonPath("$.message").value("test message")
        )
    }

    @Test
            /**
             * 포인트 충전 결과를 올바른 응답 객체에 담아 응답해야 한다.
             */
    fun `포인트 사용시 정상 경로`() {
        val userId = 0L
        val userPointAmount = 1000L
        val updateMillis = System.currentTimeMillis()
        `when`(pointService.useUserPoint(eq(userId), eq(1000L))).thenReturn(
            UserPoint(
                userId,
                userPointAmount,
                updateMillis
            )
        )

        mockMvc.perform(
            patch("/point/$userId/use")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("1000")
        ).andExpect(
            status().isOk
        ).andExpectAll(
            jsonPath("$.id").value(userId),
            jsonPath("$.point").value(userPointAmount),
            jsonPath("$.updateMillis").value(updateMillis)
        )
    }
    //endregion
}