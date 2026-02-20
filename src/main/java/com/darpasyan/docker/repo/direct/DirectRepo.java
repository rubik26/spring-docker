package com.darpasyan.docker.repo.direct;

import com.darpasyan.docker.model.direct.Direct;
import com.darpasyan.docker.model.user.User;
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
        (d.sender.id = :id)
     OR (d.recipient.id = :id)
    """)
    List<Direct> myDirects(@Param("id") int id);

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

    @Query("""
    SELECT d FROM Direct d
    WHERE (d.sender = :u1 AND d.recipient = :u2)
       OR (d.sender = :u2 AND d.recipient = :u1)
    """)
    Direct findBetweenUsers(User u1, User u2);

}
