package fashionmanager.develop.mapper;

import fashionmanager.develop.dto.MessageDTO;
import fashionmanager.develop.dto.SelectMassageDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageMapper {
    List<MessageDTO> selectAllMessages();

    List<SelectMassageDTO> selectSenderMessage(@Param("senderId")String senderId);

    List<SelectMassageDTO> selectReceiverMessage(@Param("receiverId")String receiverId);
}
