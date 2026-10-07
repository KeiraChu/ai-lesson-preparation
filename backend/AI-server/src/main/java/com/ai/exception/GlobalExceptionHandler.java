package com.ai.exception;

import com.ai.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
    public Result<Void> handleBadRequest(Exception exception) {
        return Result.error(exception.getMessage());
    }

    @ExceptionHandler(WebClientResponseException.class)
    public Result<Void> handleAiService(WebClientResponseException exception) {
        log.warn("AI service request failed: status={}", exception.getStatusCode());
        return Result.error("AI 服务暂时不可用，请稍后重试");
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleUnexpected(Exception exception) {
        log.error("Unhandled server error", exception);
        return Result.error("系统处理失败，请稍后重试");
    }
}
