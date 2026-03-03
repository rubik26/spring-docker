package com.darpasyan.docker.service.impl.group;

import com.darpasyan.docker.builders.group.impl.GroupBuilderImpl;
import com.darpasyan.docker.builders.user.impl.UserBuilderImpl;
import com.darpasyan.docker.model.group.Group;
import com.darpasyan.docker.model.group.dto.GroupRequestDto;
import com.darpasyan.docker.model.group.dto.GroupResponseDto;
import com.darpasyan.docker.model.user.User;
import com.darpasyan.docker.model.user.UserPrincipial;
import com.darpasyan.docker.repo.UserRepo;
import com.darpasyan.docker.repo.group.GroupRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceImplTest {

    @Mock
    private GroupRepo groupRepo;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private GroupServiceImpl groupService;

    @Captor
    ArgumentCaptor<Group> groupCaptor;

    UserBuilderImpl userBuilder;
    GroupBuilderImpl groupBuilder;

    @BeforeEach
    void init(){
        userBuilder = new UserBuilderImpl();
        groupBuilder = new GroupBuilderImpl();
    }

    private void mockSecurity(User user){
        UserPrincipial principial = new UserPrincipial(user);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn(principial);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }


    @Test
    void testGetGroups() {

        byte[] avatar = new byte[1];

        User admin1 = userBuilder.build();
        User admin2 = userBuilder.build();

        Group group1 = groupBuilder.
                setName("Default Group").
                setDescription("Default description").
                setAvatar(avatar).
                setDateOfCreate(LocalDate.now()).
                setAdmin(admin1).
                build();
        Group group2 = groupBuilder.
                setName("Default Group 1").
                setDescription("Default description 1").
                setAvatar(avatar).
                setDateOfCreate(LocalDate.now()).
                setAdmin(admin2).
                build();


        GroupResponseDto toDto = new GroupResponseDto(
                1,
                "Default Group",
                "Default description",
                avatar,
                LocalDate.now(),
                1,
                new HashSet<>(),
                new HashSet<>()
        );

        GroupResponseDto toDto2 = new GroupResponseDto(
                2,
                "Default Group 1",
                "Default description 1",
                avatar,
                LocalDate.now(),
                2,
                new HashSet<>(),
                new HashSet<>()
        );


        when(groupRepo.findAll()).thenReturn(List.of(group1, group2));

        List<GroupResponseDto> result = groupService.getGroups();

        assertEquals(List.of(toDto, toDto2), result);
    }

    @Test
    void testCreateGroup() {

        User admin = userBuilder.build();

        byte[] avatar = new byte[1];

        mockSecurity(admin);

        GroupRequestDto fromDto = new GroupRequestDto(
            "Test Group",
                "Test description Group",
                 avatar,
                "Test file name",
                "Test file type",
                new HashSet<>(),
                new HashSet<>()
        );

        when(userRepo.findById(1)).thenReturn(Optional.of(admin));
        when(groupRepo.save(any(Group.class))).thenAnswer(
                invocationOnMock -> {
                    Group g = invocationOnMock.getArgument(0);
                    g.setId(1);

                    return g;
                }
        );

        GroupResponseDto result = groupService.createGroup(fromDto);

        verify(userRepo).findById(1);
        verify(groupRepo).save(groupCaptor.capture());

        Group capturedGroup = groupCaptor.getValue();

        assertEquals("Test Group", capturedGroup.getName());
        assertEquals("Test description Group", capturedGroup.getDescription());
        assertArrayEquals(avatar, capturedGroup.getAvatar());
        assertEquals("Test file name", capturedGroup.getAvatarFileName());
        assertEquals("Test file type", capturedGroup.getAvatarFileType());
        assertEquals(1, capturedGroup.getAdmin().getId());
        assertEquals(Set.of(), capturedGroup.getModerators());
        assertEquals(Set.of(admin), capturedGroup.getParticipants());

        assertEquals("Test Group", result.getName());
        assertEquals("Test description Group", result.getDescription());
        assertArrayEquals(avatar, result.getAvatar());
        assertEquals(1, result.getAdminId());
        assertEquals(Set.of(), result.getModeratorsId());
        assertEquals(Set.of(1), result.getParticipantsId());

        SecurityContextHolder.clearContext();
    }

    @Test
    void testUpdateGroup() {

        int groupId = 1;

        byte[] avatar = new byte[1];

        User admin = userBuilder.build();

        User user = userBuilder.build();

        mockSecurity(admin);

        Group group = groupBuilder.
                  setAvatar(avatar).
                    setAdmin(admin).
                    build();

        GroupRequestDto fromDto = new GroupRequestDto(
                "Test Group Update",
                "Test description Group Update",
                new byte[2],
                "Test file name Update",
                "Test file type Update",
                new HashSet<>(Set.of(2)),
                new HashSet<>(Set.of(1, 2))
        );

        user.setGroups(Set.of(group));

        when(userRepo.findById(1)).thenReturn(Optional.of(admin));
        when(userRepo.findById(2)).thenReturn(Optional.of(user));
        when(groupRepo.findById(groupId)).thenReturn(Optional.of(group));

        when(groupRepo.save(any(Group.class))).thenAnswer(
                invocationOnMock -> {
                    Group g = invocationOnMock.getArgument(0);
                    g.setId(1);

                    return g;
                }
        );

        GroupResponseDto result = groupService.updateGroup(1, fromDto);

        verify(userRepo, times(2)).findById(1);
        verify(userRepo, times(2)).findById(2);
        verify(groupRepo).save(groupCaptor.capture());

        Group capturedGroup = groupCaptor.getValue();

        assertNotEquals("Test Group", capturedGroup.getName());
        assertNotEquals("Test description Group", capturedGroup.getDescription());
        assertFalse(Arrays.equals(avatar, capturedGroup.getAvatar()));
        assertNotEquals("Test file name", capturedGroup.getAvatarFileName());
        assertNotEquals("Test file type", capturedGroup.getAvatarFileType());
        assertEquals(1, capturedGroup.getAdmin().getId());
        assertEquals(Set.of(user), capturedGroup.getModerators());
        assertEquals(Set.of(admin, user), capturedGroup.getParticipants());

        assertEquals("Test Group Update", result.getName());
        assertEquals("Test description Group Update", result.getDescription());
        assertFalse(Arrays.equals(avatar, result.getAvatar()));
        assertEquals(1, result.getAdminId());
        assertEquals(Set.of(2), result.getModeratorsId());
        assertEquals(Set.of(1, 2), result.getParticipantsId());

        SecurityContextHolder.clearContext();
    }


    @Test
    void testUpdateGroupModeratorsByNonAdmin_shouldThrow() {
        User admin = userBuilder.build();
        User moderator = userBuilder.build();

        Group group = groupBuilder.
                setAdmin(admin).
                setModerators(Set.of(moderator)).
                build();

        GroupRequestDto fromDto = new GroupRequestDto(
                "Test Group",
                "Description updated",
                new byte[2],
                "fileName",
                "fileType",
                new HashSet<>(Set.of(3)),
                new HashSet<>()
        );

        when(userRepo.findById(2)).thenReturn(Optional.of(moderator));
        when(userRepo.findById(3)).thenReturn(Optional.of(new User(3,
                "NewUser",
                "pass3",
                new HashSet<>(Set.of(group)),
                new HashSet<>(),
                new HashSet<>()
                )
            )
        );
        when(groupRepo.findById(1)).thenReturn(Optional.of(group));

        mockSecurity(moderator);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                groupService.updateGroup(1, fromDto));

        assertEquals("Access denied. You are not an admin", exception.getMessage());

        SecurityContextHolder.clearContext();
    }

    @Test
    void testDeleteGroup() {

        User admin = userBuilder.build();

        mockSecurity(admin);

        Group group = groupBuilder.
                setAdmin(admin).
                build();

        when(userRepo.findById(1)).thenReturn(Optional.of(admin));
        when(groupRepo.findById(1)).thenReturn(Optional.of(group));

        groupService.deleteGroup(1);

        verify(userRepo).findById(1);
        verify(groupRepo).findById(1);


        SecurityContextHolder.clearContext();
    }

    @Test
    void testDeleteGroupByNonAdmin_shouldThrow(){
        User admin = userBuilder.build();
        User user = userBuilder.build();

        mockSecurity(user);

        Group group = groupBuilder.
                setAdmin(admin).
                setModerators(Set.of(user)).
                build();

        admin.setGroups(Set.of(group));
        user.setGroups(Set.of(group));

        when(userRepo.findById(2)).thenReturn(Optional.of(user));
        when(groupRepo.findById(1)).thenReturn(Optional.of(group));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                groupService.deleteGroup(group.getId()));

        assertEquals("Access denied", exception.getMessage());
    }

    @Test
    void testFindGroupsByName() {

        String groupName = "Test Group";
        byte[] avatar = new byte[1];

        User admin1 = userBuilder.build();
        User admin2 = userBuilder.build();

        Group group1 = groupBuilder.
                setName("Test Group").
                setDescription("Test description").
                setAvatar(avatar).
                setDateOfCreate(LocalDate.now()).
                setAdmin(admin1).
                setParticipants(Set.of(admin1)).
                build();

        Group group2 = groupBuilder.
                setName("Test Group").
                setDescription("Test description 2").
                setAvatar(avatar).
                setDateOfCreate(LocalDate.now()).
                setAdmin(admin2).
                setParticipants(Set.of(admin2)).
                build();


        GroupResponseDto toDto = new GroupResponseDto(
                1,
                "Test Group",
                "Test description",
                avatar,
                LocalDate.now(),
                1,
                new HashSet<>(),
                new HashSet<>(Set.of(admin1.getId()))
        );

        GroupResponseDto toDto2 = new GroupResponseDto(
                2,
                "Test Group",
                "Test description 2",
                avatar,
                LocalDate.now(),
                2,
                new HashSet<>(),
                new HashSet<>(Set.of(admin2.getId()))
        );


        when(groupRepo.findGroupsByName(groupName)).thenReturn(List.of(group1, group2));

        List<GroupResponseDto> result = groupService.findGroupsByName(groupName);

        assertEquals(List.of(toDto, toDto2), result);
    }

    @Test
    void testGetGroupsById() {

        byte[] avatar = new byte[1];

        int groupId = 1;

        User admin = userBuilder.build();

        Group group = groupBuilder.
                setName("Test Group").
                setDescription("Test description").
                setAvatar(avatar).
                setDateOfCreate(LocalDate.now()).
                setAdmin(admin).
                setParticipants(Set.of(admin)).
                build();


        admin.setGroups(Set.of(group));

        GroupResponseDto toDto = new GroupResponseDto(
                1,
                "Test Group",
                "Test description",
                avatar,
                LocalDate.now(),
                1,
                new HashSet<>(),
                new HashSet<>(Set.of(admin.getId()))
        );

        when(groupRepo.findById(groupId)).thenReturn(Optional.of(group));

        GroupResponseDto result = groupService.getGroupById(groupId);

        assertEquals(toDto, result);
    }
}