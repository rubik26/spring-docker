package com.darpasyan.docker.service.impl.direct;

import com.darpasyan.docker.model.direct.Direct;
import com.darpasyan.docker.model.User.User;
import com.darpasyan.docker.model.User.UserPrincipial;
import com.darpasyan.docker.repo.direct.DirectRepo;
import com.darpasyan.docker.repo.UserRepo;
import com.darpasyan.docker.service.direct.DirectService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class DirectServiceImpl implements DirectService {

    private final DirectRepo directRepo;

    private final UserRepo userRepo;

    @Override
    public List<Direct> getDirectsByCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        UserPrincipial currentUser =
                (UserPrincipial) authentication.getPrincipal();

        return directRepo.myDirects(currentUser.getId());
    }

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
    public Direct createDirect(int receiverId, Direct direct) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();


        UserPrincipial currentUser =
                (UserPrincipial) authentication.getPrincipal();


        User me = userRepo.findById(currentUser.getId()).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        User receiver = userRepo.findById(receiverId).orElseThrow(
                () -> new RuntimeException("User not found")
        );


        Direct existing = directRepo.findBetweenUsers(me, receiver);

        if(existing != null){
            return existing;
        }

        Direct direct1 = new Direct();

        direct1.setSender(me);
        direct1.setRecipient(receiver);
        direct1.setDateOfStart(LocalDate.now());

        return directRepo.save(direct1);
    }
}
