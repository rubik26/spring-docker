package com.darpasyan.docker.service;

import com.darpasyan.docker.model.Message;

import java.util.List;

public interface MessageService {

    List<Message> getMessages();
    Message createMessage(Message message);
    Message editMessage(int id, Message message);
    void deleteMessage(int id);

    List<Message> getMessagesByUser(String username);
}
