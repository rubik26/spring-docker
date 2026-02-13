package com.darpasyan.docker.repo;

import com.darpasyan.docker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepo extends JpaRepository<User, Integer> {

    User findByUsername(String username);
    void deleteById(int id);

}
