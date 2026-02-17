package com.darpasyan.docker.repo.group.message.image;

import com.darpasyan.docker.model.group.message.image.GroupMessageImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMessageImageRepo extends JpaRepository<GroupMessageImage, Integer> {

}
