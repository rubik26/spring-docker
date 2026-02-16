package com.darpasyan.docker.service.impl;

import com.darpasyan.docker.model.Direct;
import com.darpasyan.docker.model.Messages.DirectMessage;
import com.darpasyan.docker.model.User.User;
import com.darpasyan.docker.model.User.UserPrincipial;
import com.darpasyan.docker.repo.DirectMessageRepo;
import com.darpasyan.docker.repo.DirectRepo;
import com.darpasyan.docker.repo.UserRepo;
import com.darpasyan.docker.service.DirectMessageService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import java.util.List;

@Service
@AllArgsConstructor
public class DirectMessageServiceImpl implements DirectMessageService {

    private final DirectMessageRepo directMessageRepo;

    private final UserRepo userRepo;

    private final DirectRepo directRepo;


    private Direct getAccessTest(int directId){
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        UserPrincipial currentUser =
                (UserPrincipial) authentication.getPrincipal();

        User user = userRepo.findById(currentUser.getId()).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        Direct direct = directRepo.findById(directId).orElseThrow(
                () -> new RuntimeException("Direct not found")
        );

        if(direct.getSender().getId() != user.getId() && direct.getRecipient().getId() != user.getId()){
            throw new RuntimeException("Access denied");
        }

        return direct;
    }



    private record AccessData(
            User user,
            DirectMessage message
    ){}


    private AccessData putAndDeleteAccess(int id){
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        UserPrincipial currentUser =
                (UserPrincipial) authentication.getPrincipal();

        User user = userRepo.findById(currentUser.getId()).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        DirectMessage directMessage = directMessageRepo.findById(id).orElseThrow(
                () -> new RuntimeException("Message not found")
        );



        if(user.getId() != directMessage.getUser().getId()){
            throw new RuntimeException("Access denied");
        }


        return new AccessData(user, directMessage);
    }

    @Override
    public List<DirectMessage> getMessagesByDirect(int directId) {
        return directMessageRepo.getDirectMessagesByDirect(getAccessTest(directId));
    }

    @Override
    public List<DirectMessage> getDirectMessagesByUsername(int directId, String username) {

        getAccessTest(directId);


        User user = userRepo.findByUsername(username);

        return directMessageRepo.getDirectMessagesByUser(user);
    }

    @Override
    public DirectMessage createDirectMessage(int directId, DirectMessage directMessage) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        UserPrincipial currentUser =
                (UserPrincipial) authentication.getPrincipal();

        User user = userRepo.findById(currentUser.getId()).orElseThrow(
                () -> new RuntimeException("User not found")
        );


        Direct direct = directRepo.findById(directId).orElseThrow(
                () -> new RuntimeException("Direct not found")
        );


        directMessage.setDateOfSend(LocalDate.now());
        directMessage.setEdited(false);
        directMessage.setWatched(false);

        directMessage.setUser(user);
        directMessage.setDirect(direct);

        return directMessageRepo.save(directMessage);
    }

    @Override
    public DirectMessage editDirectMessage(int id, DirectMessage directMessage) {
        directMessage = putAndDeleteAccess(id).message();

        directMessage.setContent(directMessage.getContent());
        directMessage.setEdited(true);


        return directMessageRepo.save(directMessage);

    }




    @Override
    public void deleteDirectMessage(int id) {
        AccessData data = putAndDeleteAccess(id);

        User user = data.user();
        DirectMessage directMessage = data.message();


        if(user.getId() != directMessage.getUser().getId()){
            throw new RuntimeException("Access denied");
        }

        directMessageRepo.deleteById(id);
    }
}
