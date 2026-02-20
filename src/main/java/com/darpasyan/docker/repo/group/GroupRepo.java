package com.darpasyan.docker.repo.group;

import com.darpasyan.docker.model.group.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepo extends JpaRepository<Group, Integer> {

    @Query("""
       select distinct g from Group g
       left join fetch g.participants
       left join fetch g.moderators
       where g.name = :name
       """)
    List<Group> findGroupByName(@Param("name") String name);


    @Query("""
       select distinct g from Group g
       left join fetch g.participants
       left join fetch g.moderators
       """)
    List<Group> findGroupsWithUsers();
}
