package com.darpasyan.docker.repo.group.message.image;

import com.darpasyan.docker.model.group.message.image.GroupMessageImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupMessageImageRepo extends JpaRepository<GroupMessageImage, Integer> {
    List<GroupMessageImage> findGroupMessageImagesByMessage_Group_Id(int groupId);

}
