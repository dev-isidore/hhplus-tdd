package io.hhplus.tdd.point

import io.hhplus.tdd.user.exception.UserNotFoundException
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest
@AutoConfigureMockMvc
class PointControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var pointService: PointService

    //region GET /point/{id}
    @Test
            /**
             * Long 형식의 입력이 아닌 경우 400 응답을 해야 한다.
             */
    fun `when requested invalid id format then returns 400`() {
        mockMvc.perform(get("/point/id"))
            .andExpect(
                status().isBadRequest
            )
    }

    @Test
            /**
             * 존재하지 않는 아이디의 경우 예외를 받아 404 응답을 해야 한다.
             */
    fun `when requested id does not exist on table then returns 404`() {
        val notExistId = -1L

        `when`(pointService.getCurrentUserPoint(eq(notExistId))).thenThrow(UserNotFoundException("test message"))

        mockMvc.perform(get("/point/-1"))
            .andExpect(
                status().isNotFound
            ).andDo(MockMvcResultHandlers.print())
    }

    @Test
            /**
             * happy path
             */
    fun `returns id's current point`() {
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

        mockMvc.perform(get("/point/0"))
            .andExpect(status().isOk)
            .andExpectAll(
                jsonPath("$.id").value(userId),
                jsonPath("$.point").value(userPointAmount),
                jsonPath("$.updateMillis").value(updateMillis)
            )
    }
    //endregion
}