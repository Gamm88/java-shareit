package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.model.*;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@Transactional
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserServiceImplTest {
    private final EntityManager em;
    private final UserServiceImpl userService;

    @Test
    void testAddUser() {
        UserDto userDto = makeUserDto("user", "user@user.com");
        userService.addUser(userDto);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User targetUser = query
                .setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(targetUser.getId(), notNullValue());
        assertThat(targetUser.getName(), equalTo(userDto.getName()));
        assertThat(targetUser.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void testGetUsers() {
        List<UserDto> userDtoList = List.of(
                makeUserDto("user1", "user1@user.com"),
                makeUserDto("user2", "user2@user.com"),
                makeUserDto("user3", "user3@user.com")
        );

        for (UserDto userDto : userDtoList) {
            User entity = UserMapper.mapToUser(userDto);
            em.persist(entity);
        }
        em.flush();

        Collection<UserDto> targetUsers = userService.getUsers();

        assertThat(targetUsers, hasSize(userDtoList.size()));
        for (UserDto userDto : userDtoList) {
            assertThat(targetUsers, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(userDto.getName())),
                    hasProperty("email", equalTo(userDto.getEmail()))
            )));
        }
    }

    @Test
    void testGetUser() {
        UserDto userDto = makeUserDto("user", "user@user.com");
        User user = UserMapper.mapToUser(userDto);
        em.persist(user);
        em.flush();
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User targetUser = query
                .setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(targetUser.getId(), equalTo(1L));
        assertThat(targetUser.getEmail(), equalTo(userDto.getEmail()));
        assertThat(targetUser.getName(), equalTo(userDto.getName()));
    }

    @Test
    void testUpdateUser() {
        UserDto userDto = makeUserDto("user", "user@user.com");
        User user = UserMapper.mapToUser(userDto);
        em.persist(user);
        em.flush();

        UserDto userDtoUpdate = makeUserDto("update", "update@user.com");
        userService.updateUser(1L, userDtoUpdate);
        UserDto targetUser = userService.getUser(1L);

        assertThat(targetUser.getName(), equalTo("update"));
    }

    @Test
    void testDeleteUser() {
        UserDto userDto = makeUserDto("user", "user@user.com");
        userService.addUser(userDto);
        Collection<UserDto> userDtos = userService.getUsers();
        assertThat(userDtos, hasSize(1));
        userService.deleteUser(1L);
        userDtos = userService.getUsers();
        assertThat(userDtos, empty());
    }

    private UserDto makeUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }
}