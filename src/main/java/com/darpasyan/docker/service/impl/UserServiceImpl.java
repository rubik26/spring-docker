package com.darpasyan.docker.service.impl;

import com.darpasyan.docker.model.group.Group;
import com.darpasyan.docker.model.user.User;
import com.darpasyan.docker.model.user.UserPrincipial;
import com.darpasyan.docker.model.user.dto.UserRequestDto;
import com.darpasyan.docker.model.user.dto.UserResponseDto;
import com.darpasyan.docker.repo.UserRepo;
import com.darpasyan.docker.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo repo;

    private final PasswordEncoder passwordEncoder;


    private UserResponseDto toDto(User user){

        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getGroups().stream().map(Group::getId).collect(Collectors.toSet()),
                user.getBlacklist().stream().map(User::getId).collect(Collectors.toSet()),
                user.getBlockedBy().stream().map(User::getId).collect(Collectors.toSet())
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
        List<User> users = repo.findAll();
        return users.stream().map(this::toDto).toList();
    }

    @Override
    public UserResponseDto createUser(UserRequestDto fromDto) {

        if(fromDto.getPassword().length() < 8){
            throw new RuntimeException("Password should be more than 8");
        }

        User user = new User();
        user.setUsername(fromDto.getUsername());
        user.setPassword(passwordEncoder.encode(fromDto.getPassword()));
        User savedUser = repo.save(user);

        return toDto(savedUser);
    }

    @Override
    public UserResponseDto updateUser(int id, UserRequestDto fromDto) {
        User getUserForUpdate = accessTest(id);
        getUserForUpdate.setUsername(fromDto.getUsername());
        repo.save(getUserForUpdate);

        return toDto(getUserForUpdate);
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

        User user = repo.findById(authenticatedUser.getId()).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        return toDto(user);
    }

    @Override
    public UserResponseDto getUserById(int id) {
        User user = repo.findById(id).orElseThrow(
               () -> new RuntimeException("User not found")
        );

       return toDto(user);
    }
}
