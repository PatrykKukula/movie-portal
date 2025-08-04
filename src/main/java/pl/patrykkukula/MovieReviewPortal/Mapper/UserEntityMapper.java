package pl.patrykkukula.MovieReviewPortal.Mapper;

import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.UserEntityDto;
import pl.patrykkukula.MovieReviewPortal.Model.UserEntity;

import java.time.LocalDateTime;

public class UserEntityMapper {

    public static UserEntity mapToUserEntity(UserEntityDto userDto){
        return UserEntity.builder()
                .username(userDto.getUsername().toLowerCase())
                .email(userDto.getEmail().toLowerCase())
                .userSex(userDto.getUserSex())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .dateOfBirth(userDto.getDateOfBirth())
                .registeredAt(LocalDateTime.now())
                .isEnabled(false)
                .build();
    }
}
