package com.darpasyan.docker.repo.direct.message.image;

import com.darpasyan.docker.model.direct.message.image.DirectMessageImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DirectMessageImageRepo extends JpaRepository<DirectMessageImage, Integer> {
    List<DirectMessageImage> findDirectMessageImagesByMessage_Direct_Id(int directId);
}
