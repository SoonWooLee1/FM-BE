package fashionmanager.develop.exception;


// 입력값에 비어있는 값이나 잘못 된 값을 넣었을시
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}