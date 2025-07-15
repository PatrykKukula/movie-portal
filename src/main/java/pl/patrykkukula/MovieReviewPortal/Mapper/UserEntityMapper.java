package pl.patrykkukula.MovieReviewPortal.Mapper;

import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.UserEntityDto;
import pl.patrykkukula.MovieReviewPortal.Model.UserEntity;

public class UserEntityMapper {

    public static UserEntity mapToUserEntity(UserEntityDto userDto){
        return UserEntity.builder()
                .username(userDto.getUsername().toLowerCase())
                .email(userDto.getEmail().toLowerCase())
                .userSex(userDto.getUserSex())
                .dateOfBirth(userDto.getDateOfBirth())
                .firstName(userDto.getFirstName().substring(0, 1).toUpperCase() + userDto.getFirstName().substring(1).toLowerCase())
                .lastName(userDto.getLastName().substring(0, 1).toUpperCase() + userDto.getLastName().substring(1).toLowerCase())
                .build();
    }
}
