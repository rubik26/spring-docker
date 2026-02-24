package com.darpasyan.docker.service.impl.group;

import com.darpasyan.docker.model.group.Group;
import com.darpasyan.docker.model.group.dto.GroupRequestDto;
import com.darpasyan.docker.model.group.dto.GroupResponseDto;
import com.darpasyan.docker.model.user.User;
import com.darpasyan.docker.model.user.UserPrincipial;
import com.darpasyan.docker.repo.UserRepo;
import com.darpasyan.docker.repo.group.GroupRepo;
import com.darpasyan.docker.service.group.GroupService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final GroupRepo groupRepo;

    private final UserRepo userRepo;



    private GroupResponseDto toDto(Group group){
        return new GroupResponseDto(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getAvatar(),
                group.getDateOfCreate(),
                group.getAdmin().getId(),
                group.getModerators().
                        stream().
                        map(User::getId).
                        collect(Collectors.toSet()),
                group.getParticipants().
                        stream().
                        map(User::getId).
                        collect(Collectors.toSet())
        );
    }


    @Override
    public List<GroupResponseDto> getGroups() {
        List<Group> groups = groupRepo.findAll();

        return groups.stream().map(this::toDto).toList();


    }

    @Override
    public GroupResponseDto createGroup(GroupRequestDto fromDto) {
        Authentication authentication =
                SecurityContextHolder.
                        getContext().
                        getAuthentication();

        UserPrincipial currentUser =
                (UserPrincipial) authentication.getPrincipal();

        User user = userRepo.findById(currentUser.getId()).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        Group group = new Group();


        group.setName(fromDto.getName());
        group.setDescription(fromDto.getDescription());
        group.setAvatar(fromDto.getAvatar());
        group.setAvatarFileName(fromDto.getAvatarFileName());
        group.setAvatarFileType(fromDto.getAvatarFileType());
        group.setModerators(fromDto.getModeratorsId().stream().map(participantsId -> userRepo.findById(participantsId).orElseThrow(
                () -> new RuntimeException("User not found")
        )).collect(Collectors.toSet()));
        group.setParticipants(Set.of(user));
        group.setAdmin(user);
        group.setDateOfCreate(LocalDate.now());

        groupRepo.save(group);

        return toDto(group);
    }

    @Override
    public GroupResponseDto updateGroup(int id, GroupRequestDto fromDto) {
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

        if(user.getId() != groupForUpdate.getAdmin().getId() && !groupForUpdate.getModerators().contains(user)){
            throw new  RuntimeException("Access denied");
        }



        groupForUpdate.setName(fromDto.getName());
        groupForUpdate.setDescription(fromDto.getDescription());
        groupForUpdate.setParticipants(fromDto.getParticipantsId().
                stream().
                map(participantsId -> userRepo.findById(participantsId).orElseThrow(
                        () -> new RuntimeException("User not found")
                )).
                collect(Collectors.toSet()));


        groupForUpdate.setAvatar(fromDto.getAvatar());
        groupForUpdate.setAvatarFileName(fromDto.getAvatarFileName());
        groupForUpdate.setAvatarFileType(fromDto.getAvatarFileType());

        if(user.getId() == groupForUpdate.getAdmin().getId()){
            groupForUpdate.setModerators(fromDto.getModeratorsId().
                    stream().
                    map(
                            moderatorsId -> userRepo.findById(moderatorsId).orElseThrow(
                                    () -> new RuntimeException("User not found")
                            )
                    ).collect(Collectors.
                            toSet())
            );

        } else if (fromDto.getModeratorsId().stream().map(moderatorsId -> userRepo.findById(moderatorsId).orElseThrow(
                () -> new RuntimeException("User not found")
        )).collect(Collectors.toSet()).equals(groupForUpdate.getModerators())) {

            groupForUpdate.setModerators(fromDto.getModeratorsId().stream().map(moderatorsId -> userRepo.findById(moderatorsId).orElseThrow(
                    () -> new RuntimeException("User not found")
            )).collect(Collectors.toSet()));
        } else{
            throw new RuntimeException("Access denied. You are not an admin");
        }

        groupRepo.save(groupForUpdate);

        return toDto(groupForUpdate);
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
    public List<GroupResponseDto> findGroupsByName(String name) {
        return groupRepo.findGroupsByName(name).
                stream().
                map(this::toDto).
                toList();
    }

    @Override
    public GroupResponseDto getGroupById(int id) {
        Group group = groupRepo.findById(id).orElseThrow(
                () -> new RuntimeException("Group not found")
        );

        return toDto(group);
    }
}
