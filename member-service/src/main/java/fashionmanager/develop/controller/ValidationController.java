package fashionmanager.develop.controller;

import fashionmanager.develop.service.ValidationEmailService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/validation")
@Slf4j
public class ValidationController {

    private final ValidationEmailService emailService;

    public ValidationController(ValidationEmailService validationEmailService) {
        this.emailService = validationEmailService;
    }

    @PostMapping("/sendmail")
    public ResponseEntity<Integer> sendMail(String mail) {
        int result = emailService.sendMail(mail);
        if(result == 0){
            return ResponseEntity.ok(0);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/sendmailpassword")
    public ResponseEntity<Integer> sendMailPassword(String mail, String id) {
        int result = emailService.sendMailPassword(mail, id);
        if(result == 0){
            return ResponseEntity.ok(0);
        }
        return ResponseEntity.ok(result);
    }
}
