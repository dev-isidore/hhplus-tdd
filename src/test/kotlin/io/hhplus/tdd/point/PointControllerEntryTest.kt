package io.hhplus.tdd.point

import io.hhplus.tdd.database.PointHistoryRepository
import io.hhplus.tdd.database.UserPointRepository
import io.hhplus.tdd.database.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

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
        val user = userRepository.insert("testPointChargeInvalidAmountUser")

        mockMvc.perform(
            patch("/point/${user.id}/charge")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("\"test\"")
        ).andExpect(
            status().isBadRequest
        )
    }

    @Test
            /**
             * amount는 양수 입력만 받아야 한다.
             */
    fun `포인트 충전시 amount가 음수인 경우`() {
        val user = userRepository.insert("testPointChargeInvalidAmountUser")

        mockMvc.perform(
            patch("/point/${user.id}/charge")
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
        mockMvc.perform(
            patch("/point/${NOT_EXISTING_USER_ID}/charge")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("1000")
        ).andExpect(
            status().isNotFound
        ).andExpectAll(
            jsonPath("$.code").value("404"),
            jsonPath("$.message").value("$NOT_EXISTING_USER_ID does not exist.")
        )
    }

    @Test
            /**
             * 포인트 충전시 기존 포인트와 합한 결과를 반환해야 한다.
             * 포인트 충전 결과를 올바른 응답 객체에 담아 응답해야 한다.
             */
    fun `포인트 충전시 기존 포인트가 있는 경우`() {
        val user = userRepository.insert("testPointChargeExistingUser")
        pointHistoryRepository.insert(user.id, 1000L, TransactionType.CHARGE, 1000L)
        userPointRepository.insertOrUpdate(user.id, 1000L)

        mockMvc.perform(
            patch("/point/${user.id}/charge")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("1000")
        ).andExpect(
            status().isOk
        ).andExpectAll(
            jsonPath("$.id").value(user.id),
            jsonPath("$.point").value(1000L + 1000L),
        )
    }

    @Test
            /**
             * 포인트 충전시 신규 회원의 경우 새로이 충전해 결과를 반환해야 한다.
             * 포인트 충전 결과를 올바른 응답 객체에 담아 응답해야 한다.
             */
    fun `포인트 충전시 기존 포인트가 없는 경우`() {
        val user = userRepository.insert("testPointChargeNewUser")

        mockMvc.perform(
            patch("/point/${user.id}/charge")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("1000")
        ).andExpect(
            status().isOk
        ).andExpectAll(
            jsonPath("$.id").value(user.id),
            jsonPath("$.point").value(1000L),
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
        val user = userRepository.insert("testPointUseInvalidAmountUser")

        mockMvc.perform(
            patch("/point/${user.id}/use")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("\"test\"")
        ).andExpect(
            status().isBadRequest
        )
    }

    @Test
            /**
             * amount는 양수 입력만 받아야 한다.
             */
    fun `포인트 사용시 amount가 음수인 경우`() {
        val user = userRepository.insert("testPointUseInvalidAmountUser")

        mockMvc.perform(
            patch("/point/${user.id}/use")
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
    fun `포인트 사용시 존재하지 않는 아이디의 경우`() {
        mockMvc.perform(
            patch("/point/${NOT_EXISTING_USER_ID}/use")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("1000")
        ).andExpect(
            status().isNotFound
        ).andExpectAll(
            jsonPath("$.code").value("404"),
            jsonPath("$.message").value("$NOT_EXISTING_USER_ID does not exist.")
        )
    }

    @Test
            /**
             * 포인트 사용시 기존 포인트와 합한 결과를 반환해야 한다.
             * 포인트 사용 결과를 올바른 응답 객체에 담아 응답해야 한다.
             */
    fun `포인트 사용시 기존 포인트가 있는 경우`() {
        val user = userRepository.insert("testPointUseExistingUser")
        pointHistoryRepository.insert(user.id, 1000L, TransactionType.CHARGE, 1000L)
        userPointRepository.insertOrUpdate(user.id, 2000L)

        mockMvc.perform(
            patch("/point/${user.id}/use")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("1000")
        ).andExpect(
            status().isOk
        ).andExpectAll(
            jsonPath("$.id").value(user.id),
            jsonPath("$.point").value(2000L - 1000L),
        )
    }

    @Test
            /**
             * 포인트 사용시 신규 회원의 경우 새로이 충전해 결과를 반환해야 한다.
             * 포인트 사용 실패 결과를 예외와 메시지로 전달해야 한다.
             */
    fun `포인트 사용시 기존 포인트가 없는 경우`() {
        val amount = 1000L
        val user = userRepository.insert("testPointUseNewUser")

        mockMvc.perform(
            patch("/point/${user.id}/use")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(amount.toString())
        ).andExpect(
            status().isBadRequest
        ).andExpectAll(
            jsonPath("$.code").value("400"),
            jsonPath("$.message").value("amount:$amount is bigger than current point")
        )
    }

    @Test
            /**
             * 포인트 사용시 신규 회원의 경우 새로이 충전해 결과를 반환해야 한다.
             * 포인트 사용 실패 결과를 예외와 메시지로 전달해야 한다.
             */
    fun `포인트 사용시 기존 포인트가 있으나 사용하도록 요청한 값이 더 큰 경우`() {
        val amount = 3000L
        val baseAmount = 1000L
        val user = userRepository.insert("testPointUseExistingButNotEnoughAmountUser")
        pointHistoryRepository.insert(user.id, baseAmount, TransactionType.CHARGE, 1000L)
        userPointRepository.insertOrUpdate(user.id, baseAmount)

        mockMvc.perform(
            patch("/point/${user.id}/use")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(amount.toString())
        ).andExpect(
            status().isBadRequest
        ).andExpectAll(
            jsonPath("$.code").value("400"),
            jsonPath("$.message").value("amount:$amount is bigger than current point")
        )
    }
    //endregion
}