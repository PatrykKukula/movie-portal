package pl.patrykkukula.MovieReviewPortal.Mapper;

import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.UserDataDto;
import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.UserUpdateDto;
import pl.patrykkukula.MovieReviewPortal.Model.Role;
import pl.patrykkukula.MovieReviewPortal.Model.UserEntity;
import pl.patrykkukula.MovieReviewPortal.Utils.ServiceUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static pl.patrykkukula.MovieReviewPortal.Utils.ServiceUtils.*;

public class UserMapper {
    private UserMapper(){}

    public static UserDataDto mapToUserDataDto(UserEntity user){
        return UserDataDto.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .status((user.getBanned() != null && user.getBanned())? "Banned" : "Active")
                .banExpirationDate(user.getBanExpiration() != null ? user.getBanExpiration().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                        : "Doesn't apply")
                .roles(user.getRoles().stream().map(Role::getRoleName).toList())
                .build();
    }
    public static UserEntity mapUserUpdateDtoToUserEntity(UserEntity user, UserUpdateDto userUpdateDto){
        updateField(userUpdateDto::getEmail, user::setEmail);
        updateField(userUpdateDto::getDateOfBirth, user::setDateOfBirth);
        updateField(userUpdateDto::getUserSex, user::setUserSex);
        updateField(userUpdateDto::getFirstName, user::setFirstName);
        updateField(userUpdateDto::getLastName, user::setLastName);
        return user;
    }
}
