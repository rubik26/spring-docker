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
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupMessageServiceImplTest {

    @Mock
    private GroupMessageRepo groupMessageRepo;

    @Mock
    private GroupRepo groupRepo;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private GroupMessageServiceImpl groupMessageService;

    @Captor
    private ArgumentCaptor<GroupMessage> groupMessageCaptor;

    private void mockSecurity(User user){
        UserPrincipial userPrincipial = new UserPrincipial(user);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userPrincipial);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }
    @Test
    void testGetGroupMessagesByGroup() {
        User user = new User(1, "usertest", "pass", new HashSet<>(), new HashSet<>(), new HashSet<>());

        LocalDateTime dateOfSend = LocalDateTime.now();

        mockSecurity(user);

        Group group = new Group(
                1,
                "Test Group",
                "Description",
                new byte[1],
                "fileName",
                "fileType",
                LocalDate.now(),
                user,
                new HashSet<>(),
                new HashSet<>(Set.of(user))
        );

        GroupMessage groupMessage1 = new GroupMessage(
                1,
                "test content",
                dateOfSend,
                false,
                user,
                group
        );

        GroupMessage groupMessage2 = new GroupMessage(
                2,
                "test content2",
                dateOfSend,
                false,
                user,
                group
        );

        GroupMessageResponseDto toDto1 = new GroupMessageResponseDto(
                1,
                "test content",
                dateOfSend,
                false,
                user.getId(),
                group.getId()
        );

        GroupMessageResponseDto toDto2 = new GroupMessageResponseDto(
                2,
                "test content2",
                dateOfSend,
                false,
                user.getId(),
                group.getId()
        );

        user.setGroups(Set.of(group));

        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(groupRepo.findById(1)).thenReturn(Optional.of(group));
        when(groupMessageRepo.findGroupMessagesByGroup(group)).thenReturn(List.of(groupMessage1, groupMessage2));

        List<GroupMessageResponseDto> result = groupMessageService.getGroupMessagesByGroup(group.getId());

        assertEquals(List.of(toDto1, toDto2), result);

        SecurityContextHolder.clearContext();
    }

    @Test
    void testCreateGroupMessage() {
        User user = new User(1, "usertest", "pass", new HashSet<>(), new HashSet<>(), new HashSet<>());

        mockSecurity(user);

        Group group = new Group(
                1,
                "Test Group",
                "Description",
                new byte[1],
                "fileName",
                "fileType",
                LocalDate.now(),
                user,
                new HashSet<>(),
                new HashSet<>(Set.of(user))
        );

        GroupMessageRequestDto fromDto = new GroupMessageRequestDto("test content");

       when(userRepo.findById(1)).thenReturn(Optional.of(user));
       when(groupRepo.findById(1)).thenReturn(Optional.of(group));
       when(groupMessageRepo.save(any(GroupMessage.class))).thenAnswer(invocationOnMock -> {
           GroupMessage gm = invocationOnMock.getArgument(0);
           gm.setId(1);

           return gm;
       });

       GroupMessageResponseDto result = groupMessageService.createGroupMessage(group.getId(), fromDto);

       verify(groupMessageRepo).save(groupMessageCaptor.capture());

       GroupMessage capturedMessage = groupMessageCaptor.getValue();

       assertEquals("test content", capturedMessage.getContent());
       assertEquals(group, capturedMessage.getGroup());
       assertEquals(user, capturedMessage.getUser());
       assertEquals(false, capturedMessage.isEdited());

       assertEquals("test content", result.getContent());
       assertEquals(group.getId(), result.getGroupId());
       assertEquals(user.getId(), result.getSenderId());
       assertEquals(false, result.isEdited());

       SecurityContextHolder.clearContext();
    }

    @Test
    void testEditGroupMessage() {
        User user = new User(1, "usertest", "pass", new HashSet<>(), new HashSet<>(), new HashSet<>());


        mockSecurity(user);

        Group group = new Group(
                1,
                "Test Group",
                "Description",
                new byte[1],
                "fileName",
                "fileType",
                LocalDate.now(),
                user,
                new HashSet<>(),
                new HashSet<>(Set.of(user))
        );

        GroupMessage groupMessage1 = new GroupMessage(
                1,
                "test content",
                LocalDateTime.now(),
                false,
                user,
                group
        );

        GroupMessageRequestDto fromDto = new GroupMessageRequestDto("test content update");
        when(groupMessageRepo.findById(1)).thenReturn(Optional.of(groupMessage1));
        when(groupMessageRepo.save(any(GroupMessage.class))).thenAnswer(invocationOnMock -> {
            GroupMessage gm = invocationOnMock.getArgument(0);
            gm.setId(1);

            return gm;
        });

        GroupMessageResponseDto result = groupMessageService.editGroupMessage(groupMessage1.getId(), fromDto);

        verify(groupMessageRepo).save(groupMessageCaptor.capture());

        GroupMessage capturedMessage = groupMessageCaptor.getValue();

        assertEquals("test content update", capturedMessage.getContent());
        assertEquals(group, capturedMessage.getGroup());
        assertEquals(user, capturedMessage.getUser());
        assertEquals(true, capturedMessage.isEdited());

        assertEquals("test content update", result.getContent());
        assertEquals(group.getId(), result.getGroupId());
        assertEquals(user.getId(), result.getSenderId());
        assertEquals(true, result.isEdited());

        SecurityContextHolder.clearContext();
    }

    @Test
    void testDeleteGroupMessage() {
        User user = new User(1, "usertest", "pass", new HashSet<>(), new HashSet<>(), new HashSet<>());
        mockSecurity(user);

        Group group = new Group(
                1,
                "Test Group",
                "Description",
                new byte[1],
                "fileName",
                "fileType",
                LocalDate.now(),
                user,
                new HashSet<>(),
                new HashSet<>(Set.of(user))
        );

        GroupMessage groupMessage1 = new GroupMessage(
                1,
                "test content",
                LocalDateTime.now(),
                false,
                user,
                group
        );

        when(groupMessageRepo.findById(1)).thenReturn(Optional.of(groupMessage1));

        groupMessageService.deleteGroupMessage(1);
        verify(groupMessageRepo).deleteById(1);
    }

    @Test
    void testGetGroupMessagesByUser() {
        User user = new User(1, "usertest", "pass", new HashSet<>(), new HashSet<>(), new HashSet<>());

        LocalDateTime dateOfSend = LocalDateTime.now();

        mockSecurity(user);

        Group group = new Group(
                1,
                "Test Group",
                "Description",
                new byte[1],
                "fileName",
                "fileType",
                LocalDate.now(),
                user,
                new HashSet<>(),
                new HashSet<>(Set.of(user))
        );

        GroupMessage groupMessage1 = new GroupMessage(
                1,
                "test content",
                dateOfSend,
                false,
                user,
                group
        );

        GroupMessage groupMessage2 = new GroupMessage(
                2,
                "test content2",
                dateOfSend,
                false,
                user,
                group
        );


        GroupMessageResponseDto toDto1 = new GroupMessageResponseDto(
                1,
                "test content",
                dateOfSend,
                false,
                user.getId(),
                group.getId()
        );

        GroupMessageResponseDto toDto2 = new GroupMessageResponseDto(
                2,
                "test content2",
                dateOfSend,
                false,
                user.getId(),
                group.getId()
        );


        user.setGroups(Set.of(group));

        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(userRepo.findByUsername(user.getUsername())).thenReturn(user);
        when(groupRepo.findById(1)).thenReturn(Optional.of(group));
        when(groupMessageRepo.findMessagesByUser(user)).
                thenReturn(List.of(groupMessage1, groupMessage2));

        List<GroupMessageResponseDto> result = groupMessageService.
                getGroupMessagesByUser(group.getId(), user.getUsername());

        assertEquals(List.of(toDto1, toDto2), result);

        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetGroupMessageById() {
        User user = new User(1, "usertest", "pass", new HashSet<>(), new HashSet<>(), new HashSet<>());

        LocalDateTime dateOfSend = LocalDateTime.now();

        mockSecurity(user);

        Group group = new Group(
                1,
                "Test Group",
                "Description",
                new byte[1],
                "fileName",
                "fileType",
                LocalDate.now(),
                user,
                new HashSet<>(),
                new HashSet<>(Set.of(user))
        );

        GroupMessage groupMessage1 = new GroupMessage(
                1,
                "test content",
                dateOfSend,
                false,
                user,
                group
        );

        GroupMessageResponseDto toDto1 = new GroupMessageResponseDto(
                1,
                "test content",
                dateOfSend,
                false,
                user.getId(),
                group.getId()
        );

        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(groupRepo.findById(1)).thenReturn(Optional.of(group));
        when(groupMessageRepo.findById(1)).thenReturn(Optional.of(groupMessage1));

        GroupMessageResponseDto result = groupMessageService.getGroupMessageById(group.getId(), groupMessage1.getId());

        assertEquals(toDto1, result);

        SecurityContextHolder.clearContext();
    }
}