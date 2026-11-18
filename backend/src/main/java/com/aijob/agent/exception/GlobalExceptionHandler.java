
package com.aijob.agent.exception;

import com.aijob.agent.common.ErrorCode;
import com.aijob.agent.common.Result;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(
            BusinessException e) {

        Result<Void> result =
                Result.error(e.getCode(), e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(result);
    }

    /**
     * 处理参数格式错误
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<Void>> handleArgumentException(
            IllegalArgumentException e) {

        Result<Void> result =
                Result.error(ErrorCode.PARAM_ERROR);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(result);
    }

    /**
     * 处理未预期的系统异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(
            Exception e) {

        // 服务端记录详细错误，方便开发排查
        org.slf4j.LoggerFactory
                .getLogger(GlobalExceptionHandler.class)
                .error("系统发生未预期异常", e);

        // 不向前端泄露内部异常细节
        Result<Void> result =
                Result.error(ErrorCode.SYSTEM_ERROR);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(result);
    }
}