package com.darpasyan.docker.repo.group;

import com.darpasyan.docker.model.group.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepo extends JpaRepository<Group, Integer> {

    List<Group> findGroupByName(String name);



}
