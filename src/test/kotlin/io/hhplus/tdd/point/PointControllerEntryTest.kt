package io.hhplus.tdd.point

import io.hhplus.tdd.database.UserPointRepository
import io.hhplus.tdd.database.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class PointControllerEntryTest() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userPointRepository: UserPointRepository

    //region GET /point/{id}
    @Test
    fun `when requested invalid id format then returns 400`() {
        mockMvc.perform(get("/point/id"))
            .andExpect(
                status().isBadRequest
            )
    }

    @Test
    fun `when requested id does not exist on table then returns 404`() {
        mockMvc.perform(get("/point/-1"))
            .andExpect(
                status().isNotFound
            ).andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `returns id's current point`() {
        val user = userRepository.insert("testName")
        val userPoint = userPointRepository.insertOrUpdate(user.id, 1000L)

        mockMvc.perform(get("/point/0"))
            .andExpect(status().isOk)
            .andExpectAll(
                jsonPath("$.id").value(user.id),
                jsonPath("$.point").value(userPoint.point),
                jsonPath("$.updateMillis").value(userPoint.updateMillis)
            )
    }
    //endregion
}