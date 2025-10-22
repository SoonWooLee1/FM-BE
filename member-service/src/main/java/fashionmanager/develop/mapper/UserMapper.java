package fashionmanager.develop.mapper;


import fashionmanager.develop.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    UserDTO selectUserByNum(int userNum);

    List<UserDTO> selectAllUsers();

}