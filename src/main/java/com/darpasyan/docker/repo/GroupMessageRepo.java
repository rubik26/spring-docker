package com.darpasyan.docker.repo;

import com.darpasyan.docker.model.Group;
import com.darpasyan.docker.model.Messages.GroupMessage;
import com.darpasyan.docker.model.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupMessageRepo extends JpaRepository<GroupMessage, Integer> {
    List<GroupMessage> findGroupMessagesByGroup(Group group);
    List<GroupMessage> findMessagesByUser(User user);
}
