package fashionmanager.config;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 런타임 예외 발생
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntime(RuntimeException e) {
        log.error("런타임 예외 발생: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("런타임 오류: " + e.getMessage());
    }

    // 잘못된 인자 예외
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        log.error("잘못된 인자 예외 발생: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("잘못된 요청: " + e.getMessage());
    }

    // 존재하지 않는 리소스 예외
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFound(EntityNotFoundException e) {
        log.error("리소스 없음: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("리소스를 찾을 수 없습니다.");
    }

    // 나머지 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAny(Exception e) {
        log.error("서버 오류 발생: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("서버 오류가 발생했습니다.");
    }
}