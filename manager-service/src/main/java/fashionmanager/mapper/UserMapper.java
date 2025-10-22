package fashionmanager.mapper;


import org.apache.ibatis.annotations.Mapper;
import fashionmanager.dto.UserDTO;

import java.util.List;

@Mapper
public interface UserMapper {
    UserDTO selectUserByNum(int userNum);

    List<UserDTO> selectAllUsers();

}