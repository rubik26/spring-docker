package com.darpasyan.docker.service.impl;

import com.darpasyan.docker.model.group.Group;
import com.darpasyan.docker.model.user.User;
import com.darpasyan.docker.model.user.dto.UserResponseDto;
import com.darpasyan.docker.repo.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepo repo;

    @InjectMocks
    private  UserServiceImpl service;


    @Test
    void testGetUsers(){
        Group group1 = new Group();
        Group group2 = new Group();

        group1.setId(1);
        group2.setId(2);

        User user1 = new User(1, "Joe", "joe-biden-2020",
                new HashSet<>(Set.of(group1)),
                new HashSet<>(),
                new HashSet<>()
        );

        User user2 = new User(2, "Donald", "MAGA2024",
                new HashSet<>(Set.of(group1, group2)),
                new HashSet<>(),
                new HashSet<>()
        );


        user1.setBlockedBy(new HashSet<>(Set.of(user2)));
        user2.setBlacklist(new HashSet<>(Set.of(user1)));

        UserResponseDto toDto1 = new UserResponseDto(1, "Joe",
                new HashSet<>(Set.of(1)),
                new HashSet<>(),
                new HashSet<>(Set.of(2))
        );

        UserResponseDto toDto2 = new UserResponseDto(2, "Donald",
                new HashSet<>(Set.of(1, 2)),
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

        Group group = new Group();

        group.setId(1);

        User user = new User(1, "Rubik", "12345678",
                new HashSet<>(Set.of(group)),
                new HashSet<>(),
                new HashSet<>()
        );


        UserResponseDto toDto = new UserResponseDto(1, "Rubik",
                new HashSet<>(Set.of(1)),
                new HashSet<>(),
                new HashSet<>());


        when(repo.findById(userId)).thenReturn(Optional.of(user));

        UserResponseDto result = service.getUserById(userId);

        assertEquals(toDto, result);
    }




}