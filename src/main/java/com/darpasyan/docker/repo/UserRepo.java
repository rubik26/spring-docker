package com.darpasyan.docker.repo;

import com.darpasyan.docker.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRepo extends JpaRepository<User, Integer> {

    User findByUsername(String username);
    void deleteById(int id);

}
