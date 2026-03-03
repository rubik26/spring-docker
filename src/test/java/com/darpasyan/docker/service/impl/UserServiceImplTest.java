package com.darpasyan.docker.service.impl;

import com.darpasyan.docker.builders.group.impl.GroupBuilderImpl;
import com.darpasyan.docker.builders.user.impl.UserBuilderImpl;
import com.darpasyan.docker.model.group.Group;
import com.darpasyan.docker.model.user.User;
import com.darpasyan.docker.model.user.UserPrincipial;
import com.darpasyan.docker.model.user.dto.UserRequestDto;
import com.darpasyan.docker.model.user.dto.UserResponseDto;
import com.darpasyan.docker.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepo repo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private  UserServiceImpl service;

    @Captor
    ArgumentCaptor<User> userCaptor;

    private UserBuilderImpl userBuilder;
    private GroupBuilderImpl groupBuilder;

    @BeforeEach
    void init(){
        userBuilder = new UserBuilderImpl();
        groupBuilder = new GroupBuilderImpl();

    }

    private void mockSecurity(User user) {
        UserPrincipial principial = new UserPrincipial(user);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(principial);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }


    @Test
    void testGetUsers(){

        Group group = groupBuilder.build();

        User user1 = userBuilder.
                setUsername("Test User").
                setGroups(Set.of(group)).
                build();
        User user2 = userBuilder.
                setUsername("Test User 2").
                setGroups(Set.of(group)).
                blackList(Set.of(user1)).
                build();

        user1.setBlockedBy(Set.of(user2));


        UserResponseDto toDto1 = new UserResponseDto(1, "Test User",
                new HashSet<>(Set.of(1)),
                new HashSet<>(),
                new HashSet<>(Set.of(2))
        );

        UserResponseDto toDto2 = new UserResponseDto(2, "Test User 2",
                new HashSet<>(Set.of(1)),
                new HashSet<>(Set.of(1)),
                new HashSet<>()

        );

        when(repo.findAll()).thenReturn(List.of(user1, user2));

        List<UserResponseDto> result = service.getUsers();

        assertEquals(List.of(toDto1, toDto2), result);
    }

    @Test
    void testGetUserById() {
        int userId = 1;

        Group group = groupBuilder.build();
        User user = userBuilder.
                setUsername("Test User").
                setGroups(Set.of(group)).
                build();


        UserResponseDto toDto = new UserResponseDto(1, "Test User",
                new HashSet<>(Set.of(1)),
                new HashSet<>(),
                new HashSet<>());


        when(repo.findById(userId)).thenReturn(Optional.of(user));

        UserResponseDto result = service.getUserById(userId);

        assertEquals(toDto, result);
    }

    @Test
    void testCreateUser() {

        UserRequestDto fromDto = new UserRequestDto("kamaro_1", "00000000");

        when(passwordEncoder.encode("00000000"))
                .thenReturn("encodedPassword");

        when(repo.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User u = invocation.getArgument(0);
                    u.setId(1);
                    u.setGroups(new HashSet<>());
                    u.setBlacklist(new HashSet<>());
                    u.setBlockedBy(new HashSet<>());
                    return u;
                });

        UserResponseDto result = service.createUser(fromDto);

        verify(repo).save(userCaptor.capture());

        verify(passwordEncoder).encode("00000000");

        User capturedUser = userCaptor.getValue();
        assertEquals("kamaro_1", capturedUser.getUsername());
        assertEquals("encodedPassword", capturedUser.getPassword());

        assertEquals(1, result.getId());
        assertEquals("kamaro_1", result.getUsername());
    }

    @Test
    void testGetMe(){
        User user = userBuilder.build();

        mockSecurity(user);

        when(repo.findById(user.getId())).thenReturn(Optional.of(user));

        UserResponseDto result = service.getMe();

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getUsername(), result.getUsername());

        verify(repo).findById(user.getId());

        SecurityContextHolder.clearContext();
    }

    @Test
    void testUpdateUser_shouldUpdateWhenUserIsOwner() {
        int userId = 1;

        User user = userBuilder.setPassword("12345678").build();

        mockSecurity(user);

        UserRequestDto fromDto = new UserRequestDto("kamaro_1", "12345678");


        when(repo.findById(userId)).thenReturn(Optional.of(user));

        when(repo.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1);
            u.setGroups(new HashSet<>());
            u.setBlacklist(new HashSet<>());
            u.setBlockedBy(new HashSet<>());
            return u;
        });

        UserResponseDto result = service.updateUser(userId, fromDto);

        verify(repo).findById(userId);
        verify(repo).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertEquals(userId, capturedUser.getId());
        assertNotEquals("Rubik", capturedUser.getUsername());
        assertEquals("12345678", capturedUser.getPassword());

        assertEquals(userId, result.getId());
        assertEquals("kamaro_1", result.getUsername());

        SecurityContextHolder.clearContext();
    }

    @Test
    void testUpdateUserByNonOwner_shouldThrow(){
        int userId = 1;

        User user = userBuilder.build();

        User user2 = userBuilder.build();
        user2.setId(2);
        mockSecurity(user2);

        UserRequestDto fromDto = new UserRequestDto("kamaro_1", "12345678");


        when(repo.findById(userId)).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                service.updateUser(user.getId(), fromDto));


        assertEquals("Access denied", exception.getMessage());

        SecurityContextHolder.clearContext();
    }

    @Test
    void deleteUser_shouldDeleteWhenUserIsOwner() {

        int userId = 1;

        User user = userBuilder.build();
        mockSecurity(user);

        when(repo.findById(userId)).thenReturn(Optional.of(user));

        service.deleteUser(userId);

        verify(repo, times(1)).deleteById(userId);

        SecurityContextHolder.clearContext();
    }

    @Test
    void deleteUserByNonOwner_shouldThrow(){
        int userId = 1;

        User user = userBuilder.build();

        User user2 = userBuilder.build();
        user2.setId(2);

        mockSecurity(user2);

        when(repo.findById(userId)).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                service.deleteUser(userId));

        assertEquals("Access denied", exception.getMessage());

        SecurityContextHolder.clearContext();
    }
}