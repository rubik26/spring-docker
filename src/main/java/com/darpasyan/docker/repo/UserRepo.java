package com.darpasyan.docker.repo;

import com.darpasyan.docker.model.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRepo extends JpaRepository<User, Integer> {

    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.groups " +
            "LEFT JOIN FETCH u.blacklist " +
            "LEFT JOIN FETCH u.blockedBy")
    List<User> findAllWithRelations();


    User findByUsername(String username);
    void deleteById(int id);

    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN FETCH u.groups " +
            "LEFT JOIN FETCH u.blacklist " +
            "LEFT JOIN FETCH u.blockedBy " +
            "WHERE u.id = :id")
    User findByIdWithRelations(int id);


}
