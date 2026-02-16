package com.darpasyan.docker.repo;

import com.darpasyan.docker.model.Direct;
import com.darpasyan.docker.model.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DirectRepo extends JpaRepository<Direct, Integer> {

    @Query("""
SELECT d FROM Direct d
WHERE
    (d.sender = :me AND LOWER(d.recipient.username) LIKE LOWER(CONCAT('%', :name, '%')))
 OR (d.recipient = :me AND LOWER(d.sender.username) LIKE LOWER(CONCAT('%', :name, '%')))
""")
    List<Direct> searchDirects(
            @Param("me") User me,
            @Param("name") String name
    );
}
