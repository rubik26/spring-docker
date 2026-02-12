package com.darpasyan.docker.service.impl;

import com.darpasyan.docker.Config.SecurityConfig;
import com.darpasyan.docker.model.User;
import com.darpasyan.docker.model.UserPrincipial;
import com.darpasyan.docker.repo.UserRepo;
import com.darpasyan.docker.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepo repo;

    private final SecurityConfig securityConfig;




    UserServiceImpl(UserRepo repo, SecurityConfig securityConfig){
        this.repo = repo;
        this.securityConfig = securityConfig;
    }



    @Override
    public List<User> getUsers() {
        return repo.findAll();
    }

    @Override
    public User createUser(User user) {
        user.setPassword(securityConfig.passwordEncoder().encode(user.getPassword()));
        return repo.save(user);
    }

    @Override
    public User updateUser(int id, User user) {
        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        UserPrincipial authenticatedUser = (UserPrincipial) auth.getPrincipal();
        User getUserForUpdate = repo.findById(id).
                orElseThrow(() -> new RuntimeException("User not found"));
        if(getUserForUpdate.getId() != authenticatedUser.getId()){
            throw new RuntimeException("Access denied");
        }
        getUserForUpdate.setUsername(user.getUsername());
        return repo.save(getUserForUpdate);
    }

    @Override
    public void deleteUser(int id) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        UserPrincipial authenticatedUser = (UserPrincipial) authentication.getPrincipal();
        User getUserForDelete = repo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        if(getUserForDelete.getId() != authenticatedUser.getId()){
            throw new RuntimeException("Access denied");
        }
        repo.deleteById(id);
    }
}
