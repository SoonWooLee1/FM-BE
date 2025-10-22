package fashionmanager.develop.handler;

import fashionmanager.develop.exception.DuplicateUserException;
import fashionmanager.develop.exception.NotFoundException;
import fashionmanager.develop.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ParkGlobalExceptionHandler {


    // 회원입력창에 빈 칸이나 잘못된 값을 입력시 예외처리
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException(ValidationException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    // 중복된 회원 값 입력시
    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<String> handleDuplicateUserException(DuplicateUserException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)   // 409
                .body(e.getMessage());
    }


    // 회원 정보 없음 예외
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)   // 404
                .body(e.getMessage());
    }



    // 그 외 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("서버 오류가 발생했습니다: " + e.getMessage());
    }
}