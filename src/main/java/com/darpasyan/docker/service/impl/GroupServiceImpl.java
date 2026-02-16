package com.darpasyan.docker.service.impl;

import com.darpasyan.docker.model.Group;
import com.darpasyan.docker.model.User.User;
import com.darpasyan.docker.model.User.UserPrincipial;
import com.darpasyan.docker.repo.GroupRepo;
import com.darpasyan.docker.repo.UserRepo;
import com.darpasyan.docker.service.GroupService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final GroupRepo groupRepo;

    private final UserRepo userRepo;


    @Override
    public List<Group> getGroups() {
        return groupRepo.findAll();
    }

    @Override
    public Group createGroup(Group group) {
        Authentication authentication =
                SecurityContextHolder.
                        getContext().
                        getAuthentication();

        UserPrincipial currentUser =
                (UserPrincipial) authentication.getPrincipal();

        User user = userRepo.findById(currentUser.getId()).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        group.setAdmin(user);
        group.setDateOfCreate(LocalDate.now());
        group.setParticipants(Set.of(user));

        return groupRepo.save(group);
    }

    @Override
    public Group updateGroup(int id, Group group) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();


        UserPrincipial currentUser = (UserPrincipial)
                authentication.getPrincipal();

        User user = userRepo.findById(currentUser.getId()).orElseThrow(
                () -> new RuntimeException("User not found")
        );


       Group groupForUpdate =  groupRepo.findById(id).orElseThrow(
               () -> new RuntimeException("Group not found")
       );

        if(user.getId() != groupForUpdate.getAdmin().getId() || !groupForUpdate.getModerators().contains(user)){
            throw new  RuntimeException("Access denied");
        }



        groupForUpdate.setName(group.getName());
        groupForUpdate.setDescription(group.getDescription());
        groupForUpdate.setParticipants(group.getParticipants());
        groupForUpdate.setAvatar(group.getAvatar());
        groupForUpdate.setAvatarFileName(group.getAvatarFileName());
        groupForUpdate.setAvatarFileType(group.getAvatarFileType());

        if(user.getId() == groupForUpdate.getAdmin().getId()){
            groupForUpdate.setModerators(group.getModerators());

        } else if (group.getModerators().equals(groupForUpdate.getModerators())) {

            groupForUpdate.setModerators(group.getModerators());
        } else{
            throw new RuntimeException("Access denied. You are not an admin");
        }

        return groupRepo.save(groupForUpdate);
    }

    @Override
    public void deleteGroup(int id) {

        Group groupForDelete = groupRepo.findById(id).orElseThrow(
                () -> new RuntimeException("Access denied")
        );

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();


        UserPrincipial currentUser = (UserPrincipial) authentication.getPrincipal();

        User user = userRepo.findById(currentUser.getId()).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        if(user.getId() != groupForDelete.getAdmin().getId()){
            throw new RuntimeException("Access denied");
        }


        groupRepo.delete(groupForDelete);

    }

    @Override
    public List<Group> findGroupByName(String name) {
        return groupRepo.findGroupByName(name);
    }
}
