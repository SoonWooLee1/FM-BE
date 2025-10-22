package fashionmanager.develop.struct;

import fashionmanager.develop.dto.BadgeDTO;
import fashionmanager.develop.dto.UserDTO;
import fashionmanager.develop.entity.Badge;
import fashionmanager.develop.entity.User;
import org.mapstruct.Mapper;


// 단순 Entity ↔ DTO 변환용(Mybatis랑 매핑 X)
@Mapper(componentModel = "spring")
public interface UserStructMapper {

    UserDTO toDto(User user);
    BadgeDTO toDto(Badge badge);

}