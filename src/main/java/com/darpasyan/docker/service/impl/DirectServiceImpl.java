package com.darpasyan.docker.service.impl;

import com.darpasyan.docker.model.Direct;
import com.darpasyan.docker.model.User.User;
import com.darpasyan.docker.model.User.UserPrincipial;
import com.darpasyan.docker.repo.DirectRepo;
import com.darpasyan.docker.repo.UserRepo;
import com.darpasyan.docker.service.DirectService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DirectServiceImpl implements DirectService {

    private final DirectRepo directRepo;

    private final UserRepo userRepo;
    @Override
    public List<Direct> getDirectByUsername(String username) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();


        UserPrincipial currentUser =
                (UserPrincipial) authentication.getPrincipal();


        User me = userRepo.findById(currentUser.getId()).orElseThrow(
                () -> new RuntimeException("User not found")
        );



       return directRepo.searchDirects(me, username);
    }

    @Override
    public Direct createDirect(Direct direct) {
        return directRepo.save(direct);
    }
}
