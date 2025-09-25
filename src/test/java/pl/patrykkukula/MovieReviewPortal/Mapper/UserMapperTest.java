package pl.patrykkukula.MovieReviewPortal.Mapper;

import org.junit.jupiter.api.Test;
import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.UserDataDto;
import pl.patrykkukula.MovieReviewPortal.Model.Role;
import pl.patrykkukula.MovieReviewPortal.Model.UserEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {

    @Test
    public void shouldMapUserEntityToUserDataDtoCorrectly(){
        new Role();
        UserEntity userEntity = UserEntity.builder()
                .username("user")
                .email("user@user.com")
                .banned(true)
                .banExpiration(LocalDateTime.of(LocalDate.of(2000,1, 1), LocalTime.of(12, 0)))
                .roles(List.of(Role.builder().roleName("USER").build()))
                .build();

        UserDataDto mappedUser = UserMapper.mapToUserDataDto(userEntity);

        assertEquals("user", mappedUser.getUsername());
        assertEquals("user@user.com", mappedUser.getEmail());
        assertEquals("Banned", mappedUser.getStatus());
        assertEquals(LocalDateTime.of(LocalDate.of(2000,1, 1), LocalTime.of(12, 0))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), mappedUser.getBanExpirationDate());
        assertEquals("USER", mappedUser.getRoles().getFirst());
    }
}
