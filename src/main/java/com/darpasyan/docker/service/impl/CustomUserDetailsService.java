package com.darpasyan.docker.service.impl;

import com.darpasyan.docker.model.User;
import com.darpasyan.docker.model.UserPrincipial;
import com.darpasyan.docker.repo.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepo repo;

    CustomUserDetailsService(UserRepo repo){
        this.repo = repo;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repo.findByUsername(username);

        if(user == null){
            System.out.println("User not found");
            throw new UsernameNotFoundException("Something went wrong");
        }

        return new UserPrincipial(user);
    }
}
