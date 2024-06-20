package io.hhplus.tdd.point.controller

import io.hhplus.tdd.ErrorResponse
import io.hhplus.tdd.point.service.PointService
import io.hhplus.tdd.point.dto.response.PointHistoryResponse
import io.hhplus.tdd.point.dto.response.UserPointResponse
import io.hhplus.tdd.point.exception.NegativeAmountException
import io.hhplus.tdd.user.exception.UserNotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/point")
class PointController(@Autowired private val pointService: PointService) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    fun point(
        @PathVariable id: Long,
    ): UserPointResponse {
        val currentUserPoint = pointService.getCurrentUserPoint(id)
        logger.info("PointController.point id:$id currentUserPoint:$currentUserPoint")
        return UserPointResponse.of(currentUserPoint)
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    fun history(
        @PathVariable id: Long,
    ): List<PointHistoryResponse> {
        val userPointHistories = pointService.getUserPointHistories(id)
        logger.info("PointController.point id:$id userPointHistories:${userPointHistories.size}")
        return userPointHistories.map { PointHistoryResponse.of(it) }
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    fun charge(
        @PathVariable id: Long,
        @RequestBody amount: Long,
    ): UserPointResponse {
        if(amount < 0) {
            logger.warn("Negative amount: $amount id: $id")
            throw NegativeAmountException("amount:$amount cannot be negative")
        }
        val chargeUserPoint = pointService.chargeUserPoint(id, amount)
        return UserPointResponse.of(chargeUserPoint)
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    fun use(
        @PathVariable id: Long,
        @RequestBody amount: Long,
    ): UserPointResponse {
        if(amount < 0) {
            logger.warn("Negative amount: $amount id: $id")
            throw NegativeAmountException("amount:$amount cannot be negative")
        }
        val usedUserPoint = pointService.useUserPoint(id, amount)
        return UserPointResponse.of(usedUserPoint)
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(e: UserNotFoundException): ResponseEntity<ErrorResponse> {
        logger.warn("User not found: ${e.message}")
        return ResponseEntity(
            ErrorResponse("404", e.message?:"이용자 정보를 찾을 수 없습니다."),
            HttpStatus.NOT_FOUND
        )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleNegativeAmountException(e: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        logger.warn("Invalid input: ${e.message}")
        return ResponseEntity(
            ErrorResponse("400", e.message?:"비정상적인 입력입니다."),
            HttpStatus.BAD_REQUEST
        )
    }
}