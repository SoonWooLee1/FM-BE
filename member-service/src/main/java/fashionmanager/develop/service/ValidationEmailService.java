package fashionmanager.develop.service;

import fashionmanager.develop.dto.MemberDTO;
import fashionmanager.develop.mapper.MemberMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidationEmailService {

    private final JavaMailSender javaMailSender;
    private final MemberMapper memberMapper;

    public static int createNumber() {
        return (int)(Math.random() * (90000)) +100000;
    }
    public int sendMail(String mail) {

        MemberDTO member = memberMapper.selectMemberByEmail(mail);

        if(member == null) {
            return 0;
        }

        MimeMessage message = javaMailSender.createMimeMessage();
        String senderEmail= "indy03222100@gmail.com";
        int number = createNumber();

        try{
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO,mail);
            message.setSubject("Fashion-Manager 인증번호");
            String body = "";
            body += "<h3>" + "인증번호 입니다." + "</h3>";
            body += "<h1>" + number + "</h1>";
            message.setText(body,"UTF-8","html");

            if(body.equals("") || number == 0){
                return 0;
            }

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        return number;
    }

    public int sendMailPassword(String mail, String id) {
        MemberDTO member = memberMapper.selectMemberByEmailAndId(mail, id);

        if(member == null) {
            return 0;
        }

        MimeMessage message = javaMailSender.createMimeMessage();
        String senderEmail= "indy03222100@gmail.com";
        int number = createNumber();

        try{
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO,mail);
            message.setSubject("Fashion-Manager 인증번호");
            String body = "";
            body += "<h3>" + "인증번호 입니다." + "</h3>";
            body += "<h1>" + number + "</h1>";
            message.setText(body,"UTF-8","html");

            if(body.equals("") || number == 0){
                return 0;
            }

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        return number;
    }
}
