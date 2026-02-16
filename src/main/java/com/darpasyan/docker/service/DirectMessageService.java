package com.darpasyan.docker.service;

import com.darpasyan.docker.model.Messages.DirectMessage;


import java.util.List;

public interface DirectMessageService {
    List<DirectMessage> getMessagesByDirect(int directId);
    List<DirectMessage> getDirectMessagesByUsername(int directId, String username);
    DirectMessage createDirectMessage(int directId, DirectMessage directMessage);
    DirectMessage editDirectMessage(int id, DirectMessage directMessage);
    void deleteDirectMessage(int id);
}
