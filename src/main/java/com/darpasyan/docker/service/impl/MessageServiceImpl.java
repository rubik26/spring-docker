package com.darpasyan.docker.service.impl;

import com.darpasyan.docker.model.Message;
import com.darpasyan.docker.model.User;
import com.darpasyan.docker.model.UserPrincipial;
import com.darpasyan.docker.repo.MessageRepo;
import com.darpasyan.docker.repo.UserRepo;
import com.darpasyan.docker.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepo messageRepo;

    private final UserRepo userRepo;





    @Override
    public List<Message> getMessages() {
        return messageRepo.findAll();
    }

    @Override
    public Message createMessage(Message message) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipial currentUser = (UserPrincipial) authentication.getPrincipal();
        User user = userRepo.findById(currentUser.getId()).orElseThrow(() ->
                new RuntimeException("User not found"));

        message.setEdited(false);
        message.setUser(user);
        message.setDateOfSend(LocalDate.now());
        return messageRepo.save(message);
    }

    @Override
    public Message editMessage(int id, Message message) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipial currentUser = (UserPrincipial) authentication.getPrincipal();

        Message messageForEdit = messageRepo.findById(id).orElseThrow(() ->
                new RuntimeException("Message not found"));

        if(currentUser.getId() != messageForEdit.getUser().getId()){
            throw new RuntimeException("Access denied");
        }

        messageForEdit.setContent(message.getContent());
        messageForEdit.setEdited(true);

        return messageRepo.save(messageForEdit);

    }

    @Override
    public void deleteMessage(int id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipial currentUser = (UserPrincipial) authentication.getPrincipal();

        Message messageForDelete = messageRepo.findById(id).orElseThrow(() ->
                new RuntimeException("Message not found"));

        if(currentUser.getId() != messageForDelete.getUser().getId()){
            throw new RuntimeException("Access denied");
        }

        messageRepo.deleteById(id);
    }

    @Override
    public List<Message> getMessagesByUser(String username) {
        User user = userRepo.findByUsername(username);
        return messageRepo.findMessagesByUser(user);
    }
}
