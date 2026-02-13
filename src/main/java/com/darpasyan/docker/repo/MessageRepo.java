package com.darpasyan.docker.repo;

import com.darpasyan.docker.model.Message;
import com.darpasyan.docker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepo extends JpaRepository<Message, Integer> {
    List<Message> findMessagesByUser(User user);
}
