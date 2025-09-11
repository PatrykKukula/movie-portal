package pl.patrykkukula.MovieReviewPortal.Mapper;

import org.junit.jupiter.api.Test;
import pl.patrykkukula.MovieReviewPortal.Constants.UserSex;
import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.UserEntityDto;
import pl.patrykkukula.MovieReviewPortal.Model.UserEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserEntityMapperTest {

    @Test
    public void shouldMapUserEntityDtoToUserEntityCorrectly(){
        UserEntityDto userDto = UserEntityDto.builder()
                .username("user")
                .email("user@user.com")
                .dateOfBirth(LocalDate.of(2000, 1,1))
                .userSex(UserSex.HE)
                .firstName("name")
                .lastName("lastname")
                .build();

        UserEntity mappedUser = UserEntityMapper.mapToUserEntity(userDto);

        assertEquals("user", mappedUser.getUsername());
        assertEquals("user@user.com", mappedUser.getEmail());
        assertEquals(LocalDate.of(2000,1,1), mappedUser.getDateOfBirth());
        assertEquals(UserSex.HE, mappedUser.getUserSex());
        assertEquals("name", mappedUser.getFirstName());
        assertEquals("lastname", mappedUser.getLastName());
    }
}
