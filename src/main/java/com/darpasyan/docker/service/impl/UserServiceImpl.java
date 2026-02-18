package com.darpasyan.docker.service.impl;

import com.darpasyan.docker.config.SecurityConfig;
import com.darpasyan.docker.model.User.User;
import com.darpasyan.docker.model.User.UserPrincipial;
import com.darpasyan.docker.repo.UserRepo;
import com.darpasyan.docker.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo repo;

    private final SecurityConfig securityConfig;

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
    public List<User> getUsers() {
        return repo.findAll();
    }

    @Override
    public User createUser(User user) {

        if(user.getPassword().length() < 8){
            throw new RuntimeException("Password should be more than 8");
        }

        user.setPassword(securityConfig.passwordEncoder().encode(user.getPassword()));

        return repo.save(user);
    }

    @Override
    public User updateUser(int id, User user) {
        User getUserForUpdate = accessTest(id);
        getUserForUpdate.setUsername(user.getUsername());
        return repo.save(getUserForUpdate);
    }

    @Override
    public void deleteUser(int id) {
        accessTest(id);

        repo.deleteById(id);
    }

    @Override
    public User getMe() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        UserPrincipial authenticatedUser = (UserPrincipial) authentication.getPrincipal();

        return repo.findById(authenticatedUser.getId()).orElseThrow(()
                -> new RuntimeException("User not found"));
    }
}
