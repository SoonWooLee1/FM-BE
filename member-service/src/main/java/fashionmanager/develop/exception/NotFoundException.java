package fashionmanager.develop.exception;

// 회원번호 없음 예외
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}