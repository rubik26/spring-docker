package com.darpasyan.docker.service.impl.group;

import com.darpasyan.docker.model.group.Group;
import com.darpasyan.docker.model.group.dto.GroupRequestDto;
import com.darpasyan.docker.model.group.dto.GroupResponseDto;
import com.darpasyan.docker.model.user.User;
import com.darpasyan.docker.model.user.UserPrincipial;
import com.darpasyan.docker.repo.UserRepo;
import com.darpasyan.docker.repo.group.GroupRepo;
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

        User admin1 = new User(1, "Admin1", "12345678", new HashSet<>(), new HashSet<>(), new HashSet<>());
        User admin2 = new User(2, "Admin2", "12345678", new HashSet<>(), new HashSet<>(), new HashSet<>());

        Group group1 = new Group(
            1, "Test Group", "Test description",
                new byte[1],
            "Test file name",
            "Test file type",
                LocalDate.now(),
                new User(),
                new HashSet<>(),
                new HashSet<>()
        );
        Group group2 = new Group(
                2, "Test Group2", "Test description2",
                new byte[2],
                "Test file name2",
                "Test file type2",
                LocalDate.now(),
                new User(),
                new HashSet<>(),
                new HashSet<>()
        );

        admin1.setGroups(Set.of(group1));
        admin2.setGroups(Set.of(group2));

        group1.setAdmin(admin1);
        group2.setAdmin(admin2);

        GroupResponseDto toDto = new GroupResponseDto(
                1,
                "Test Group",
                "Test description",
                new byte[1],
                LocalDate.now(),
                1,
                new HashSet<>(),
                new HashSet<>()
        );

        GroupResponseDto toDto2 = new GroupResponseDto(
                2,
                "Test Group2",
                "Test description2",
                new byte[2],
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
        User admin1 = new User(1, "Admin1", "12345678", new HashSet<>(), new HashSet<>(), new HashSet<>());

        byte[] avatar = new byte[1];

        mockSecurity(admin1);

        GroupRequestDto fromDto = new GroupRequestDto(
            "Test Group",
                "Test description Group",
                 avatar,
                "Test file name",
                "Test file type",
                new HashSet<>(),
                new HashSet<>()
        );

        when(userRepo.findById(1)).thenReturn(Optional.of(admin1));
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
        assertEquals(Set.of(admin1), capturedGroup.getParticipants());

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

        User admin1 = new User(1, "Admin1", "12345678", new HashSet<>(), new HashSet<>(), new HashSet<>());

        User user = new User(2, "New Participant", "12345678NewUserInTheGroup", new HashSet<>(), new HashSet<>(), new HashSet<>());

        mockSecurity(admin1);

        Group group = new Group(1, "Test Group", "Test description",
                avatar,
                "Test file name",
                "Test file type",
                LocalDate.now(),
                admin1,
                new HashSet<>(),
                new HashSet<>(Set.of(admin1))
        );

        admin1.setGroups(Set.of(group));

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

        when(userRepo.findById(1)).thenReturn(Optional.of(admin1));
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
        assertEquals(Set.of(admin1, user), capturedGroup.getParticipants());

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

        User admin = new User(1, "Admin", "pass", new HashSet<>(), new HashSet<>(), new HashSet<>());
        User moderator = new User(2, "Moderator", "pass2", new HashSet<>(), new HashSet<>(), new HashSet<>());

        Group group = new Group(
                1,
                "Test Group",
                "Description",
                new byte[1],
                "fileName",
                "fileType",
                LocalDate.now(),
                admin,
                new HashSet<>(Set.of(moderator)),
                new HashSet<>()
        );

        admin.setGroups(Set.of(group));
        moderator.setGroups(Set.of(group));

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

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            groupService.updateGroup(1, fromDto);
        });

        assertEquals("Access denied. You are not an admin", exception.getMessage());

        SecurityContextHolder.clearContext();
    }

    @Test
    void testDeleteGroup() {
        User admin = new User(1, "Admin", "pass", new HashSet<>(), new HashSet<>(), new HashSet<>());

        mockSecurity(admin);

        Group group = new Group(
                1,
                "Test Group",
                "Description",
                new byte[1],
                "fileName",
                "fileType",
                LocalDate.now(),
                admin,
                new HashSet<>(),
                new HashSet<>()
        );

        when(userRepo.findById(1)).thenReturn(Optional.of(admin));
        when(groupRepo.findById(1)).thenReturn(Optional.of(group));

        groupService.deleteGroup(1);

        verify(userRepo).findById(1);
        verify(groupRepo).findById(1);


        SecurityContextHolder.clearContext();
    }

    @Test
    void testFindGroupsByName() {
        String groupName = "Test Group";

        User admin1 = new User(1, "Admin1", "12345678", new HashSet<>(), new HashSet<>(), new HashSet<>());
        User admin2 = new User(2, "Admin2", "12345678", new HashSet<>(), new HashSet<>(), new HashSet<>());

        Group group1 = new Group(
                1, "Test Group", "Test description",
                new byte[1],
                "Test file name",
                "Test file type",
                LocalDate.now(),
                new User(),
                new HashSet<>(),
                new HashSet<>()
        );
        Group group2 = new Group(
                2, "Test Group", "Test description2",
                new byte[2],
                "Test file name2",
                "Test file type2",
                LocalDate.now(),
                new User(),
                new HashSet<>(),
                new HashSet<>()
        );

        admin1.setGroups(Set.of(group1));
        admin2.setGroups(Set.of(group2));

        group1.setAdmin(admin1);
        group2.setAdmin(admin2);

        GroupResponseDto toDto = new GroupResponseDto(
                1,
                "Test Group",
                "Test description",
                new byte[1],
                LocalDate.now(),
                1,
                new HashSet<>(),
                new HashSet<>()
        );

        GroupResponseDto toDto2 = new GroupResponseDto(
                2,
                "Test Group",
                "Test description2",
                new byte[2],
                LocalDate.now(),
                2,
                new HashSet<>(),
                new HashSet<>()
        );


        when(groupRepo.findGroupsByName(groupName)).thenReturn(List.of(group1, group2));

        List<GroupResponseDto> result = groupService.findGroupsByName(groupName);

        assertEquals(List.of(toDto, toDto2), result);
    }

    @Test
    void testGetGroupsById() {
        int groupId = 1;

        User admin1 = new User(1, "Admin1", "12345678", new HashSet<>(), new HashSet<>(), new HashSet<>());

        Group group1 = new Group(
                1, "Test Group", "Test description",
                new byte[1],
                "Test file name",
                "Test file type",
                LocalDate.now(),
                new User(),
                new HashSet<>(),
                new HashSet<>()
        );

        admin1.setGroups(Set.of(group1));
        group1.setAdmin(admin1);

        GroupResponseDto toDto = new GroupResponseDto(
                1,
                "Test Group",
                "Test description",
                new byte[1],
                LocalDate.now(),
                1,
                new HashSet<>(),
                new HashSet<>()
        );

        when(groupRepo.findById(groupId)).thenReturn(Optional.of(group1));

        GroupResponseDto result = groupService.getGroupById(groupId);

        assertEquals(toDto, result);
    }
}