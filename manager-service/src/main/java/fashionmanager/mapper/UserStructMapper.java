package fashionmanager.mapper;

import org.mapstruct.Mapper;
import fashionmanager.dto.BadgeDTO;
import fashionmanager.dto.UserDTO;
import fashionmanager.entity.Badge;
import fashionmanager.entity.User;

@Mapper(componentModel = "spring")
public interface UserStructMapper {

    UserDTO toDto(User user);
    BadgeDTO toDto(Badge badge);

}