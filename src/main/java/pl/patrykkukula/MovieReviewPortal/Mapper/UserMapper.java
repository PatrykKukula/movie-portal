package pl.patrykkukula.MovieReviewPortal.Mapper;

import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.UserDataDto;
import pl.patrykkukula.MovieReviewPortal.Model.Role;
import pl.patrykkukula.MovieReviewPortal.Model.UserEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserMapper {
    private UserMapper(){}

    public static UserDataDto mapToUserDataDto(UserEntity user){
        return UserDataDto.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .status((user.getBanned() != null && user.getBanned())? "Banned" : "Active")
                .banExpirationDate(user.getBanExpiration() != null ? user.getBanExpiration().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                        : "Doesn't apply")
                .roles(user.getRoles().stream().map(Role::getRoleName).toList())
                .build();
    }
}
