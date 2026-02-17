package com.darpasyan.docker.repo.group.message;

import com.darpasyan.docker.model.group.Group;
import com.darpasyan.docker.model.group.message.GroupMessage;
import com.darpasyan.docker.model.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupMessageRepo extends JpaRepository<GroupMessage, Integer> {
    List<GroupMessage> findGroupMessagesByGroup(Group group);
    List<GroupMessage> findMessagesByUser(User user);
}
