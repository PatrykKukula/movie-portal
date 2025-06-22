package pl.patrykkukula.MovieReviewPortal.Mapper;

import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.UserEntityDto;
import pl.patrykkukula.MovieReviewPortal.Model.UserEntity;

public class UserEntityMapper {

    public static UserEntityDto mapUserEntityToUserEntityDto(UserEntity userEntity) {
        UserEntityDto userEntityDto = new UserEntityDto();
        userEntityDto.setUsername(userEntity.getUsername());
        userEntityDto.setEmail(userEntity.getEmail());
        return userEntityDto;
    }
}
