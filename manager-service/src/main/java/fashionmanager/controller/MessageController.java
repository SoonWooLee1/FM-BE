package fashionmanager.controller;

import fashionmanager.dto.InsertMessageDTO;
import fashionmanager.dto.MemberDTO;
import fashionmanager.dto.MessageDTO;
import fashionmanager.dto.SelectMassageDTO;
import fashionmanager.service.MemberService;
import fashionmanager.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/message")
public class MessageController {

    private final MessageService ms;
    private final MemberService memberService;

    public MessageController(MessageService messageService, MemberService memberService) {
        this.ms = messageService;
        this.memberService = memberService;
    }

    @GetMapping("/selectallmessages")
    public ResponseEntity<List<MessageDTO>> selectAllMessages() {
        List<MessageDTO> messageList = ms.selectAllMessages();
        for (MessageDTO messageDTO : messageList) {
            log.info("messageDTO: {}", messageDTO);
        }

        return ResponseEntity.ok(messageList);
    }

    @PostMapping("/selectsendermessage")
    public ResponseEntity<List<SelectMassageDTO>> selectSenderMessage(String senderId) {
        List<SelectMassageDTO> messageList = ms.selectSenderMessage(senderId);
        for (SelectMassageDTO messageDTO : messageList) {
            log.info("messageDTO: {}", messageDTO);
        }

        return ResponseEntity.ok(messageList);
    }

    @PostMapping("/selectreceivermessage")
    public ResponseEntity<List<SelectMassageDTO>> selectReceiverMessage(String receiverId) {
        List<SelectMassageDTO> messageList = ms.selectReceiverMessage(receiverId);
        for (SelectMassageDTO messageDTO : messageList) {
            log.info("messageDTO: {}", messageDTO);
        }

        return ResponseEntity.ok(messageList);
    }

    @PostMapping("/insertmessage")
    public ResponseEntity<String> insertMessage(String messageReceiver, String messageSender, String messageTitle, int messageCategory, String messageContent){
        MemberDTO senderMember = memberService.selectMemberById(messageSender);
        MemberDTO receiverMember = memberService.selectMemberById(messageReceiver);

        InsertMessageDTO insertMessageDTO = new InsertMessageDTO();
        insertMessageDTO.setTitle(messageTitle);
        insertMessageDTO.setContent(messageContent);
        insertMessageDTO.setDate(LocalDateTime.now());
        insertMessageDTO.setExpDate(LocalDateTime.now().plusMonths(1));
        insertMessageDTO.setSenderNum(senderMember.getMemberNum());
        insertMessageDTO.setReceiverNum(receiverMember.getMemberNum());
        insertMessageDTO.setSenderName(messageSender);
        insertMessageDTO.setReceiverName(messageReceiver);
        insertMessageDTO.setCategoryNum(messageCategory);

        System.out.println("SenderName: " + senderMember.getMemberName());
        System.out.println("ReceiverName: " + receiverMember.getMemberName());

        int result = ms.insertMessage(insertMessageDTO);

        if(result == 1){
            log.info("메시지가 보내졌습니다.");
            return ResponseEntity.ok("메시지가 보내졌습니다.");
        }else{
            log.info("메시지를 보내는데, 실패했습니다.");
            return ResponseEntity.ok("메시지를 보내는데, 실패했습니다.");
        }
    }

    @PostMapping("/deletemessage")
    public ResponseEntity<String> deleteMessage(int messageNum){
        int result = ms.deleteMessage(messageNum);
        if(result == 1){
            log.info("메시지를 삭제하였습니다.");
            return ResponseEntity.ok("메시지를 삭제하였습니다.");
        }else{
            log.info("메시지 삭제에 실패했습니다.");
            return ResponseEntity.ok("메시지 삭제에 실패했습니다.");
        }
    }

    @PostMapping("/updatemessageconfirm")
    public ResponseEntity<String> updateMessageConfirm(int messageNum){
        int result = ms.updatemessageconfirm(messageNum);
        if(result == 1){
            return ResponseEntity.ok("쪽지를 확인했습니다.");
        }else{
            return ResponseEntity.ok("쪽지를 확인하지 않았습니다.");
        }
    }
}
