package com.darpasyan.docker.service.impl.group.message;

import com.darpasyan.docker.model.group.Group;
import com.darpasyan.docker.model.group.message.GroupMessage;
import com.darpasyan.docker.model.User.User;
import com.darpasyan.docker.model.User.UserPrincipial;
import com.darpasyan.docker.repo.group.message.GroupMessageRepo;
import com.darpasyan.docker.repo.group.GroupRepo;
import com.darpasyan.docker.repo.UserRepo;
import com.darpasyan.docker.service.group.message.GroupMessageService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class GroupMessageServiceImpl implements GroupMessageService {

    private final GroupMessageRepo groupMessageRepo;

    private final UserRepo userRepo;

    private  final GroupRepo groupRepo;


    record AccessData(
            User user,
            Group group
    ){}

    private AccessData getAccess(int groupId){
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        UserPrincipial currentUser =
                (UserPrincipial)  authentication.getPrincipal();

        User user = userRepo.findById(currentUser.getId()).orElseThrow(
                () -> new RuntimeException("User not found")
        );


        Group group = groupRepo.findById(groupId).orElseThrow(
                () -> new RuntimeException("Group not found")
        );

        if(!group.getParticipants().contains(user)){
            throw new RuntimeException("Access denied. You are not in the group");
        }


        return new AccessData(user, group);
    }


    @Override
    public List<GroupMessage> getGroupMessagesByGroup(int groupId) {

        return groupMessageRepo.findGroupMessagesByGroup(getAccess(groupId).group());
    }

    @Override
    public GroupMessage createGroupMessage(int groupId, GroupMessage message) {

        AccessData data = getAccess(groupId);



        message.setEdited(false);
        message.setUser(data.user());
        message.setDateOfSend(LocalDate.now());

        return groupMessageRepo.save(message);
    }

    @Override
    public GroupMessage editGroupMessage(int id, GroupMessage message) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        UserPrincipial currentUser = (UserPrincipial) authentication.getPrincipal();

        GroupMessage groupMessageForEdit = groupMessageRepo.findById(id).orElseThrow(() ->
                new RuntimeException("Message not found"));

        if(currentUser.getId() != groupMessageForEdit.getUser().getId()){
            throw new RuntimeException("Access denied");
        }

        groupMessageForEdit.setContent(message.getContent());
        groupMessageForEdit.setEdited(true);

        return groupMessageRepo.save(groupMessageForEdit);

    }

    @Override
    public void deleteGroupMessage(int id) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        UserPrincipial currentUser = (UserPrincipial) authentication.getPrincipal();

        GroupMessage messageForDelete = groupMessageRepo.findById(id).orElseThrow(() ->
                new RuntimeException("Message not found"));

        if(currentUser.getId() != messageForDelete.getUser().getId()){
            throw new RuntimeException("Access denied");
        }

        groupMessageRepo.deleteById(id);
    }

    @Override
    public List<GroupMessage> getGroupMessagesByUser(int groupId, String username) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        UserPrincipial currentUser =
                (UserPrincipial)  authentication.getPrincipal();

        User user = userRepo.findById(currentUser.getId()).orElseThrow(
                () -> new RuntimeException("User not found")
        );


        Group group = groupRepo.findById(groupId).orElseThrow(
                () -> new RuntimeException("Group not found")
        );

        if(!group.getParticipants().contains(user)){
            throw new RuntimeException("Access denied. You are not in the group");
        }

        User sender = userRepo.findByUsername(username);

        return groupMessageRepo.findMessagesByUser(sender);
    }
}