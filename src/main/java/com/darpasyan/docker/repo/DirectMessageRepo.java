package com.darpasyan.docker.repo;


import com.darpasyan.docker.model.Direct;
import com.darpasyan.docker.model.Messages.DirectMessage;
import com.darpasyan.docker.model.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DirectMessageRepo extends JpaRepository<DirectMessage, Integer> {
    List<DirectMessage> getDirectMessagesByDirect(Direct direct);
    List<DirectMessage> getDirectMessagesByUser(User user);
}
