package com.darpasyan.docker.service.impl.group.message;

import com.darpasyan.docker.model.group.Group;
import com.darpasyan.docker.model.group.message.GroupMessage;
import com.darpasyan.docker.model.group.message.dto.GroupMessageRequestDto;
import com.darpasyan.docker.model.group.message.dto.GroupMessageResponseDto;
import com.darpasyan.docker.model.user.User;
import com.darpasyan.docker.model.user.UserPrincipial;
import com.darpasyan.docker.repo.UserRepo;
import com.darpasyan.docker.repo.group.GroupRepo;
import com.darpasyan.docker.repo.group.message.GroupMessageRepo;
import com.darpasyan.docker.service.group.message.GroupMessageService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class GroupMessageServiceImpl implements GroupMessageService {

    private final GroupMessageRepo groupMessageRepo;

    private final UserRepo userRepo;

    private  final GroupRepo groupRepo;



    private GroupMessageResponseDto toDto(GroupMessage groupMessage){
        return new GroupMessageResponseDto(
                groupMessage.getId(),
                groupMessage.getContent(),
                groupMessage.getDateOfSend(),
                groupMessage.isEdited(),
                groupMessage.getUser().getId(),
                groupMessage.getGroup().getId()
        );
    }


    record AccessData(
            User user,
            Group group
    ){}

    private AccessData getAccessToGroup(int groupId){
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


    private GroupMessage getAccessToEditAndDeleteMessage(int id){
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        UserPrincipial currentUser = (UserPrincipial) authentication.getPrincipal();

        GroupMessage groupMessage = groupMessageRepo.findById(id).orElseThrow(() ->
                new RuntimeException("Message not found"));

        if(currentUser.getId() != groupMessage.getUser().getId()){
            throw new RuntimeException("Access denied");
        }

        return groupMessage;
    }


    @Override
    public List<GroupMessageResponseDto> getGroupMessagesByGroup(int groupId) {

        return groupMessageRepo.findGroupMessagesByGroup(getAccessToGroup(groupId).group()).stream().
                map(this::toDto).
                toList();
    }

    @Override
    public GroupMessageResponseDto createGroupMessage(int groupId, GroupMessageRequestDto fromDto) {

        AccessData data = getAccessToGroup(groupId);

        GroupMessage message = new GroupMessage();

        message.setContent(fromDto.getContent());
        message.setEdited(false);
        message.setUser(data.user());
        message.setDateOfSend(LocalDateTime.now());
        message.setGroup(data.group);

         groupMessageRepo.save(message);

         return toDto(message);
    }

    @Override
    public GroupMessageResponseDto editGroupMessage(int id, GroupMessageRequestDto fromDto) {
        GroupMessage groupMessage = getAccessToEditAndDeleteMessage(id);

        groupMessage.setContent(fromDto.getContent());
        groupMessage.setEdited(true);

        groupMessageRepo.save(groupMessage);

        return toDto(groupMessage);

    }

    @Override
    public void deleteGroupMessage(int id) {
        getAccessToEditAndDeleteMessage(id);

        groupMessageRepo.deleteById(id);
    }

    @Override
    public List<GroupMessageResponseDto> getGroupMessagesByUser(int groupId, String username) {

        getAccessToGroup(groupId);

        User sender = userRepo.findByUsername(username);

        return groupMessageRepo.findMessagesByUser(sender).stream().
                map(this::toDto).
                toList();
    }

    @Override
    public GroupMessageResponseDto getGroupMessageById(int groupId, int messageId) {
        getAccessToGroup(groupId);

       GroupMessage message = groupMessageRepo.findById(messageId).orElseThrow(
               () -> new RuntimeException("Message not found")
       );

        return toDto(message);
    }
}