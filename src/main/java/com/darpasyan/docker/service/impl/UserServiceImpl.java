package com.darpasyan.docker.service.impl;

import com.darpasyan.docker.config.SecurityConfig;
import com.darpasyan.docker.model.user.User;
import com.darpasyan.docker.model.user.UserPrincipial;
import com.darpasyan.docker.model.user.dto.UserRequestDto;
import com.darpasyan.docker.model.user.dto.UserResponseDto;
import com.darpasyan.docker.model.group.Group;
import com.darpasyan.docker.repo.UserRepo;
import com.darpasyan.docker.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo repo;

    private final SecurityConfig securityConfig;


    private UserResponseDto toResponseDto(User user){

        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getGroups().stream().map(Group::getId).collect(Collectors.toSet()),
                user.getBlacklist().stream().map(User::getId).collect(Collectors.toSet()),
                user.getBlockedBy().stream().map(User::getId).collect(Collectors.toSet())
        );
    }


    private UserRequestDto toRequestDto(User user){
        return new UserRequestDto(
                user.getUsername(),
                user.getPassword()
        );
    }




    private User accessTest(int id){
        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        UserPrincipial authenticatedUser = (UserPrincipial) auth.getPrincipal();

        User user = repo.findById(id).
                orElseThrow(() -> new RuntimeException("User not found"));
        if(user.getId() != authenticatedUser.getId()){
            throw new RuntimeException("Access denied");
        }

        return user;
    }
    @Override
    public List<UserResponseDto> getUsers() {

        List<User> users = repo.findAllWithRelations();
        return users.stream().map(this::toResponseDto).toList();


    }

    @Override
    public UserRequestDto createUser(User user) {

        if(user.getPassword().length() < 8){
            throw new RuntimeException("Password should be more than 8");
        }

        user.setPassword(securityConfig.passwordEncoder().encode(user.getPassword()));
        repo.save(user);

        return toRequestDto(user);
    }

    @Override
    public UserRequestDto updateUser(int id, User user) {
        User getUserForUpdate = accessTest(id);
        getUserForUpdate.setUsername(user.getUsername());
        repo.save(getUserForUpdate);

        return toRequestDto(getUserForUpdate);
    }

    @Override
    public void deleteUser(int id) {
        accessTest(id);

        repo.deleteById(id);
    }

    @Override
    public UserResponseDto getMe() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        UserPrincipial authenticatedUser = (UserPrincipial) authentication.getPrincipal();

        User user = repo.findByIdWithRelations(authenticatedUser.getId());

        return toResponseDto(user);
    }
}
